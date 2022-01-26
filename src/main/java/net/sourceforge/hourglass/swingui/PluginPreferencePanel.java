/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Copyright (C) 2009 Eric Lavarde <ewl@users.sourceforge.net>
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
 * CVS Revision $Revision: 1.2 $
 * Last modified on $Date: 2004/05/02 04:04:58 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import net.sourceforge.hourglass.plugins.Plugin;
import net.sourceforge.hourglass.plugins.PluginManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.DefaultListCellRenderer;
import java.util.prefs.BackingStoreException;

/**
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public class PluginPreferencePanel 
    extends PreferencePanel {

    private JList m_list;

    public PluginPreferencePanel() {
        super();
        initializeComponents();
    }
  
    public String getDisplayName() {
        return Utilities.getInstance().getString(Strings.PREFS_PLUGINS);
    }


    public String getLongName() {
        return Utilities.getInstance().getString(Strings.PREFS_PLUGINS_LONG);
    }


    public void commitSettings() {
    }

    public void initializeSettings() { 
    }


    private void initializeComponents() {
        getContentPane().add(createPluginListPanel(), BorderLayout.CENTER);
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createPluginListPanel() {
        JPanel result = new JPanel(new BorderLayout());
        JScrollPane jsp = new JScrollPane(getPluginList());
        result.add(jsp);
        return result;
    }

    private JList getPluginList() {
        if (m_list == null) {
            m_list = new JList(PluginManager.getInstance());
            m_list.setCellRenderer(new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent
                        (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        
                        Plugin plugin = (Plugin) value;
                        return super.getListCellRendererComponent
                            (list, plugin.getName(), index, isSelected, cellHasFocus);
                    }
                });
        }
        return m_list;
    }

    private JPanel createButtonPanel() {
        JPanel result = new JPanel();
        result.add(new JButton(createAddAction()));
        result.add(new JButton(createRemoveAction()));
        return result;
    }

    private Action createRemoveAction() {
        return new AbstractAction(
				gu().getString(Strings.PREFS_PLUGINS_REMOVE)) {
                public void actionPerformed(ActionEvent ae) {
                    Plugin plugin = (Plugin) getPluginList().getSelectedValue();
                    if (plugin != null) {
                        try {
                            PluginManager.getInstance().removePlugin(plugin);
                        }
                        catch (BackingStoreException e) {
                            // TODO [MGT, 01 May 2004]: Show error.
                            e.printStackTrace();
                        }
                    }
                }
            };
    }

    private Action createAddAction() {
        return new AbstractAction(
				gu().getString(Strings.PREFS_PLUGINS_ADD)) {
                public void actionPerformed(ActionEvent ae) {
                    JFileChooser chooser = new JFileChooser();
                    int returnValue = chooser.showOpenDialog(ClientState.getInstance().getSummaryFrame());
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        try {
                            PluginManager.getInstance().addPlugin(file);
                        }
                        catch (Exception e) {
                            // TODO [MGT, 21 Apr 2004]: Show error
                            e.printStackTrace();
                        }
                    }
                }
            };
    }

    public boolean isRestartRequired() {
        return false;
    }

  private static Utilities gu() {
	  return Utilities.getInstance();
  }
}
