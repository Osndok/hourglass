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
 * Last modified on $Date: 2008/11/13 21:08:51 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import net.sourceforge.hourglass.framework.HourglassPreferences;


/**
 * Abstract functionality of a JPanel for configuring preferences.
 *
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public class TimePreferencePanel 
  extends PreferencePanel
  implements ActionListener {


  public TimePreferencePanel() {
    super();
    initializeComponents();
    _icon = Utilities.getInstance().getIcon(Strings.IMAGE_PREFS_TIME);
  }
  
  
  public Icon getIcon() {
    return _icon;
  }


  public String getDisplayName() {
    return Utilities.getInstance().getString(Strings.PREFS_TIME);
  }


  public String getLongName() {
    return Utilities.getInstance().getString(Strings.PREFS_TIME_LONG);
  }


  public void commitSettings() {
    boolean newUseDefaultTimeZone =
      getTimezoneButtonGroup().getSelection().getActionCommand().equals(USE_DEFAULT);
    String newTimeZone = 
      (String) getTimeZoneList().getSelectedValue();
    String newTimeFormat = getFormatButtonGroup().getSelection().getActionCommand();

    if ((newTimeZone == null && _originalTimeZone != null) ||
        (newTimeZone != null && _originalTimeZone != null) ||
        (newTimeZone != null && !newTimeZone.equals(_originalTimeZone))) {

      getPreferences().setTimezone(newTimeZone);
      setTimezoneSettingsChanged(true);
    }
        
    if (newUseDefaultTimeZone != _originalUseDefaultTimeZone) {
      getPreferences().setTimezoneUseDefault(newUseDefaultTimeZone);
      setSettingsChanged(true);
    }

    if (newTimeFormat != _originalTimeFormat) {
        getPreferences().setTimeFormatType(newTimeFormat);
    	setSettingsChanged(true);
    }

  }

  public void initializeSettings() { 
    _originalUseDefaultTimeZone = getPreferences().getTimezoneUseDefault();
    _originalTimeZone = getPreferences().getTimezone();
    _originalTimeFormat = getPreferences().getTimeFormatType();

    if (_originalUseDefaultTimeZone) {
      _defaultButton.setSelected(true);
      getTimeZoneList().setEnabled(false);
    }
    else {
      _manualButton.setSelected(true);
      getTimeZoneList().setEnabled(true);
    }

    if (getPreferences().getTimezone() != null) {
      getTimeZoneList().setSelectedValue(_originalTimeZone, true);
    }
    else {
      getTimeZoneList().clearSelection();
    }
    
    if (_originalTimeFormat.equals(Strings.TIME_FORMAT_24_HOUR)) {
    	_24HourButton.setSelected(true);
    }
    else {
    	_12HourButton.setSelected(true);
    }
  }


  private void initializeComponents() {
    getContentPane().add(getTimezonePanel(), BorderLayout.CENTER);
    getContentPane().add(getFormatPanel(), BorderLayout.SOUTH);
  }
  
  private JPanel getFormatPanel() {
  	if (_formatPanel == null) {
		_formatPanel = new JPanel(new BorderLayout());
		JPanel innerPanel = new JPanel(new FlowLayout());
		_formatPanel.setBorder(BorderFactory.createTitledBorder(
				Utilities.getInstance().getString(Strings.PREFS_FORMAT)
				));
		innerPanel.add(new JLabel(
				Utilities.getInstance().getString(Strings.PREFS_TIME_FORMAT_TYPE)
				));
		_12HourButton = makeRadioButton(getFormatButtonGroup(), 
				Utilities.getInstance().getString(Strings.TIME_FORMAT_12_HOUR),
				Strings.TIME_FORMAT_12_HOUR);
		_24HourButton = makeRadioButton(getFormatButtonGroup(),
				Utilities.getInstance().getString(Strings.TIME_FORMAT_24_HOUR),
				Strings.TIME_FORMAT_24_HOUR);
		innerPanel.add(_12HourButton);
		innerPanel.add(_24HourButton);
		_formatPanel.add(innerPanel, BorderLayout.WEST);
	}

	return _formatPanel;
  }


  private JPanel getTimezonePanel() {
    if (_timezonePanel == null) {
      _timezonePanel = new JPanel(new BorderLayout());
      _timezonePanel.add(getRadioButtonPanel(), BorderLayout.NORTH);
      _timezonePanel.add(getTimeZoneListPane(), BorderLayout.CENTER);
      _timezonePanel.setBorder(BorderFactory.createTitledBorder(
    		  Utilities.getInstance().getString(Strings.PREFS_TIMEZONE)
    		  ));
    }
    return _timezonePanel;
  }


  private JScrollPane getTimeZoneListPane() {
    if (_timeZoneListPane == null) {
      _timeZoneListPane = new JScrollPane(getTimeZoneList());
    }
    return _timeZoneListPane;
  }


  private JList getTimeZoneList() {
    if (_timeZoneList == null) {
      String[] ids = TimeZone.getAvailableIDs();
      Arrays.sort(ids);
      _timeZoneList = new JList(ids);
    }
    return _timeZoneList;
  }


  private JPanel getRadioButtonPanel() {
    if (_radioPanel == null) {
      _radioPanel = new JPanel(new GridLayout(2, 1));
      _defaultButton = makeRadioButton
        (getTimezoneButtonGroup(), 
        		Utilities.getInstance().getString(Strings.PREFS_TIMEZONE_USE_DEFAULT),
        		USE_DEFAULT);

      _manualButton = makeRadioButton
        (getTimezoneButtonGroup(),
           		Utilities.getInstance().getString(Strings.PREFS_TIMEZONE_USE_MANUAL),
        		USE_MANUAL);

      _radioPanel.add(_defaultButton);
      _radioPanel.add(_manualButton);
    }
    return _radioPanel;
  }


  private JRadioButton makeRadioButton
    (ButtonGroup group, String desc, String action) {

    JRadioButton result = new JRadioButton(desc);
    result.setActionCommand(action);
    group.add(result);
    result.addActionListener(this);

    return result;
  }


  /**
   * Action listener implementation for the radio buttons.
   */
  public void actionPerformed(ActionEvent ae) {
    if (getTimezoneButtonGroup().getSelection().
        getActionCommand().equals(USE_MANUAL)) {
      getTimeZoneList().setEnabled(true);
    }
    else {
      getTimeZoneList().setEnabled(false);
    }
  }


  private ButtonGroup getTimezoneButtonGroup() {
    if (_timezoneButtonGroup == null) {
      _timezoneButtonGroup = new ButtonGroup();
    }
    return _timezoneButtonGroup;
  }

  private ButtonGroup getFormatButtonGroup() {
    if (_formatButtonGroup == null) {
      _formatButtonGroup = new ButtonGroup();
    }
    return _formatButtonGroup;
  }

  public boolean isRestartRequired() {
    return isTimezoneSettingsChanged();
  }
  
  private boolean isTimezoneSettingsChanged() {
      return _timezoneSettingsChanged;
  }
  
  private void setTimezoneSettingsChanged(boolean b) {
      _timezoneSettingsChanged = b;
      if (b) {
          setSettingsChanged(b);
      }
  }


  private static final String USE_DEFAULT = "USE_DEFAULT";
  private static final String USE_MANUAL = "USE_MANUAL";
  
  private ButtonGroup _timezoneButtonGroup;
  private ButtonGroup _formatButtonGroup;
  private Icon _icon;
  private JPanel _timezonePanel;
  private JPanel _formatPanel;
  private JPanel _radioPanel;
  private JScrollPane _timeZoneListPane;
  private JList _timeZoneList;
  private JRadioButton _manualButton;
  private JRadioButton _defaultButton;
  private JRadioButton _12HourButton;
  private JRadioButton _24HourButton;
  private String _originalTimeZone;
  private boolean _originalUseDefaultTimeZone;
  private String _originalTimeFormat;
  private boolean _timezoneSettingsChanged;
}
