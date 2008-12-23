/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2004  <mgrant@mike>
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
 * Last modified on $Date: 2004/08/22 01:40:14 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.acceptance.gui.tests;

import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.RobotTestHelper;
import net.sourceforge.hourglass.acceptance.gui.framework.MainScreen;
import net.sourceforge.hourglass.swingui.ClientState;
import net.sourceforge.hourglass.swingui.Main;

/**
 *
 * @author  <mgrant@mike>
 */
public class BaseTest extends JFCTestCase {

    private MainScreen m_mainScreen;

    public BaseTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        setHelper(new RobotTestHelper());
        System.out.println("Setting up.");
        if (ClientState.getInstance().getSummaryFrame() == null) {
        	System.out.println("About to launch main.");
            Main.main(new String[0]);
            System.out.println("Launched main.");
        }
        System.out.println("About to create main screen.");
        m_mainScreen = new MainScreen(this);
    }

    public void tearDown() throws Exception {
        super.tearDown();
//        getMainScreen().clickMenu(new String[] {"File", "Exit"});
    }

    protected MainScreen getMainScreen() {
        return m_mainScreen;
    }

}