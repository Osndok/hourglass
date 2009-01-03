/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Portions Copyright (C) 2003 Neil Thier <nthier@alumni.uwaterloo.ca>
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
 * CVS Revision $Revision: 1.41 $
 * Last modified on $Date: 2008/11/13 21:08:51 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.hourglass.framework.IllegalParentException;
import net.sourceforge.hourglass.framework.MutableProject;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.TimeSpan;
import net.sourceforge.hourglass.plugins.PluginManager;
import net.sourceforge.hourglass.reports.timecard.TimecardReportPanel;

import org.apache.log4j.Logger;
import org.jfree.ui.DateChooserPanel;
import org.xml.sax.SAXException;

/**
 * Main application frame that has summary information and basic controls.
 *
 * @author Mike Grant
 */
public class SummaryFrame 
  extends
    JFrame 
  implements 
    ClientStateListener,
    TimerListener,
    SwingConstants,
    TreeSelectionListener {

	private static final long serialVersionUID = 1L;

	public SummaryFrame() {
		super(gu().getString(Strings.APPLICATION_TITLE));
		m_isEditHistMode = false;
		setName(Strings.SUMMARY_FRAME);
		initializeComponents();
		pack();
		initializeShutdownHook();
		initializeData();
		getClientState().getTimer().addTimerListener(this);
		getClientState().addClientStateListener(this);
		getProjectTree().addTreeSelectionListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

  private void initializeComponents() {
    initializeIcons();
    setIconImage(((ImageIcon) _iconClock).getImage());
    getContentPane().add(getMainPanel(), BorderLayout.CENTER);
    getContentPane().add(getStatusPanel(), BorderLayout.SOUTH);
    setJMenuBar(getMainMenuBar());
    initializeWindowListener();
  }


  private void initializeIcons() {
    _iconClock = gu().getIconFromResourceName
      ("net/sourceforge/hourglass/images/icons/Clock.png");
    _iconClockGo = gu().getIconFromResourceName
      ("net/sourceforge/hourglass/images/icons/ClockGo.png");
    _iconClockStop = gu().getIconFromResourceName
      ("net/sourceforge/hourglass/images/icons/ClockStop.png");
  }


  private void initializeData() {
    try {
      loadProjectData();
    } catch (SAXException se) {
      getLogger().fatal("Project XML file is corrupt. " + se.getMessage());
    } catch (IOException ioe) {
      getLogger().fatal("Error opening Project XML file. " + ioe.getMessage());
    }
  }

  private void initializeShutdownHook() {
	  // add shutdown hook
	  FrameShutdownHook shutdownHook = new FrameShutdownHook();
	  Runtime.getRuntime().addShutdownHook(shutdownHook);
  }

  private class FrameShutdownHook extends Thread {
      public void run() {
          doShutdown();
      }
  }

  private JPanel getMainPanel() {
    if (_mainPanel == null) {
      _mainPanel = new JPanel(new BorderLayout());
      _mainPanel.add(getLeftPanel(), BorderLayout.CENTER);
      _mainPanel.add(getActivityPanel(), BorderLayout.EAST);
    }
    return _mainPanel;
  }


  private JPanel getStatusPanel() {
    if (_statusPanel == null) {
      _statusPanel = new JPanel(new BorderLayout());
      _statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
      _statusPanel.add(getCurrentTimeLabel(), BorderLayout.CENTER);
      updateCurrentTimeLabel();
    }
    return _statusPanel;
  }


  private JLabel getCurrentTimeLabel() {
    if (_currentTimeLabel == null) {
      _currentTimeLabel = new JLabel("", LEFT);
    }
    return _currentTimeLabel;
  }


  private void updateCurrentTimeLabel() {
    DateFormat df = DateFormat.getDateTimeInstance
      (DateFormat.LONG, DateFormat.SHORT);
    getCurrentTimeLabel().setText
      (df.format(new Date()) + " " + TimeZone.getDefault().getID());
  }


  private void initializeWindowListener() {
    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent we) {
          if (!doClose()) {
          	setVisible(true);
          }
        }
      });
  }


  private JMenuBar getMainMenuBar() {
    if (_menuBar == null) {
      _menuBar = new JMenuBar();
      _menuBar.add(gu().createMenuFromResource
                   (Strings.MENU_FILE, new Action[] {
                     getSaveNowAction(),
                     getExitAction(),
                   }));
      _menuBar.add(gu().createMenuFromResource
                   (Strings.MENU_EDIT, new Action[] {
                     getPreferencesAction()
                   }));
      _menuBar.add(gu().createMenuFromResource
                   (Strings.MENU_PROJECT, new Action[] {
                     getAddProjectAction(),
                     getEditProjectAction(),
                     getRemoveProjectAction()
                   }));
      _menuBar.add(getPluginMenu());
      _menuBar.add(gu().createMenuFromResource
                   (Strings.MENU_REPORTS, new Action[] {
                     getTimecardReportAction(),
                   }));
      _menuBar.add(gu().createMenuFromResource
                   (Strings.MENU_HELP, new Action[] {
                     getHelpAboutAction()
                   }));
    }

    return _menuBar;
  }

  private static Utilities gu() {
	  return Utilities.getInstance();
  }

  private ActivityPanel getActivityPanel() {
    if (_activityPanel == null) {
      _activityPanel = new ActivityPanel();
    }
    return _activityPanel;
  }

  
  /**
   * Brings the application down cleanly using the
   * {@link #doShutdown() doShutdown} method, after having asked
   * the user for confirmation, should a timer be running.
   * @return true if the application can be properly closed, false
   *         if the user refused to close or the cleanup was unsuccessful.
   * @see #doShutdown() doShutdown
   */
  private boolean doClose() {
	  if (getClientState().isRunning() && 
			  gu().showConfirmation(this, Strings.CONFIRM_EXIT, null, JOptionPane.YES_NO_OPTION)
			  != JOptionPane.YES_OPTION) {
		  return false;
	  } else {
		  return doShutdown();
	  }
  }

  /**
   * Brings the application down without user interaction, stopping
   * a potentially running timer, writing data and doing the last
   * cleanup.
   * @return true if the application can be properly closed, false
   *         if the cleanup was unsuccessful.
   * @see #doClose() doClose
   */
  private boolean doShutdown() {
	  if (getClientState().isRunning()) {
		  getClientState().stopCurrentTimeSpan();
	  }
	  try {
		  writeProjectData();
		  getClientState().stopCleanly();
		  SummaryFrame.this.dispose();
		  return true;
	  } 
	  catch (IOException ioe) {
		  getLogger().fatal(ioe.getMessage());
		  return false;
	  } 
	  catch (SAXException saxe) {
		  getLogger().fatal(saxe.getMessage());
		  return false;
	  }
  }

