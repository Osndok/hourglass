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
 * CVS Revision $Revision: 1.6 $
 * Last modified on $Date: 2005/05/02 00:04:12 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.plugins;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import net.sourceforge.hourglass.framework.Utilities;

import org.apache.log4j.Logger;

/**
 *
 * @author Michael K. Grant <mike@localhost>
 */
public class PluginClassLoader extends ClassLoader {
    
    private static final Logger s_logger = Logger.getLogger(PluginClassLoader.class);
    private static final String CLASS_SUFFIX = ".class";
    private JarFile m_jarFile;
    private JarFile[] m_supportingLibraries;
    
    public PluginClassLoader(JarFile jarFile, String[] supportingLibResNames) throws IOException {
        m_jarFile = jarFile;
        extractSupportingLibraries(jarFile, supportingLibResNames);
    }
    
    private void extractSupportingLibraries(JarFile jarFile, String[] supportingLibResNames) throws IOException {
		if (supportingLibResNames != null) {
        	m_supportingLibraries = new JarFile[supportingLibResNames.length];
        	for (int i = 0; i < supportingLibResNames.length; ++i) {
        		m_supportingLibraries[i] = unpackageJar(jarFile, supportingLibResNames[i]);
        	}
        }
	}

    protected URL findResource(String name) {
    	try {
    		String string = "jar:file:/" + m_jarFile.getName().replace('\\', '/') + "!/" + name;
    		return new URL(string);
    	}
    	catch (MalformedURLException e) {
    		return null;
    	}
	}
    
	public Class findClass(String name) throws ClassNotFoundException {
		byte[] classData;
        try {
            classData = loadClassData(name, m_jarFile);
            if (m_supportingLibraries != null) {
	            for (int i = 0; classData == null && i < m_supportingLibraries.length; ++i) {
	            	//System.out.println("Searching supporting library " + m_supportingLibraries[i].getName());
	            	classData = loadClassData(name, m_supportingLibraries[i]);
	            }
            }
            if (classData == null) {
                throw new ClassNotFoundException("Could not find class " + name);
            }
            else {
            	return defineClass(name, classData, 0, classData.length);
            }
        }
        catch (IOException e) {
            throw new ClassNotFoundException("Could not find class " + name, e);
        }
    }

    private byte[] loadClassData(String name, JarFile jarFile) throws IOException {
        String entryName = name.replace('.', '/') + CLASS_SUFFIX;
        ZipEntry entry = jarFile.getEntry(entryName);
        if (entry == null) {
            return null;
        }
        
        InputStream is = jarFile.getInputStream(entry);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        Utilities.copy(is, bOut);
        return bOut.toByteArray();
    }    
    
    private JarFile unpackageJar(JarFile base, String name) throws IOException {
    	ZipEntry entry = base.getEntry(name);
    	InputStream is = base.getInputStream(entry);
        File tmpFile = File.createTempFile("hourglass-jar-", "jar");
        tmpFile.deleteOnExit();
        FileOutputStream fout = new FileOutputStream(tmpFile);
        Utilities.copy(is, fout);
        fout.close();
        return new JarFile(tmpFile);
    }
    
}
