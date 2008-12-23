/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * --------------------------------------------------------------------
 *
 * CVS Revision $Revision: 1.9 $
 * Last modified on $Date: 2005/05/02 00:04:13 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.hourglass.plugins.Plugin;
import net.sourceforge.hourglass.plugins.PluginManager;
import javax.swing.event.ListDataEvent;
import java.util.List;
import javax.swing.event.ListDataListener;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;

/**
 * A dialog for configuring preferences.
 *
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public class PreferencesDialog
 
  extends JDialog
  implements ListSelectionListener {

  public PreferencesDialog(Frame owner, PreferencePanel[] panels) {
    super(owner, "Preferences", true);
    _owner = owner;
    _staticPanels = panels;
    _pluginPanels = Collections.EMPTY_LIST;
    initializeComponents();
    setSize(new Dimension(640, 480));
    
    //setResizable(false);
  }


  private void initializeComponents() {
    getContentPane().add(getSplitPane(), BorderLayout.CENTER);
  }


  private JSplitPane getSplitPane() {
    if (_splitPane == null) {
      _splitPane = new JSplitPane
        (JSplitPane.HORIZONTAL_SPLIT, getNavigationPane(), getRightPanel());
    }
    return _splitPane;
  }


  private JPanel getRightPanel() {
    if (_rightPanel == null) {
      _rightPanel = new JPanel(new BorderLayout());
      JScrollPane jsp = new JScrollPane(getConfigPanel());
      _rightPanel.add(jsp, BorderLayout.CENTER);
      _rightPanel.add(getButtonPanel(), BorderLayout.SOUTH);
    }
    return _rightPanel;
  }


  private JPanel getButtonPanel() {
    if (_buttonPanel == null) {
      _buttonPanel = new JPanel();
      _buttonPanel.add(new JButton(getOkAction()));
      _buttonPanel.add(new JButton(getCancelAction()));
    }
    return _buttonPanel;
  }


  private Action getCancelAction() {
    if (_cancelAction == null) {
      _cancelAction = new AbstractAction() {
          {
            putValue(NAME, "Cancel");
            putValue(MNEMONIC_KEY, new Integer('c'));
          }
          
          public void actionPerformed(ActionEvent ae) {
            setVisible(false);
          }
        };
    }
    return _cancelAction;
  }


  private Action getOkAction() {
    if (_okAction == null) {
      _okAction = new AbstractAction() {
          {
            putValue(NAME, "OK");
            putValue(MNEMONIC_KEY, new Integer('o'));
          }
          
          public void actionPerformed(ActionEvent ae) {
            Iterator i = getAllPanels().iterator();
            while (i.hasNext()) {
              PreferencePanel eachPanel = (PreferencePanel) i.next();
              eachPanel.commitSettings();
              if (eachPanel.isRestartRequired()) {
                _isRestartRequired = true;
              }
            }
            setVisible(false);
          }
        };
    }
    return _okAction;
  }

  private void addPanelToDialog(PreferencePanel panel) {
    getConfigPanel().add(panel, panel.getClass().getName());
  }

  private void removePanelFromDialog(PreferencePanel panel) {
    getConfigPanel().remove(panel);
  }


  private JPanel getConfigPanel() {
    if (_configPanel == null) {
      _configPanel = new JPanel();
      _configPanel.setLayout(getConfigPanelLayout());
      _configPanel.add(new JPanel(), "HOME");

      for (int i = 0; i < _staticPanels.length; ++i) {
        addPanelToDialog(_staticPanels[i]);
      }
    }
    return _configPanel;
  }


  private CardLayout getConfigPanelLayout() {
    if (_configPanelLayout == null) {
      _configPanelLayout = new CardLayout();
    }
    return _configPanelLayout;
  }


  private JComponent getNavigationPane() {
    if (_navScrollPane == null) {
      _navScrollPane = new JScrollPane(getNavigationList());
      _navScrollPane.setPreferredSize(new Dimension(150, 0));
    }
    return _navScrollPane;
  }


  private JList getNavigationList() {
    if (_navList == null) {
      _navList = new JList(new PreferencesListModel());
      _navList.setCellRenderer(new IconCellRenderer());
      _navList.addListSelectionListener(this);       
    }
    return _navList;
  }

  public void setVisible(boolean visible) {
  	if (visible) {
	    _isRestartRequired = false;
	    setLocationRelativeTo(_owner);
	    Iterator i = getAllPanels().iterator();
	    while (i.hasNext()) {
	      PreferencePanel eachPanel = (PreferencePanel) i.next();
	      eachPanel.initializeSettings();
	    }
  	}
    super.setVisible(visible);
  }


  public void valueChanged(ListSelectionEvent e) {
    Object selectedValue = getNavigationList().getSelectedValue();
    if (selectedValue != null) {
      String name = selectedValue.getClass().getName();
      getConfigPanelLayout().show(getConfigPanel(), name);
    }
  }

  public Collection getAllPanels() {
    List result = new ArrayList(_staticPanels.length + _pluginPanels.size());
    result.addAll(Arrays.asList(_staticPanels));
    result.addAll(_pluginPanels);
    return result;
  }



  public boolean isRestartRequired() {
    return _isRestartRequired;
  }

  class PreferencesListModel extends AbstractListModel implements ListDataListener {

    public PreferencesListModel() {
      loadPluginPanels();

      // TODO [MGT, 01 May 2004]: Memory Leak!
      PluginManager.getInstance().addListDataListener(this);
    }

    // TODO [MGT, 09 May 2004]: This might clear non-committed settings.
    private void loadPluginPanels() {
      Iterator i = _pluginPanels.iterator();
      while (i.hasNext()) {
        removePanelFromDialog((PreferencePanel) i.next());
      }

      _pluginPanels = new ArrayList();
      
      i = PluginManager.getInstance().getPlugins().iterator();
      while (i.hasNext()) {
        Plugin p = (Plugin) i.next();
        if (p.getPreferencePanel() != null) {
          _pluginPanels.add(p.getPreferencePanel());
          addPanelToDialog(p.getPreferencePanel());
        }
      }

      fireContentsChanged(this, 0, getSize() - 1);
    }

    public int getSize() {
      return _staticPanels.length + _pluginPanels.size();
    }

    public Object getElementAt(int index) {
      if (index < _staticPanels.length) {
        return _staticPanels[index];
      }
      else {
        int pluginIndex = index - _staticPanels.length;
        return _pluginPanels.get(pluginIndex);
      }
    }

    public void contentsChanged(ListDataEvent e) {
      loadPluginPanels();
    }

    public void intervalAdded(ListDataEvent e) {
      loadPluginPanels();
    }
    
    public void intervalRemoved(ListDataEvent e) {
      loadPluginPanels();
    }

  }


  class IconCellRenderer extends JLabel implements ListCellRenderer {

    public Component getListCellRendererComponent
      (JList list, Object value, int index,               
       boolean isSelected, boolean cellHasFocus) {
       
      PreferencePanel ppanel = (PreferencePanel) value;
      setText(ppanel.getDisplayName());
      setIcon(ppanel.getIcon());

      if (isSelected) {
        this.setBackground(list.getSelectionBackground());
        this.setForeground(list.getSelectionForeground());
      }
      else {
        this.setBackground(list.getBackground());
        this.setForeground(list.getForeground());
      }
      this.setEnabled(list.isEnabled());
      this.setFont(list.getFont());
      this.setOpaque(true);
      return this;
    }
  }


  private JScrollPane _navScrollPane;
  private JList _navList;
  private JPanel _configPanel;
  private JPanel _rightPanel;
  private JPanel _buttonPanel;
  private Frame _owner;
  private PreferencePanel[] _staticPanels;
  private List _pluginPanels;
  private JSplitPane _splitPane;
  private CardLayout _configPanelLayout;
  private Action _cancelAction;
  private Action _okAction;
  private boolean _isRestartRequired;
}
