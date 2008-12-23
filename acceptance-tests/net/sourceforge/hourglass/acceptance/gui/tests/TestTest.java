/*
 * Created on Aug 16, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.sourceforge.hourglass.acceptance.gui.tests;

/**
 * @author mike
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestTest extends BaseTest {

  public TestTest(String name) {
    super(name);
  }

  public void testNothing() throws Exception {
  	System.out.println("Doing something.");
    getMainScreen().clickMenu(new String[] {"File", "Save Now"});
//    getMainScreen().clickMenu(new String[] {"Project", "Add Project"});  
  }
  
  public void testNothing2() throws Exception {
    getMainScreen().clickMenu(new String[] {"Edit", "Preferences"});
  }

}
