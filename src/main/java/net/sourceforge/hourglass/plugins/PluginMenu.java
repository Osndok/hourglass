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
 * CVS Revision $Revision: 1.1 $
 * Last modified on $Date: 2004/06/25 04:15:02 $ by $Author: mgrant79 $
 */
package net.sourceforge.hourglass.plugins;

import javax.swing.JMenu;

/**
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class PluginMenu extends JMenu implements PluginObject {
	
	private Plugin m_plugin;
	
	public PluginMenu(Plugin plugin) {
		m_plugin = plugin;
	}
	
	public PluginMenu(Plugin plugin, String name) {
		super(name);
		m_plugin = plugin;
	}

	public void unregister(PluginManager m) {
		m.unregisisterPluginMenuItem(this);
	}

	public Plugin getPlugin() {
		return m_plugin;
	}

}
