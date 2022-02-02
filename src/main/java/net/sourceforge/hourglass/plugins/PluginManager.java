/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2004  Michael K. Grant <mike@acm.jhu.edu>
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
 * CVS Revision $Revision: 1.10 $
 * Last modified on $Date: 2005/05/02 00:04:13 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.sourceforge.hourglass.framework.HourglassException;
import net.sourceforge.hourglass.framework.HourglassPreferences;
import net.sourceforge.hourglass.swingui.ExceptionHandler;
import net.sourceforge.hourglass.swingui.Utilities;
import net.sourceforge.hourglass.swingui.Strings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class PluginManager implements ListModel {

    private static PluginManager s_instance = null;
    private static final Logger s_logger = LogManager.getLogger(PluginManager.class);
    
    private static final String PLUGIN_PREFS_KEY = "PLUGINS";
    private static final String PLUGIN_JAR_FILE_LOCATION_KEY = "JAR_FILE_LOCATION";
    private static final String PLUGIN_NAME = "PLUGIN_NAME";

    private Preferences m_pluginPreferences;
    private List m_plugins;
    private List m_dataListeners;
    private List m_projectEditorPanels;
    private List m_pluginMenuItems;
    private Map m_registeredPluginObjects;

    protected PluginManager() throws BackingStoreException {
        Preferences rootPrefs = 
            HourglassPreferences.getInstance().getRootPreferencesNode();
        m_pluginPreferences = rootPrefs.node(PLUGIN_PREFS_KEY);
        m_plugins = new ArrayList();
        m_dataListeners = new LinkedList();
        m_pluginMenuItems = new LinkedList();
        m_projectEditorPanels = new LinkedList();
        m_registeredPluginObjects = new HashMap();
        setUpPlugins();
    }

    public static void initializePluginManager() throws HourglassException {
    	try {
    		s_instance = new PluginManager();
    	}
    	catch (BackingStoreException e) {
    		throw new HourglassException(e, Strings.ERROR_KEY_PLUGIN_MGR_INIT, null);
    	}
    }

    public List getPlugins() {
        return m_plugins;
    }

    public void setUpPlugins() throws BackingStoreException {
        String[] pluginPrefNames = m_pluginPreferences.childrenNames();
        for (int i = 0; i < pluginPrefNames.length; ++i) {
            String pluginNodeName = pluginPrefNames[i];
			Preferences node = m_pluginPreferences.node(pluginNodeName);
            String filename = node.get(PLUGIN_JAR_FILE_LOCATION_KEY, null);
            if (filename != null) {
                try {
                    Plugin eachPlugin = getPluginInstance(new File(filename));
                    setupAndAdd(eachPlugin, node);
                }
                catch (FileNotFoundException e) {
                	handleInstalledPluginLoadError(node, filename, e);
                }
                catch (Exception e) {
                	handleInstalledPluginLoadError(node, filename, e);
                }
            }
            else {
            	// Plugin had no file associated with it.
            	handleInstalledPluginLoadError(node, filename, null);
            }
        }
    }

    /**
     * Handles an error loading a plugin that was previously installed.
     * Propmts the user for whether to remove the plugin next time hourglass
     * is started.
     */
    private void handleInstalledPluginLoadError(Preferences node,
			String filename, Exception cause)
			throws BackingStoreException {
        ExceptionHandler.showUser(cause);
    	String name = node.get(PLUGIN_NAME, "(unknown)");
		s_logger.error("Error loading plugin " + name + " at file "
				+ filename, cause);
		int option = Utilities.getInstance().showConfirmation(null, 
				Strings.ERROR_KEY_PLUGIN_LOAD, new String[] { name, filename },
				JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			node.removeNode();
		}
	}

	private Plugin getPluginInstance(File file) throws Exception {
        PluginHandle handle = new PluginHandle(file);
        return handle.getPluginInstance();
    }

    private void setupAndAdd(Plugin plugin, Preferences node) {
        plugin.setUp(this, node);
        m_plugins.add(plugin);
    }

    /**
     * Adds a new plugin to the manager.
     */
    public void addPlugin(File pluginFile) throws Exception {
        Plugin plugin = getPluginInstance(pluginFile);
        String prefName = getPrefName(plugin);
        if (!m_pluginPreferences.nodeExists(prefName)) {
            Preferences node = m_pluginPreferences.node(getPrefName(plugin));
            node.put(PLUGIN_JAR_FILE_LOCATION_KEY, pluginFile.getAbsolutePath());
            node.put(PLUGIN_NAME, plugin.getName());
            setupAndAdd(plugin, node);
            fireContentsChanged();
        }
        // TODO [MGT, 01 May 2004]: Else throw exception.
    }

    /**
     * Removes a plugin from the manager.
     * @param p The plugin to remove.
     * @throws BackingStoreException
     */
    public void removePlugin(Plugin p) throws BackingStoreException {
        p.tearDown();
        m_plugins.remove(p);
        String prefName = getPrefName(p);
        if (m_pluginPreferences.nodeExists(prefName)) {
            m_pluginPreferences.node(prefName).removeNode();
        }
        removeRegisteredObjects(p);
        fireContentsChanged();
    }
    
    protected void removeRegisteredObjects(Plugin p) {
		List list = ((List) m_registeredPluginObjects.get(p));
		if (list != null) {
			Iterator i = list.iterator();
			while (i.hasNext()) {
				PluginObject eachObject = (PluginObject) i.next();
				eachObject.unregister(this);
			}
			m_registeredPluginObjects.remove(p);
		}
	}

    public void tearDownPlugins() {
        Iterator i = getPlugins().iterator();
        while (i.hasNext()) {
            Plugin eachPlugin = (Plugin) i.next();
            eachPlugin.tearDown();
        }
    }

    public static PluginManager getInstance() {
        return s_instance;
    }

    private String getPrefName(Plugin plugin) {
        return plugin.getDomain() + "_" + plugin.getName();
    }

    public void addListDataListener(ListDataListener l) {
        m_dataListeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        m_dataListeners.remove(l);
    }

    public int getSize() {
        return m_plugins.size();
    }
    
    public Object getElementAt(int index) {
        return m_plugins.get(index);
    }

    private void fireContentsChanged() {
        Iterator i = m_dataListeners.iterator();
        while (i.hasNext()) {
            ListDataListener eachListener = (ListDataListener) i.next();
            ListDataEvent event = new ListDataEvent
                (this, ListDataEvent.CONTENTS_CHANGED, 0, m_plugins.size() - 1);
            eachListener.contentsChanged(event);
        }
    }
    
    public void registerProjectEditorPanel(PluginProjectEditorPanel panel) {
    	addRegisteredPluginObject(panel.getPlugin(), panel);
    	m_projectEditorPanels.add(panel);
    }
    
    public void registerPluginMenuItem(PluginObject menu) {
    	addRegisteredPluginObject(menu.getPlugin(), menu);
    	m_pluginMenuItems.add(menu);
    }
    
    void unregisisterPluginMenuItem(PluginObject menu) {
    	m_pluginMenuItems.remove(menu); 
    }
    
    void unregisterProjectEditorPanel(Plugin plugin, PluginProjectEditorPanel p) {
    	m_projectEditorPanels.remove(p);
    }
    
    public List getProjectEditorPanels() {
    	return m_projectEditorPanels;
    }
    
    public List getPluginMenuItems() {
    	return m_pluginMenuItems;
    }
    
    protected void addRegisteredPluginObject(Plugin plugin, PluginObject o) {
    	if (!m_registeredPluginObjects.containsKey(plugin)) {
    		m_registeredPluginObjects.put(plugin, new LinkedList());
    	}
    	((List) m_registeredPluginObjects.get(plugin)).add(o);
    }

}
