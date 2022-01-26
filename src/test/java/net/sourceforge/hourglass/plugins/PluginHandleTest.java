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
 * Last modified on $Date: 2005/03/06 00:25:34 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;
import net.sourceforge.hourglass.framework.Utilities;


/**
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public class PluginHandleTest extends TestCase {
    
    
    private static final String PLUGIN_NAME = "Test Plugin";
    private static final String JAR_RESOURCE_NAME = "net/sourceforge/hourglass/plugins/testplugin.jar";
    private File m_jarFile;
    
    
    public void setUp() throws Exception {
        m_jarFile = File.createTempFile("testplugin", ".jar");
        m_jarFile.deleteOnExit();
        InputStream jarStream = getClass().getClassLoader().getResourceAsStream(JAR_RESOURCE_NAME);
        if (jarStream == null) {
            throw new IllegalStateException("Could not locate embedded test plugin: "+JAR_RESOURCE_NAME);
        }
        FileOutputStream outputStream = new FileOutputStream(m_jarFile);
        Utilities.copy(jarStream, outputStream);
        outputStream.close();
        jarStream.close();
    }
    
    public void testGetPlugin() throws Exception {
        PluginHandle handle = new PluginHandle(m_jarFile);
        Plugin plugin = handle.getPluginInstance();
        assertEquals(PLUGIN_NAME, plugin.getName());
    }
}
