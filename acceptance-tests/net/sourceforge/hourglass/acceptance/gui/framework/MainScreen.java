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
 * CVS Revision $Revision: 1.4 $
 * Last modified on $Date: 2005/03/06 00:24:07 $ by $Author: mgrant79 $
 *
 */

package net.sourceforge.hourglass.acceptance.gui.framework;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.finder.AbstractWindowFinder;
import junit.extensions.jfcunit.finder.JMenuItemFinder;
import net.sourceforge.hourglass.swingui.Strings;

/**
 * 
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class MainScreen extends BaseScreen {

	private JFrame m_frame;

	public MainScreen(JFCTestCase testCase) throws Exception {
		super(testCase);
		System.out.println("Initializing main screen.");
		AbstractWindowFinder finder = new AbstractWindowFinder(null) {
			public boolean testComponent(Component c) {
				return (c instanceof JFrame) && c.getName().equals(Strings.SUMMARY_FRAME);
			}
		};
		m_frame = (JFrame) finder.find();
		if (m_frame == null) {
			throw new RuntimeException("Could not find main frame (got null).");
		}
		System.out.println("Frame: " + m_frame.getName());
	}

	public void clickMenu(String[] menuPath) throws Exception {
		    if (menuPath.length == 0) {
		      throw new IllegalArgumentException("Menu path must be nonzero.");
		    }
		    
		    JMenuItemFinder finder;
		    
		    for (int i = 0; i < menuPath.length; ++i) {
		    	finder = new JMenuItemFinder(menuPath[i]);
		    	JMenuItem menuItem = (JMenuItem) finder.find();
		    	getHelper().enterClickAndLeave(new MouseEventData(getTestCase(), menuItem));
		    	
		    }
		    
	}
}