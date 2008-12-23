/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Portions Copyright (C) 2003 Neil Thier <nthier@alumni.uwaterloo.ca>
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
 * CVS Revision $Revision: 1.7 $
 * Last modified on $Date: 2005/05/08 00:16:30 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.Component;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sourceforge.hourglass.framework.DateUtilities;
import net.sourceforge.hourglass.framework.Project;


/**
 * Renders projects in a tree by displaying their names.
 *
 * @author Mike Grant
 */
public class ProjectTreeCellRenderer extends DefaultTreeCellRenderer {


  public Component getTreeCellRendererComponent
    (JTree tree, Object value, boolean sel,
     boolean expanded, boolean leaf, int row, boolean hasFocus) {

  	if (value instanceof Project) {
	    Project p = (Project) value;
	    String name;
	    if (p.equals(tree.getModel().getRoot())) {
	      name = "ROOT (No Parent)";
	    }
	    else {
	      name = p.getName();
	    }
	
	    JComponent result = 
	      (JComponent) super.getTreeCellRendererComponent
	      (tree, name, sel, expanded, leaf, row, hasFocus);
	
	    result.setToolTipText(createToolTipText(p));            
	    return result;
  	}
  	else {
  		return super.getTreeCellRendererComponent
		      (tree, value, sel, expanded, leaf, row, hasFocus);
  	}
  }


  private String createToolTipText(Project p) {
    StringBuffer sb = new StringBuffer();
    sb.append("<html><strong>");
    sb.append(p.getName()).append("</strong>");
    sb.append("<br>");
    sb.append(getDescriptionString(p.getDescription()));
    sb.append("<br>");
    sb.append("Today: " + formatTime(getTime(p, p.getTimeSince
                 (DateUtilities.getBeginningOfDay(new Date()), true))));
    sb.append(" - Total: " + formatTime
              (getTime(p, p.getTotalTime(true))));
    sb.append("<html>");

    return sb.toString();
  }

  private long getTime(Project p, long time) {
    long now = System.currentTimeMillis();
    ClientState cs = ClientState.getInstance();
	if (cs.isRunning() && p.equals(cs.getSelectedProject())) {
      long elapsed =
        now - cs.getOpenTimeSpanStartMillis();
      return time + elapsed;
    }
    else {
      return time;
    }
  }

  /**
   * Formats the description string for display in a ToolTip.
   */
  private String getDescriptionString(String fullDescription) {
    if (fullDescription == null ||
        Utilities.getInstance().isAllWhitespace(fullDescription)) {
      return "(no description)";
    }
    else {
      return Utilities.getInstance().chopString(fullDescription, 40, "...");
    }
  }

  /**
   * Formats a millisecond amount int hh:mm.
   */
  // TODO [MGT, 27 Oct 2003]: refactor
  private String formatTime(long ms) { 
    long hours = ms / MS_PER_HOUR;
    long minutes = (ms - hours * MS_PER_HOUR) / MS_PER_MINUTE;

    return 
      ((hours < 10) ? "0" : "") + hours + ":" + 
      ((minutes < 10) ? "0" : "") + minutes;
  }

  private static final long MS_PER_MINUTE = 60000;
  private static final long MS_PER_HOUR = MS_PER_MINUTE * 60;

}
