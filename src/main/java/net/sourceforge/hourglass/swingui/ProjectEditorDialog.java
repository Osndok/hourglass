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
 * Last modified on $Date: 2005/05/07 20:22:11 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.hourglass.framework.IllegalParentException;
import net.sourceforge.hourglass.framework.MutableProject;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.plugins.PluginManager;
import net.sourceforge.hourglass.plugins.PluginProjectEditorPanel;

/**
 * Dialog for editing project meta-information (name, desc).
 *
 * @author Mike Grant
 */
public class ProjectEditorDialog extends JDialog {


  /**
   * Constructs a dialog with the data from the given project.
   *
   * @param owner the parent frame
   * @param title the dialog title
   * @param prj the project with which to populate the dialog, or null
   *            for a blank dialog
   */
  public ProjectEditorDialog(Frame owner, String title, Project prj) {
    super(owner, title, true);
    _initialProject = prj;
    _utilities = Utilities.getInstance();
    initializeComponents();
    pack();
    setResizable(false);
    getRootPane().setDefaultButton(getOkButton());
    setLocationRelativeTo(owner);
  }

  /**
   * Constructs a new blank dialog.
   * 
   * Equivalent to ProjectEditorDialog(owner, title, null)
   */
  public ProjectEditorDialog(Frame owner, String title) {
    this(owner, title, null);
  }


  private void initializeComponents() {
  	JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(getInformationPanel(), BorderLayout.CENTER);
    getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
    if (_initialProject != null) {
      selectCurrentParent();
    }
    
    List pluginPanels = PluginManager.getInstance().getProjectEditorPanels();
    if (pluginPanels.isEmpty()) {
    	getContentPane().add(mainPanel, BorderLayout.CENTER);
    }
    else {
    	// Add the plugin panels in a JTabbedPane.
    	JTabbedPane pane = new JTabbedPane();
    	pane.add("Core Options", mainPanel);
    	Iterator i = pluginPanels.iterator();
    	while (i.hasNext()) {
    		PluginProjectEditorPanel eachPanel = (PluginProjectEditorPanel) i.next();
    		eachPanel.initialize(_initialProject);
    		pane.add(eachPanel.getTitle(), eachPanel);
    	}
    	getContentPane().add(pane, BorderLayout.CENTER);
    }
  }


  public Project getParentProject() {
    TreePath selectionPath = getParentTree().getSelectionPath();
    if (selectionPath == null) {
      return null;
    }
    else {
      return (Project) selectionPath.getLastPathComponent();
    }
  }


  /**
   * Sets the parent selection.
   */
  public void setParentProject(Project parent, ProjectGroup group) {
    TreePath tp = new TreePath
      (Utilities.getInstance().createPathTo(parent, group).toArray());

    getParentTree().setSelectionPath(tp);
    getParentTree().scrollPathToVisible(tp);
  }


  private void selectCurrentParent() {
    ProjectGroup group = ClientState.getInstance().getProjectGroup();
    Project parent = 
      group.getParent(ClientState.getInstance().getSelectedProject());

    setParentProject(parent, group);
  }


  private JPanel getInformationPanel() {
    if (_informationPanel == null) {
      _informationPanel = new JPanel(new GridBagLayout());

      GridBagConstraints c_lbl = new GridBagConstraints();
      GridBagConstraints c_fld = new GridBagConstraints();
      c_lbl.insets = new Insets(2, 2, 2, 5);
      c_lbl.anchor = GridBagConstraints.NORTHEAST;
      c_lbl.weightx = 0;
      c_fld.gridwidth = GridBagConstraints.REMAINDER;
      c_fld.weightx = 1;
      c_fld.fill = GridBagConstraints.HORIZONTAL;
      c_fld.insets = new Insets(0, 0, 15, 0);

      _informationPanel.add(
        new JLabel(_utilities.getFieldLabel(Strings.PROJECT_NAME)),
        c_lbl.clone());
      _informationPanel.add(getNameField(), c_fld.clone());
      
      _informationPanel.add(
        new JLabel(_utilities.getFieldLabel(Strings.PROJECT_DESCRIPTION)),
        c_lbl.clone());
      _informationPanel.add(getTextPane(), c_fld.clone());
      
      _informationPanel.add(
        new JLabel(_utilities.getFieldLabel(Strings.PROJECT_PARENT)),
        c_lbl.clone());
      _informationPanel.add(getParentPane(), c_fld.clone());

      _informationPanel.setBorder
        (BorderFactory.createEmptyBorder(20, 20, 5, 20));
    }
    return _informationPanel;
  }

  
  private JComponent getParentPane() {
    if (_parentPane == null) {
      JScrollPane result = new JScrollPane(getParentTree());
      result.setPreferredSize(new Dimension(250, 100));
      _parentPane = result;
    }
    return _parentPane;
  }


