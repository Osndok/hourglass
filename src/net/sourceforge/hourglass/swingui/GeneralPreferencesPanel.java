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
 * CVS Revision $Revision: 1.2 $
 * Last modified on $Date: 2003/08/16 05:30:22 $ by $Author: mgrant79 $
 *
 */


package net.sourceforge.hourglass.swingui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * General hourglass preferences, ie autosave settings.
 * 
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class GeneralPreferencesPanel extends PreferencePanel {
  
  public GeneralPreferencesPanel() {
    super(new BorderLayout());
    initialize();
  }
  
  
  private void initialize() {
    
    Box box = Box.createVerticalBox();
    box.add(createAutosaveSection());
    
    getContentPane().add(box, BorderLayout.NORTH);
  }
  
  private void updateWidgets() {
    _autosaveIntervalMinutes.setEnabled(_autosaveEnable.isSelected());
    _intervalLabel.setEnabled(_autosaveEnable.isSelected());
  }
  

  public void commitSettings() {
    boolean isAutoSaveEnabled = _autosaveEnable.isSelected();
    boolean isPersistAllEnabled = _persistAllChanges.isSelected();
    int interval = ((Integer) _autosaveIntervalMinutes.getValue()).intValue();
    int backups = ((Integer) _backupsNumber.getValue()).intValue();

    if (isAutoSaveEnabled != getPreferences().getAutosavingEnable()) {
      getPreferences().setAutosavingEnable(isAutoSaveEnabled);
      setSettingsChanged(true);
    }

    if (isPersistAllEnabled != getPreferences().getSaveAllChanges()) {
      getPreferences().setSaveAllChanges(isPersistAllEnabled);
      setSettingsChanged(true);
    }

    if (interval != getPreferences().getAutosavingIntervalMinutes()) {
      getPreferences().setAutosavingIntervalMinutes(interval);
      setSettingsChanged(true);
    }

    if (backups != getPreferences().getBackupsNumber()) {
      getPreferences().setBackupsNumber(backups);
      setSettingsChanged(true);
    }
  }
  
  private JComponent createAutosaveSection() {
    JPanel result = createNewSection(Strings.PREFS_SAVING);
    JPanel widgetPanel = new JPanel(new GridBagLayout());
    widgetPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    _autosaveEnable =
      new JCheckBox(getUtilities().getString(Strings.PREFS_AUTOSAVING_ENABLE));
    
    _persistAllChanges =
      new JCheckBox(getUtilities().getString(Strings.PREFS_SAVE_ALL_CHANGES));

    _autosaveIntervalMinutes = new JSpinner(new SpinnerNumberModel());
    _autosaveIntervalMinutes.getEditor().setPreferredSize(
      new Dimension(50, _autosaveIntervalMinutes.getPreferredSize().height));   

    _intervalLabel =
      new JLabel(
        getUtilities().getFieldLabel(
          Strings.PREFS_AUTOSAVING_INTERVAL_MINUTES));

    _backupsNumber = new JSpinner(new SpinnerNumberModel());
    _backupsNumber.getEditor().setPreferredSize(
      new Dimension(50, _backupsNumber.getPreferredSize().height));   

    _backupsLabel = new JLabel(
        getUtilities().getFieldLabel(Strings.PREFS_BACKUPS_NUMBER) );

    _autosaveEnable.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        updateWidgets();
      }
    });

    {
      GridBagConstraints c = new GridBagConstraints();
      c.gridwidth = GridBagConstraints.REMAINDER;
      c.anchor = GridBagConstraints.WEST;
      widgetPanel.add(_persistAllChanges, c);
      
      c.insets = new Insets(0, 0, 10, 0);
      widgetPanel.add(_autosaveEnable, c);
    }

    {
      GridBagConstraints c_lbl = new GridBagConstraints();
      GridBagConstraints c_fld = new GridBagConstraints();
      c_lbl.anchor = GridBagConstraints.WEST;
      c_lbl.insets = new Insets(0, 0, 0, 5);
      c_fld.gridwidth = GridBagConstraints.REMAINDER;
      c_fld.anchor = GridBagConstraints.WEST;

      widgetPanel.add(_intervalLabel, c_lbl);
      widgetPanel.add(_autosaveIntervalMinutes, c_fld);
    }

    {
      GridBagConstraints b_lbl = new GridBagConstraints();
      GridBagConstraints b_fld = new GridBagConstraints();
      b_lbl.anchor = GridBagConstraints.WEST;
      b_lbl.insets = new Insets(0, 0, 0, 5);
      b_fld.gridwidth = GridBagConstraints.REMAINDER;
      b_fld.anchor = GridBagConstraints.WEST;

      widgetPanel.add(_backupsLabel, b_lbl);
      widgetPanel.add(_backupsNumber, b_fld);
    }

    result.add(widgetPanel, BorderLayout.WEST);
    return result;
  }

  public String getDisplayName() {
    return Utilities.getInstance().getString(Strings.PREFS_GENERAL);
  }
  
  public String getLongName() {
    return Utilities.getInstance().getString(Strings.PREFS_GENERAL_LONG);
  }
  

  public void initializeSettings() {
    boolean isAutosaveEnabled = getPreferences().getAutosavingEnable();
    boolean isPersistAllChangesEnabled = getPreferences().getSaveAllChanges();
    int autosaveInterval = getPreferences().getAutosavingIntervalMinutes();
    int backups = getPreferences().getBackupsNumber();

    _autosaveEnable.setSelected(isAutosaveEnabled);
    _persistAllChanges.setSelected(isPersistAllChangesEnabled);
    _autosaveIntervalMinutes.setValue(new Integer(autosaveInterval));
    _backupsNumber.setValue(new Integer(backups));

    updateWidgets();
  }

  public boolean isRestartRequired() {
    return false;
  }


  public Icon getIcon() {
    return Utilities.getInstance().getIcon(Strings.IMAGE_PREFS_GENERAL);
  }

  private JCheckBox _autosaveEnable;
  private JCheckBox _persistAllChanges;
  private JSpinner _autosaveIntervalMinutes;
  private JLabel _intervalLabel;
  private JSpinner _backupsNumber;
  private JLabel _backupsLabel;

}
