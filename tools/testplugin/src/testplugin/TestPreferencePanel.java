/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2004 Michael K. Grant <mike@localhost>
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
 * Last modified on $Date: 2004/08/30 02:08:19 $ by $Author: mgrant79 $
 *
 */
package testplugin;

import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sourceforge.hourglass.framework.HourglassPreferences;
import net.sourceforge.hourglass.plugins.Plugin;
import net.sourceforge.hourglass.swingui.PreferencePanel;
import net.sourceforge.hourglass.swingui.Utilities;


class TestPreferencePanel extends PreferencePanel {

    private JTextField m_tf;
    
    public TestPreferencePanel(Plugin plugin) {
        super(new FlowLayout());
        m_tf = new JTextField(10);
        
        // This allows us to get the icon in our external "res.jar".  Note that we
        // need to specify the classloader of the plugin.
        Icon i = Utilities.getInstance().getIconFromResourceName(
                plugin.getClass().getClassLoader(), "images/gnome-fs-client.png");
        getContentPane().add(new JLabel("Test Value:", i, SwingConstants.LEFT));
        getContentPane().add(m_tf);
    }

    public void initializeSettings() {
        // Read any preferences needed to initialize the panel.
        String value = HourglassPreferences.getInstance().getString("test_default", "Default Value");
        m_tf.setText(value);
    }

    public void commitSettings() {
        // Post settings back to preferences.
        HourglassPreferences.getInstance().putString("test_default", m_tf.getText());
    }

    public boolean isRestartRequired() {
        // Return whether the preferences that were change require hourglass
        // to be restarted (will display a message to the user).
        return false;
    }

    public String getDisplayName() {
        // Return the name to be displayed as a menu item.
        return "TestPlugin";
    }
    
    public String getLongName() {
        return "Test Plugin Preferences";
    }
}