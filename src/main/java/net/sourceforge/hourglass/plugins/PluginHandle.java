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
 * CVS Revision $Revision: 1.4 $
 * Last modified on $Date: 2004/07/03 04:58:36 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.plugins;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;


/**
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public class PluginHandle {
    
    private JarFile m_jarFile;
    private PluginClassLoader m_pluginClassLoader;
    private static final String CLASS_NAME_ATTRIBUTE = "Hourglass-Plugin-Class";
    private static final String SUPPORTING_LIB_ATTRIBUTE = "Hourglass-Supporting-Libraries";
    
    public PluginHandle(File jarFile) throws IOException {
        m_jarFile = new JarFile(jarFile);
        m_pluginClassLoader = new PluginClassLoader(m_jarFile, getSupportingLibs());
    }
    
    private String[] getSupportingLibs() throws IOException {
		String supportingLibStr = m_jarFile.getManifest().getMainAttributes().getValue(SUPPORTING_LIB_ATTRIBUTE);
        String[] libs = null;
        if (supportingLibStr != null) {
        	libs = supportingLibStr.split(",");
        }
		return libs;
	}

	public Plugin getPluginInstance() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        Class pluginClass = m_pluginClassLoader.loadClass(getPluginClassName(m_jarFile));
        return (Plugin) pluginClass.newInstance();
    }
    
    private String getPluginClassName(JarFile jar) throws IOException {
        return jar.getManifest().getMainAttributes().getValue(CLASS_NAME_ATTRIBUTE);
    }
    
}