private JPanel getLeftPanel() {
    if (_leftPanel == null) {
      _leftPanel = new JPanel();
      _leftPanel.setLayout(new GridBagLayout());

      GridBagConstraints c_prj = new GridBagConstraints();
      c_prj.gridwidth = GridBagConstraints.REMAINDER;
      c_prj.fill = GridBagConstraints.BOTH;
      c_prj.insets = new Insets(2, 2, 2, 2);
      c_prj.weightx = 1;
      c_prj.weighty = 1;
      
      GridBagConstraints c_lower = (GridBagConstraints) c_prj.clone();
      c_lower.weighty = 0;

      _leftPanel.add(getProjectPanel(), c_prj.clone());
      _leftPanel.add(getLowerPanel(), c_lower.clone());

    }
    return _leftPanel;
  }


  private JButton getToggleButton() {
    if (_toggleButton == null) {
      _toggleButton = new JButton(gu().getString(Strings.START), _iconClockGo);
      _toggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
              getClientState().toggleTimer();
              updateToggleButtonFromClientState();
            }
        });

      _toggleButton.setEnabled(false);
      _toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    return _toggleButton;
  }
  
	private void updateToggleButtonFromClientState() {
		if (getClientState().isRunning()) {
			getToggleButton().setText(gu().getString(Strings.STOP));
			getRunningProjectLabel().setText(getClientState().getRunningProject().getName());
			getToggleButton().setIcon(_iconClockStop);
		} else {
			getToggleButton().setText(gu().getString(Strings.START));
			getRunningProjectLabel().setText("");
			getToggleButton().setIcon(_iconClockGo);
		}
	}


	public JPanel getProjectPanel() {
    if (_projectPanel == null) {
      _projectPanel = new JPanel(new BorderLayout());
      _projectPanel.setBorder(BorderFactory.createTitledBorder(gu().getString(Strings.PROJECTS)));
      JScrollPane jsp = new JScrollPane(getProjectTree());
      jsp.setPreferredSize(new Dimension(200, 150));
      _projectPanel.add(jsp, BorderLayout.CENTER);
    }
    return _projectPanel;
  }


  public JTree getProjectTree() {
    if (_projectTree == null) {
      ProjectGroupTreeModel model = 
        new ProjectGroupTreeModel(getClientState().getProjectGroup());
      getClientState().addClientStateListener(model);
      _projectTree = new JTree(model);
      _projectTree.setRootVisible(false);

      _projectTree.setCellRenderer(new ProjectTreeCellRenderer());
      _projectTree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
      ToolTipManager.sharedInstance().registerComponent(_projectTree);
      _projectTree.addMouseListener(getPopupListener());
      _projectTree.setExpandsSelectedPaths(true);
    }
    return _projectTree;
  }

  
  public JPanel getLowerPanel() {
      if (_lowerPanel == null) {
          _lowerPanel = new JPanel(getLowerPanelLayout());
          _lowerPanel.add(getDetailPanel(), DETAIL_PANEL_KEY);
          _lowerPanel.add(getCalendarPanel(), CALENDAR_PANEL_KEY);
      }
      return _lowerPanel;
  }
  
  public DateChooserPanel getCalendarPanel() {
      if (_calendarPanel == null) {
          _calendarPanel = new DateChooserPanel() {
            public void actionPerformed(ActionEvent event) {
                super.actionPerformed(event);
                if (SummaryFrame.this != null) {
                    refreshTimePlates();
                }
            }
          };
      }   

      return _calendarPanel;
  }

  private CardLayout getLowerPanelLayout() {
      if (_lowerPanelLayout == null) {
          _lowerPanelLayout = new CardLayout();
      }
      return _lowerPanelLayout;
  }

  public JPanel getDetailPanel() {
        if (_detailPanel == null) {
            _detailPanel = new JPanel(new BorderLayout());
            
            // The details
            DetailPanel detPanel = new DetailPanel();
            detPanel.setBorder(BorderFactory.createTitledBorder(gu().getString(
                    Strings.DETAIL_PANEL_HEADER)));
            getClientState().getTimer().addTimerListener(detPanel);
            getClientState().addClientStateListener(detPanel);
            getProjectGroup().addProjectGroupListener(detPanel);
            _detailPanel.add(detPanel, BorderLayout.CENTER);
            
            _detailPanel.add(createToggleButtonPanel(), BorderLayout.SOUTH);
        }
        return _detailPanel;
    }

	private JPanel createToggleButtonPanel() {
		JPanel toggleButtonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.insets = new Insets(1, 0, 4, 0);
		toggleButtonPanel.add(getRunningProjectLabel(), c.clone());
		c.insets = new Insets(0, 0, 0, 0);
		toggleButtonPanel.add(getToggleButton(), c.clone());
		return toggleButtonPanel;
	}

	private JLabel getRunningProjectLabel() {
		if (m_runningProjectLabel == null) {
			m_runningProjectLabel = new JLabel();
			m_runningProjectLabel.setForeground(Color.BLUE);
			m_runningProjectLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		}
		return m_runningProjectLabel;
	}

  private Action getSaveNowAction() {
    if (_saveNowAction == null) {
      _saveNowAction = new AbstractAction() {
        {
          putValue(NAME, gu().getString(Strings.SAVE_NOW));
          putValue(MNEMONIC_KEY, gu().getMnemonicAsInt(Strings.SAVE_NOW));
        }
        public void actionPerformed(ActionEvent ae) {
          try {
            writeProjectData();
          }
          catch (SAXException e) {
            // TODO Auto-generated catch block
            getLogger().error(e);
          }
          catch (IOException e) {
            // TODO Auto-generated catch block
            getLogger().error(e);
          }
        }
      };
    }
    return _saveNowAction;
  }


  private Action getExitAction() {
    if (_exitAction == null) {
      _exitAction = new AbstractAction() {
          {
            putValue(NAME, gu().getString(Strings.EXIT));
            putValue(MNEMONIC_KEY, gu().getMnemonicAsInt(Strings.EXIT));
          }
          public void actionPerformed(ActionEvent ae) {
            doClose();
          }
        };
    }
    return _exitAction;
  }


  private Action getAddProjectAction() {
    if (_addProjectAction == null) {
      _addProjectAction = new AbstractAction() {
          {
            putValue(NAME, gu().getString(Strings.ADD_PROJECT));
            putValue(MNEMONIC_KEY, gu().getMnemonicAsInt(Strings.ADD_PROJECT));
          }
          public void actionPerformed(ActionEvent ae) {
	    addNewProject();
          }
        };
    }
    return _addProjectAction;
  }


  private Action getRemoveProjectAction() {
    if (_removeProjectAction == null) {
      _removeProjectAction = new AbstractAction() {
          {
            putValue(NAME, gu().getString(Strings.REMOVE_PROJECT));
            putValue(MNEMONIC_KEY, gu().getMnemonicAsInt(Strings.REMOVE_PROJECT));
          }
          public void actionPerformed(ActionEvent ae) {
          	removeProject();
          }
        };
    }
    return _removeProjectAction;
  }


  private Action getPreferencesAction() {
    if (_preferencesAction == null) {
      _preferencesAction = new AbstractAction() {
          {
            putValue(NAME, gu().getString(Strings.PREFERENCES));
            putValue(MNEMONIC_KEY, gu().getMnemonicAsInt(Strings.PREFERENCES));
          }
          public void actionPerformed(ActionEvent ae) {
	    showPreferencesDialog();
          }
        };
    }
    return _preferencesAction;
  }


  private Action getEditProjectAction() {
    if (_editProjectAction == null) {
      _editProjectAction = new AbstractAction() {
          {
            putValue(NAME, gu().getString(Strings.EDIT_PROJECT));
            putValue(MNEMONIC_KEY, gu().getMnemonicAsInt(Strings.EDIT_PROJECT));
          }
          public void actionPerformed(ActionEvent ae) {
            editProject();
          }
        };
    }
    return _editProjectAction;
  }          


  private Action getNewSubprojectAction() {
    if (_newSubprojectAction == null) {
      _newSubprojectAction = new AbstractAction() {
          {
            putValue(NAME, gu().getString(Strings.NEW_SUBPROJECT));
            putValue(MNEMONIC_KEY, gu().getMnemonicAsInt(Strings.NEW_SUBPROJECT));
          }
          public void actionPerformed(ActionEvent ae) {
            addNewProject(getSelectedProject());
          }
        };
    }
    return _newSubprojectAction;
  }


  private Action getHelpAboutAction() {
    if (_helpAboutAction == null) {
      _helpAboutAction = new AbstractAction() {
          {
            putValue(NAME, gu().getString(Strings.ABOUT));
            putValue(MNEMONIC_KEY, gu().getMnemonicAsInt(Strings.ABOUT));
          }
          public void actionPerformed(ActionEvent ae) {
            new AboutDialog(SummaryFrame.this).setVisible(true);
          }
        };
    }
    return _helpAboutAction;
  }

  private Action getTimecardReportAction() {
    if (_timecardReportAction == null) {
      _timecardReportAction = new AbstractAction() {
          {
            putValue(NAME, gu().getString(Strings.TIMECARD_REPORT));
            putValue(MNEMONIC_KEY, gu().getMnemonicAsInt(Strings.TIMECARD_REPORT));
          }
          public void actionPerformed(ActionEvent ae) {
            JFrame frame = new JFrame
              (gu().getString(Strings.TIMECARD_REPORT));
            frame.setIconImage(((ImageIcon) _iconClock).getImage());
            frame.getContentPane().add
              (new TimecardReportPanel(getClientState().getProjectGroup(), frame),
               BorderLayout.CENTER);
            frame.setSize(new Dimension(740, 480));
            frame.setVisible(true);
          }
        };
    }
    return _timecardReportAction;
  }

  /**
   * Gets the details from the user, and adds a new project.
   * Parent defaults to the toplevel ``root project''.
   */
  public void addNewProject() {
    addNewProject(getRootProject());
  }


  /**
   * Gets the details from the user, and adds a new project
   */
  private void addNewProject(Project parent) {
    ProjectEditorDialog dlg = new ProjectEditorDialog
      (this, "Create a New Project", null);

    parent = (parent == null) ? getRootProject() : parent;
    dlg.setParentProject(parent, getClientState().getProjectGroup());

    /*
     * Optimistically start generating the new project in a separate
     * thread while the user interacts with the dialog.
     *
     * Project creation takes a significant amount of time because a
     * unique ID has to be generated.  This can be expensive the first
     * time a project is created, since the ID generator has to be
     * initialized.
     */
    Thread t = new Thread() {
        public void run() {
          _newProject =
            getClientState().getProjectFactory().createProject(getClientState().getProjectGroup());
        }
      };
    t.start();

    dlg.setVisible(true);

    try {
      t.join();
    }
    catch (InterruptedException ie) {
      /*
       * If we can't join the thread, we'll create the project in the
       * current one.  This should almost never be the case.
       */
      getLogger().error("Interrupted while creating new project.");
      getLogger().error("Creating project in current thread.");
      _newProject = getClientState().getProjectFactory().createProject(getClientState().getProjectGroup());
    }

    if (dlg.getStatus() == ProjectEditorDialog.OK) {
      /*
       * We shouldn't usually be casting to MutableProject.  We should
       * either use the factory methods that take name and
       * description, or mutate through ProjectGroup.  The reason we
       * have to do this cast here is that we're asynchronously
       * creating the project before we have the name and description
       * available.  We don't want to mutate through project group
       * since we don't want to add the project with null name and
       * descripttion.
       *
       * This should not be repeated elsewhere in then code unless
       * there is a similar compelling reason.  
       */
    	dlg.createProject(getProjectGroup(), (MutableProject) _newProject);

      getClientState().setSelectedProject(_newProject);
    }
    _newProject = null;
  }


  private Project getSelectedProject() {
    TreePath selectionPath = getProjectTree().getSelectionPath();
    if (selectionPath == null) {
      return null;
    }
    else {
      return (Project) selectionPath.getLastPathComponent();
    }
  }


  /**
   * Displays the preferences dialog.
   */
  private void showPreferencesDialog() {
    getPreferencesDialog().setVisible(true);
    if (getPreferencesDialog().isRestartRequired()) {
      JOptionPane.showMessageDialog
        (this, 
         "Some changes will not take effect until Hourglass is restarted.",
         "Restart Required", JOptionPane.INFORMATION_MESSAGE);
    }      
  }


  private PreferencesDialog getPreferencesDialog() {
    if (_preferencesDialog == null) {
      _preferencesDialog = new PreferencesDialog
        (this, new PreferencePanel[] {
          new GeneralPreferencesPanel(),
          new TimePreferencePanel(),
          new PluginPreferencePanel()
        });
    }
    return _preferencesDialog;
  }


  /**
   * Performs an edit on the currently selected project.
   */
  public void editProject() {
    Project p = getSelectedProject();
    if (p != null) {
      ProjectEditorDialog dlg = new ProjectEditorDialog
        (this, "Edit Project", p);    
      dlg.setVisible(true);
      if (dlg.getStatus() == ProjectEditorDialog.OK) {
        try {
          dlg.persistProject(getProjectGroup(), p);
          getClientState().setSelectedProject(p);
        }
        catch (IllegalParentException ipex) {
          JOptionPane.showMessageDialog
            (this, "New parent cannot be a subproject.",
             "Error", JOptionPane.ERROR_MESSAGE);
        }
      }      
    }
    else {
      JOptionPane.showMessageDialog
        (this, "Please select a project first.", "Error",
         JOptionPane.ERROR_MESSAGE);
    }
  }



  /**
   * Removes the currently selected project.
   */
  public void removeProject() {
    Project p = getSelectedProject();
    if (p != null) {
      int result = JOptionPane.showConfirmDialog
        (this, "Delete Project \"" + p.getName() + "\"?", "Confirm Delete",
         JOptionPane.YES_NO_OPTION);
      if (result == JOptionPane.YES_OPTION) {
      	getClientState().removeProject(p);
      }
    }
    else {
      JOptionPane.showMessageDialog
        (this, "Please select a project first.", "Error",
         JOptionPane.ERROR_MESSAGE);
    }
  }


  /**
   * Loads project data from permanent storage
   */
  private void loadProjectData() throws SAXException, IOException {
    getLogger().debug("Loading project data");
    try {
      getClientState().setProjectGroup(getPersistenceManager().load());
    } catch (FileNotFoundException fnfe) {
      // nothing to load
    }
  }

  /**
   * Write the current project data to permemant storage.
   */
  private void writeProjectData() throws SAXException, IOException {
    getLogger().debug("Saving project data");
    getPersistenceManager().save(ClientState.getInstance().getProjectGroup());
  }

  private Logger getLogger() {
    if (_logger == null) {
      _logger = Logger.getLogger(getClass());
    }
    return _logger;
  }


  private MouseListener getPopupListener() {
    if (_popupListener == null) {
      _popupListener = new MouseAdapter() {

          public void mousePressed(MouseEvent me) {
            maybeShowPopup(me);
          }

          public void mouseReleased(MouseEvent me) {
            maybeShowPopup(me);
          }

          private void maybeShowPopup(MouseEvent me) {
            if (me.isPopupTrigger()) {            
              TreePath sel = getProjectTree().getPathForLocation
                (me.getX(), me.getY());
              
              if (sel != null) {
                getProjectTree().setSelectionPath(sel);
              }
              
              getPopup().show(me.getComponent(), me.getX(), me.getY());
            }
          }
        };
    }
    return _popupListener;
  }

  
  public void secondChanged() { }


  public void minuteChanged() {
    updateCurrentTimeLabel();
  }


  public void hourChanged() { }


  public void dayChanged() { 
    if (getClientState().isRunning()) {
      rolloverToNextDay();
      /*
       * No need to refresh activity panel.  It happens in rollover.
       */
    }
    else {
      /*
       * Slight hack.  Makes the activity panel think we switched to a
       * new project, when in fact it's the same one. 
       */
      getActivityPanel().selectedProjectChanged
        (getClientState().getSelectedProject());
    }
  }


  private void rolloverToNextDay() {
    /*
     * PLEASE REFACTOR ME
     */
    Date now = new Date();
    Calendar cal = new GregorianCalendar();
    cal.setTime(now);

    /*
     * Roll back the date by 1.
     */ 
    cal.add(Calendar.DATE, -1);

    /*
     * Set clock to 11:59:59.999
     *
     * Actually, I'm not sure if this is really what we want, because
     * certain days have fewer seconds than others... leap seconds, I
     * think.  Not sure that Java even recognizes these.
     *
     * Someone with a better knowledge of dates needs to think about
     * this.  For now, I'll take my chances.
     */
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);

    getLogger().debug("Stopping time span at: " + cal.getTime());

    /**
     * Stop the timer at 11:59 on the previous date.
     */
    getClientState().stopCurrentTimeSpan(cal.getTime());

    /*
     * Roll forward 1 ms (0:00 next day).
     */
    cal.add(Calendar.MILLISECOND, 1);

    getLogger().debug("Starting time span at: " + cal.getTime());


    /*
     * Slight hack.  Makes the activity panel think we switched to a
     * new project, when in fact it's the same one. 
     */
    getActivityPanel().selectedProjectChanged
      (getClientState().getSelectedProject());
    
    /**
     * Start a new time span at 0:00
     */
    getClientState().startNewTimeSpan(cal.getTime());
  }


  private JPopupMenu getPopup() {
    if (_popup == null) {
      _popup = new JPopupMenu();
      _popup.add(getNewSubprojectAction());
      _popup.add(getEditProjectAction());
      _popup.add(getRemoveProjectAction());
    }
    return _popup;
  }


  /**
   * Reacts to selection changes in the project list.
   */
  public void valueChanged(TreeSelectionEvent e) {
    getClientState().setSelectedProject(getSelectedProject());
  }


  /**
   * Reacts to the current active project changing.
   */
  public void selectedProjectChanged(Project activeProject) {
  	getToggleButton().setEnabled(activeProject != null);
  	refreshTimePlates();
    selectProject(activeProject);
  }


  public void selectProject(Project p) {
  	if (p != null) {
	    TreePath tp = new TreePath
	      (gu().createPathTo
	       (p, getProjectGroup()).toArray());
	
	    getProjectTree().setSelectionPath(tp);
	    getProjectTree().scrollPathToVisible(tp);
  	}
  	else {
  		getProjectTree().clearSelection();
  	}
  }


  /**
   * Convenience method for getting a handle to the ClientState.
   *
   * Equivalent to ClientState.getInstance()
   */
  private ClientState getClientState() {
    return ClientState.getInstance();
  }


  /**
   * Convenience method for getting the root project.
   */
  private Project getRootProject() {
    return getClientState().getProjectGroup().getRootProject();
  }


  public ProjectPersistenceManager getPersistenceManager() {
    return getClientState().getPersistenceManager();
  }


  public ProjectGroup getProjectGroup() {
    return getClientState().getProjectGroup();
  }
  
  private JMenu getPluginMenu() {
  	if (_pluginMenu == null) {
  		_pluginMenu = gu().createMenuFromResource(Strings.MENU_PLUGINS);
  		_pluginMenu.setMnemonic(gu().getMnemonicAsInt(Strings.MENU_PLUGINS));
  		_pluginMenu.addMenuListener(new MenuListener() {
  			public void menuCanceled(MenuEvent e) { }
  			public void menuDeselected(MenuEvent e) { }
  			public void menuSelected(MenuEvent e) {
  				updatePluginMenu();
			}
  		});
  	}
  	return _pluginMenu;
  }
  
  private void updatePluginMenu() {
  	getPluginMenu().removeAll();
  	Iterator i = PluginManager.getInstance().getPluginMenuItems().iterator();
  	while (i.hasNext()) {
  		getPluginMenu().add((JMenuItem) i.next());
  	}
  }
  
  /**
   * Toggles and returns the edit history mode.
   */
  public boolean toggleEditHistoryMode() {
      m_isEditHistMode = !m_isEditHistMode;
      getLowerPanelLayout().show(getLowerPanel(), isEditHistMode() ? CALENDAR_PANEL_KEY : DETAIL_PANEL_KEY);
      refreshTimePlates();
      getLogger().debug("History mode toggled.");
      return m_isEditHistMode;
  }


  private void refreshTimePlates() {
  	ClientState cs = ClientState.getInstance();
  	getActivityPanel().setEnabled(cs.getSelectedProject() != null);
	getActivityPanel().refreshPlates(cs.getSelectedProject());
  }

  public void projectGroupChanged(ProjectGroup group) { }
  public void newTimeSpanStarted(Date d) { }
  public void currentTimeSpanStopped(TimeSpan ts) { }
  
  public void currentTimeSpanAborted() { 
      updateToggleButtonFromClientState();
  }

  /**
   * Returns whether the application is in "edit history mode."
   */
  public boolean isEditHistMode() {
      return m_isEditHistMode;
  }

  /**
   * Returns the data for which to display time span plates.  If the
   * application is in edit mode, this will be the edit date.  If the
   * application is not in edit mode, this will be the current date.
   */
  public Date getEditDate() {
      return m_isEditHistMode ? getCalendarPanel().getDate() : new Date();
  }
  
  private JButton _toggleButton;

  private ActivityPanel _activityPanel;
  private JPanel _detailPanel;
  private JPanel _leftPanel;
  private JPanel _projectPanel;
  private JPanel _mainPanel;
  private JPanel _statusPanel;
  private JPanel _lowerPanel;
  private DateChooserPanel _calendarPanel;
  private CardLayout _lowerPanelLayout;

  private JTree _projectTree;
  private JLabel _currentTimeLabel;
  private JLabel m_runningProjectLabel;
  
  private JMenu _pluginMenu;
  private JMenuBar _menuBar;
  private JPopupMenu _popup;

  private MouseListener _popupListener;

  private Action _addProjectAction;
  private Action _editProjectAction;
  private Action _exitAction;
  private Action _helpAboutAction;
  private Action _newSubprojectAction;
  private Action _removeProjectAction;
  private Action _preferencesAction;
  private Action _timecardReportAction;
  private Action _saveNowAction;

  private Logger _logger;

  private Icon _iconClock;
  private Icon _iconClockGo;
  private Icon _iconClockStop;

  private Project _newProject;
  private boolean m_isEditHistMode;

  private PreferencesDialog _preferencesDialog;
  
  private static String DETAIL_PANEL_KEY = "DETAIL_PANEL_KEY";
  private static String CALENDAR_PANEL_KEY = "CALENDAR_PANEL_KEY";

}
