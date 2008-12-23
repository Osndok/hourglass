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
 * CVS Revision $Revision: 1.8 $
 * Last modified on $Date: 2004/08/30 02:08:19 $ by $Author: mgrant79 $
 *
 */
package testplugin;

import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;

import net.sourceforge.hourglass.plugins.Plugin;
import net.sourceforge.hourglass.plugins.PluginManager;
import net.sourceforge.hourglass.plugins.PluginMenuItem;
import net.sourceforge.hourglass.swingui.PreferencePanel;


/**
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class TestPlugin implements Plugin {
    
    private PreferencePanel m_preferencePanel;

    public String getName() {
        // Just making sure the class loader can find dependencies.
        return new NameHelper().getNameFromHelper();
    }

    public String getAuthor() {
        return "Mike Grant";
    }

    public String getDomain() {
        // Should return a unique namespace.  The package name is a good choice.
        return "testplugin";
    }

    public void setUp(PluginManager mgr, Preferences prefs) {
        // Register a menu item under the plugins menu.
        mgr.registerPluginMenuItem(new PluginMenuItem(this, new AbstractAction("Test Plugin Menu Item") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Test Plugin Menu Item Clicked.");
            }
        }));
        
        // Register a panel under the project editor dialog.
        mgr.registerProjectEditorPanel(new TestPluginProjectEditorPanel(this));
    }

    public void tearDown() {
        // Registered objects will automatically be removed, so this is only
        // for custom teardown behavior.
    }

    public PreferencePanel getPreferencePanel() {
        // Provide a panel in the preferences dialog.  Also, see PreferencePanel.
        if (m_preferencePanel == null) {
            m_preferencePanel = new TestPreferencePanel(this);
        }
        return m_preferencePanel;
    }

}