  private JTree getParentTree() {
    if (_parentTree == null) {
      ProjectGroupTreeModel model = new ProjectGroupTreeModel
        (ClientState.getInstance().getProjectGroup());
      _parentTree = new JTree(model);
      _parentTree.setCellRenderer(new ProjectTreeCellRenderer());
      _parentTree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
      _parentTree.setExpandsSelectedPaths(true);
      ToolTipManager.sharedInstance().registerComponent(_parentTree);
    }
    return _parentTree;
  }


  private JTextField getNameField() {
    if (_nameField == null) {
      _nameField = new JTextField();
      if (_initialProject != null) {
        _nameField.setText(_initialProject.getName());
      }
    }
    return _nameField;
  }


  private JComponent getTextPane() {
    if (_textPane == null) {
      _textPane = new JScrollPane(getTextArea());
      _textPane.setPreferredSize(new Dimension(250, 70));
    }
    return _textPane;
  }


  private JTextArea getTextArea() {
    if (_textArea == null) {
      _textArea = new JTextArea();
      _textArea.setLineWrap(true);
      _textArea.setWrapStyleWord(true);
      if (_initialProject != null) {
        _textArea.setText(_initialProject.getDescription());
        _textArea.setCaretPosition(0);
      }
    }
    return _textArea;
  }


  private JPanel getButtonPanel() {
    if (_buttonPanel == null) {
      _buttonPanel = new JPanel();
      _buttonPanel.add(getOkButton());
      _buttonPanel.add(new JButton(getCancelAction()));
    }
    return _buttonPanel;
  }


  private JButton getOkButton() {
    if (_okButton == null) {
      _okButton = new JButton(getOkAction());
    }
    return _okButton;
  }


  /**
   * Returns the status of the dialog after it's closed.
   *
   * @return OK or CANCEL depending on which button was pressed.
   */
  public int getStatus() {
    return _status;
  }


  /**
   * Returns the new name that was given to the project.
   */
  public String getName() {
    return _name;
  }


  /**
   * Returns the new description of the project.
   */
  public String getDescription() {
    return _desc;
  }


  private Action getOkAction() {
    if (_okAction == null) {
      _okAction = new AbstractAction() {
          {
            putValue(NAME, _utilities.getString(Strings.OK));
          }
          public void actionPerformed(ActionEvent ae) {
            _status = OK;
            _name = getNameField().getText();
            _desc = getTextArea().getText();
            setVisible(false);
          }
        };
    }
    return _okAction;
  }


  private Action getCancelAction() {
    if (_cancelAction == null) {
      _cancelAction = new AbstractAction() {
          {
            putValue(NAME, _utilities.getString(Strings.CANCEL));
          }
          public void actionPerformed(ActionEvent ae) {
            _status = CANCEL;
            
            Iterator i = PluginManager.getInstance().getProjectEditorPanels().iterator();
            while (i.hasNext()) {
            	PluginProjectEditorPanel eachPanel = (PluginProjectEditorPanel) i.next();
            	eachPanel.cancelChanges(_initialProject);
            }
            
            setVisible(false);
          }
        };
    }
    return _cancelAction;
  }
  
  public void createProject(ProjectGroup group, MutableProject prj) {
    prj.setName(getName());
    prj.setDescription(getDescription());
    group.addProject(prj, getParentProject());
    persistPluginChanges(prj);
  }
  
  public void persistProject(ProjectGroup group, Project prj) throws IllegalParentException {
    group.setParent(prj, getParentProject());
    group.setProjectName(prj, getName());
    group.setProjectDescription(prj, getDescription());
    persistPluginChanges(prj);
  }


  private void persistPluginChanges(Project prj) {
	Iterator i = PluginManager.getInstance().getProjectEditorPanels().iterator();
	while (i.hasNext()) {
		PluginProjectEditorPanel eachPanel = (PluginProjectEditorPanel) i.next();
		eachPanel.persistChanges(prj);
	}
}


private JButton _okButton;
  private JPanel _buttonPanel;
  private JPanel _informationPanel;
  private JScrollPane _textPane;
  private JTextArea _textArea;
  private JTextField _nameField;
  private JComponent _parentPane;
  private JTree _parentTree;

  private Action _okAction;
  private Action _cancelAction;

  private int _status;
  private String _name;
  private String _desc;

  private Project _initialProject;
  private Utilities _utilities;
  
  public static final int CANCEL = 0;
  public static final int OK = 1;
}
