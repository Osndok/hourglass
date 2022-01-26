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
 * CVS Revision $Revision: 1.2 $
 * Last modified on $Date: 2004/06/24 04:32:50 $ by $Author: mgrant79 $
 */
package net.sourceforge.hourglass.plugins;

import javax.swing.JPanel;

import net.sourceforge.hourglass.framework.Project;

/**
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public abstract class PluginProjectEditorPanel 
	extends JPanel
	implements PluginObject {
	
	private Plugin m_plugin;
	private String m_title;
	
	public String getTitle() {
		return m_title;
	}
	
	public PluginProjectEditorPanel(Plugin plugin) {
		this(plugin, null);
	}
	
	public PluginProjectEditorPanel(Plugin plugin, String title) {
		m_plugin = plugin;
		m_title = title;
	}

	public void unregister(PluginManager m) {
		m.unregisterProjectEditorPanel(getPlugin(), this);
	}
	
	public Plugin getPlugin() {
		return m_plugin;
	}
	
	public abstract void initialize(Project prj);
	
	public abstract void persistChanges(Project prj);
	
	public abstract void cancelChanges(Project prj);

}
