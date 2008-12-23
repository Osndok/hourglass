/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2004 Michael K. Grant <mike@acm.jhu.edu>
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
 * CVS Revision $Revision: 1.4 $
 * Last modified on $Date: 2004/06/14 02:02:10 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.plugins;


import java.util.prefs.Preferences;

import net.sourceforge.hourglass.swingui.PreferencePanel;

/**
 *
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public interface Plugin {
    
    /**
     * Returns the user-visible name of the plugin.
     */
    String getName();

    /**
     * Returns a preference panel for this plugin.
     */
    PreferencePanel getPreferencePanel();

    /**
     * Returns the author of the plugin.
     */
    String getAuthor();

    /**
     * Returns the domain of the plugin.  By convention, this should
     * almost always return the name of the plugins containing
     * package.  This should not change from release to release.
     */
    String getDomain();

    /**
     * Called to initialize the plugin after hourglass has started, or
     * after the plugin has initially been added.  The plugin may
     * retrieve or set any preferences in the preferenceNode, and the
     * node will be saved when hourglass exits.
     * @param mgr TODO
     */
    void setUp(PluginManager mgr, Preferences preferenceNode);

    /**
     * Called to tear down the plugin.  This is called immediately
     * before hourglass exits, or if the plugin is removed.
     */
    void tearDown();
    
}
