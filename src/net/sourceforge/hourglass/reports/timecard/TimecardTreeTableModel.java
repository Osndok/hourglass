/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Copyright (C) 2009 Eric Lavarde <ewl@users.sourceforge.net>
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
 * CVS Revision $Revision: 1.12 $
 * Last modified on $Date: 2005/03/06 00:25:17 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.reports.timecard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import net.sourceforge.hourglass.framework.DateUtilities;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.swingui.Strings;
import net.sourceforge.hourglass.swingui.Utilities;
import net.sourceforge.hourglass.swingui.treetable.AbstractTreeTableModel;
import net.sourceforge.hourglass.swingui.treetable.TreeTableModel;

/**
 * {@link TreeTableModel} for the timecard report.  Provides a project
 * tree with the columns representing hours worked on a given day.
 *
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class TimecardTreeTableModel extends AbstractTreeTableModel {

 /**
   * Creates a new TimecardTreeTableModel.
   *
   * @param projectGroup to project group being modelled
   * @param startDate the start date for the columns
   * @param endDate the end date for the columns
   */
  public TimecardTreeTableModel(ProjectGroup projectGroup, 
                                Date startDate, 
                                Date endDate,
                                DateUtilities.TimeFormatter timeFormatter) {

    super(TREE_ROOT);
    _dateFormat = new SimpleDateFormat(
		gu().getString(Strings.TIMECARD_COLUMN_DATE_FORMAT)); 
    
    _calendar = Calendar.getInstance();
    reinitialize(projectGroup, startDate, endDate, timeFormatter);
  }
  
  
  public void reinitialize(ProjectGroup projectGroup, 
                           Date startDate,
                           Date endDate,
                           DateUtilities.TimeFormatter timeFormatter) {

    _projectGroup = projectGroup;
    _startDate = DateUtilities.getBeginningOfDay(startDate);
    _endDate = DateUtilities.getBeginningOfDay(endDate);
    _timeFormatter = timeFormatter;
  }

  public Object getChild(Object parent, int index) {
  	Project p = null;
  	if (parent.equals(TREE_ROOT)) {
  		p = _projectGroup.getRootProject();
  		if (index == getChildCount(parent) - 1) {
  			// This is the special "sum row".
  			return _projectGroup.getRootProject();
  		}
  	}
  	else if (index < 0 || index >= getChildCount(parent)) {
  		return null;
  	}
  	else {
  		p = (Project) parent;
  	}
  	return p.getChildren().get(index);
  }

  public int getChildCount(Object parent) {
  	if (parent.equals(_projectGroup.getRootProject())) {
  		// This is only used for the summary row.  We don't wish to expose the children. 
  		return 0;
  	}
  	else {
	  	Project p = (parent.equals(TREE_ROOT))
			? _projectGroup.getRootProject()
	  		: (Project) parent;
	    
	    // Root has an extra child for the "total" row.
	    return p.getChildren().size() + (parent.equals(TREE_ROOT) ? 1 : 0);
  	}
  }

  public int getColumnCount() {

    // Number of columns is the number of days plus the "Project"
    // column and the rollup column.
    return getNumDays() + 2;
  }

  private int getNumDays() {
    // Start at 1 because days are inclusive.
    int numDays = 1;

    synchronized (_calendar) {
      // Actually counts the number of date one by one.  Not very
      // efficient, but we're only talking about a handful of days.
      _calendar.setTime(_startDate);
      while (!_endDate.equals(_calendar.getTime())) {
        _calendar.add(Calendar.DATE, 1);
        ++numDays;
      }
    }
    return numDays;
}


public Class getColumnClass(int col) {
    return (col == 0) ? TreeTableModel.class : String.class;
  }

  public String getColumnName(int col) {
    // TODO [MGT, 25 Oct 2003]: Need to show Day (MTWThF) in date
    // column header.
  	
  	if (isProjectColumn(col)) {
  		return gu().getString(Strings.PROJECT);
  	}
  	else if (isSumColumn(col)) {
  		return "Total";
  	}
  	else {
  		return _dateFormat.format(getDate(col - 1));
  	}
  }
  
  private boolean isProjectColumn(int col) {
  	return col == 0;
  }
  
  private boolean isSumColumn(int col) {
  	return col == getColumnCount() - 1;
  }

  public Object getValueAt(Object node, int col) {
    if (isProjectColumn(col)) {
      return node;
    }
    else {
      Project p = (Project) node;
      Date start = null;
      Date end = null;

      if (isSumColumn(col)) {
          start = getDate(0);
          end = DateUtilities.getEndOfDay(getDate(getNumDays() - 1));
      }
      else {
          start = getDate(col - 1);
          end = DateUtilities.getEndOfDay(start);
      }

      long time = p.getTimeBetween(start, end, isNodeCollapsed(p));
      return time == 0 ? null : _timeFormatter.formatTime(time);
    }
  }

  /**
   * Sets the internal tree component of the tree table.
   */
  public void setTree(JTree tree) {
    _tree = tree;
  }

  
  /**
   * Returns the date of the i^{th} column.
   */
  private Date getDate(int i) {
    synchronized(_calendar) {
      _calendar.setTime(_startDate);
      _calendar.add(Calendar.DATE, i);
      return _calendar.getTime();
    }
  }

  private boolean isNodeCollapsed(Project project) {
    if (isLeaf(project)) {
      return true;
    }
    else {
      // If the node's first child is visible, it's expanded,
      // otherwise it's collapsed.
      Project firstChild = (Project) getChild(project, 0);
      Object[] arrayPath = gu().createPathTo
         (firstChild, _projectGroup).toArray();
      // We use a special root object, not the root project.
      arrayPath[0] = TREE_ROOT;
      TreePath tp = new TreePath(arrayPath);
      return !_tree.isVisible(tp);
    }
  }

  private static Utilities gu() {
	  return Utilities.getInstance();
  }

  public static final Object TREE_ROOT = "TREE_ROOT";
  
  private Project _root;
  private ProjectGroup _projectGroup;
  private Date _startDate;
  private Date _endDate;
  private Calendar _calendar;
  private DateFormat _dateFormat;
  private JTree _tree;
  private DateUtilities.TimeFormatter _timeFormatter;
}
