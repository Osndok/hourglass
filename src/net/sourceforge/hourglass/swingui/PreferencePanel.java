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
 * CVS Revision $Revision: 1.5 $
 * Last modified on $Date: 2003/08/16 05:30:22 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.hourglass.framework.HourglassPreferences;


/**
 * Abstract functionality of a JPanel for configuring preferences.
 *
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public abstract class PreferencePanel extends JPanel {


  public PreferencePanel() {
    this(new BorderLayout());
    _settingsChanged = false;
  }


  public PreferencePanel(LayoutManager layout) {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
    _contentPane = new JPanel(layout);
    
    add(createHeader(), BorderLayout.NORTH);
    add(_contentPane, BorderLayout.CENTER);
  }


  private JComponent createHeader() {
    JPanel result = new JPanel(new BorderLayout());
    
    JLabel headerLabel = new JLabel(getLongName());
    headerLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 2, 5));
    headerLabel.setBackground(Color.WHITE);
    headerLabel.setOpaque(true);
    headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 18.0f));
    
    result.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
    result.add(headerLabel, BorderLayout.CENTER);
    return result;
  }

  /**
   * Returns the human-readable name of the preference or set of
   * preferences.  This name will be displayed in the preference
   * editor dialog navigation panel.
   */
  public abstract String getDisplayName();
  
  /**
   * Returns the human-readable description of the preference or 
   * set of preferences. This name will be displayed in the main 
   * panel for the preference as a descriptive header.  The default 
   * value is the display name.
   */
  public String getLongName() {
    return getDisplayName();
  }


  /**
   * Returns the icon associated with the preferences.
   */
  public Icon getIcon() {
    if (_defaultIcon == null) {
      _defaultIcon =
        Utilities.getInstance().getIcon(Strings.IMAGE_PREFS_DEFAULT);
    }
    return _defaultIcon;
  }


  /**
   * Commits the settings.
   */
  public abstract void commitSettings();


  /**
   * Initializes the panel UI with the proper preference settings.
   */
  public abstract void initializeSettings();


  /**
   * Returns true if the committed settings are different from the
   * originals.
   * 
   * TODO [mkg 8/16/2003]: Remove this and use HourglassPreferences.Listener
   */
  public boolean isSettingsChanged() {
    return _settingsChanged;
  }


  /**
   * Returns true if the committed settings require a restert to take
   * effect.
   */
  public abstract boolean isRestartRequired();


  protected HourglassPreferences getPreferences() {
    return HourglassPreferences.getInstance();
  }
  
  
  protected Utilities getUtilities() {
    return Utilities.getInstance();
  }
  
  protected JPanel createNewSection(String resourceKey) {
    JPanel result = new JPanel(new BorderLayout());
    result.setBorder(
      BorderFactory.createTitledBorder(getUtilities().getString(resourceKey)));
    return result;
  }
  
  
  protected Container getContentPane() {
    return _contentPane;
  }
  
    
  protected void setSettingsChanged(boolean b) {
    _settingsChanged = b;
  }
  

  private Icon _defaultIcon;
  private JPanel _contentPane;
  private boolean _settingsChanged;
}
