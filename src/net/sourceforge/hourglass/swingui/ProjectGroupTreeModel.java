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
 * CVS Revision $Revision: 1.9 $
 * Last modified on $Date: 2005/05/08 00:16:30 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.ProjectGroupListener;
import net.sourceforge.hourglass.framework.TimeSpan;

import org.apache.log4j.Logger;

/**
 * TreeModel for a ProjectGroup.
 */
public class ProjectGroupTreeModel 
  implements TreeModel, ClientStateListener, ProjectGroupListener {

  
  /**
   * Creates a ProjectGroupTreeModel to represent the given ProjectGroup.
   */
  public ProjectGroupTreeModel(ProjectGroup group) {
    _group = group;
    _group.addProjectGroupListener(this);
    _listeners = new LinkedList();
  }


  public void addTreeModelListener(TreeModelListener listener) {
    _listeners.add(listener);
  }


  public void removeTreeModelListener(TreeModelListener listener) {
    _listeners.remove(listener);
  }

  
  public Object getChild(Object parent, int index) {
    if (index < 0 || index >= getChildCount(parent)) {
      return null;
    }
    getLogger().debug("getChildCount(" + parent + ", " + index + ")");
    Project p = (Project) parent;
    return p.getChildren().get(index);
  }


  public int getChildCount(Object parent) {
    Project p = (Project) parent;
    return p.getChildren().size();
  }


  public int getIndexOfChild(Object parent, Object child) {
    Project p = (Project) parent;
    return p.getChildren().indexOf(child);
  }


  public Object getRoot() {
    return _group.getRootProject();
  }


  public boolean isLeaf(Object o) {
    Project p = (Project) o;
    return (p.getChildren().size() == 0);
  }


  public void valueForPathChanged(TreePath path, Object newValue) {
  }
  

  public Logger getLogger() {
    if (_logger == null) {
      _logger = Logger.getLogger(getClass());
    }
    return _logger;
  }


  public void fireTreeStructureChanged(TreeModelEvent tme) {
    getLogger().debug("fireTreeStructureChanged(" + tme + ")");
    Iterator i = _listeners.iterator();
    while (i.hasNext()) {
      TreeModelListener listener = (TreeModelListener) i.next();
      listener.treeStructureChanged(tme);
    }
  }


  public void fireTreeNodesChanged(TreeModelEvent tme) {
    Iterator i = _listeners.iterator();
    while (i.hasNext()) {
      TreeModelListener listener = (TreeModelListener) i.next();
      listener.treeNodesChanged(tme);
    }
  }
        

  public void projectsChanged(Project parent) {
    TreeModelEvent tme = new TreeModelEvent
      (this, createPathTo(parent).toArray());
    fireTreeStructureChanged(tme);
  }


  public void projectAttributesChanged(Project p) {
    Project parent = _group.getParent(p);
    TreeModelEvent tme = new TreeModelEvent
      (this, 
       createPathTo(parent).toArray(), 
       new int[] {parent.getChildren().indexOf(p)},
       null);
    fireTreeNodesChanged(tme);
  }

  private List createPathTo(Project parent) {
    return Utilities.getInstance().createPathTo(parent, _group);
  }


  public void projectGroupChanged(ProjectGroup g) {
    getLogger().debug("ProjectGroup Changed.");
    _group.removeProjectGroupListener(this);
    _group = g;
    _group.addProjectGroupListener(this);

    TreeModelEvent tme = new TreeModelEvent
      (this, new Object[] {_group.getRootProject()});
    fireTreeStructureChanged(tme);
  }



  public void selectedProjectChanged(Project p) { }
  public void newTimeSpanStarted(java.util.Date d) { }
  public void currentTimeSpanStopped(TimeSpan t) { }
  public void currentTimeSpanAborted() { }


  private Logger _logger;
  private ProjectGroup _group;
  private List _listeners;
}
