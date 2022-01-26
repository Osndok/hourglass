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
 * CVS Revision $Revision: 1.7 $
 * Last modified on $Date: 2008/10/25 15:22:52 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.swingui;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.hourglass.HourglassTestCase;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.local.LocalProjectFactory;

import java.util.UUID;


/**
 * Unit tests for ProjectGroupTreeModel
 *
 * @author Mike Grant
 */
public class ProjectGroupTreeModelTests 
  extends HourglassTestCase
  implements TreeModelListener {


  public static Test suite() {
    TestSuite result = new TestSuite(ProjectGroupTreeModelTests.class);
    result.setName("ProjectGroupTreeModelTests tests");
    
    return result;
  }


  public void setUp() throws Exception {
    _group = getSampleDataHier();
    _model = new ProjectGroupTreeModel(_group);
    _model.addTreeModelListener(this);
    _treeStructureChangedEvent = null;
  }


  public void testTreeStructure() {
    Project rootProject = (Project) _model.getRoot();
    Project p1 = _group.getProject(ID_HIER_1);
    Project p2 = _group.getProject(ID_HIER_2);
    Project p3 = _group.getProject(ID_HIER_3);
    Project p4 = _group.getProject(ID_HIER_4);
    Project p5 = _group.getProject(ID_HIER_5);

    assertFalse(_model.isLeaf(rootProject));
    assertEquals(1, _model.getChildCount(rootProject));
    assertEquals(p1, _model.getChild(rootProject, 0));
    assertEquals(0, _model.getIndexOfChild(rootProject, p1)); 


    assertFalse(_model.isLeaf(p1));
    assertEquals(2, _model.getChildCount(p1));
    assertEquals(p2, _model.getChild(p1, 0));
    assertEquals(p3, _model.getChild(p1, 1));
    assertNull(_model.getChild(p1, 2));
    assertEquals(0, _model.getIndexOfChild(p1, p2)); 
    assertEquals(1, _model.getIndexOfChild(p1, p3)); 
    assertEquals(-1, _model.getIndexOfChild(p1, rootProject));

    assertTrue(_model.isLeaf(p2));
    assertEquals(0, _model.getChildCount(p2));
    assertNull(_model.getChild(p2, 0));
    assertEquals(-1, _model.getIndexOfChild(p2, rootProject));

    assertFalse(_model.isLeaf(p3));
    assertEquals(2, _model.getChildCount(p3));
    assertEquals(p4, _model.getChild(p3, 0));
    assertEquals(p5, _model.getChild(p3, 1));
    assertNull(_model.getChild(p3, 2));
    assertEquals(0, _model.getIndexOfChild(p3, p4)); 
    assertEquals(1, _model.getIndexOfChild(p3, p5)); 
    assertEquals(-1, _model.getIndexOfChild(p3, rootProject));

    assertTrue(_model.isLeaf(p4));
    assertEquals(0, _model.getChildCount(p4));
    assertNull(_model.getChild(p4, 0));
    assertEquals(-1, _model.getIndexOfChild(p4, rootProject));

    assertTrue(_model.isLeaf(p5));
    assertEquals(0, _model.getChildCount(p5));
    assertNull(_model.getChild(p5, 0));
    assertEquals(-1, _model.getIndexOfChild(p5, rootProject));

  }


  public void testAdd() {
    LocalProjectFactory f = new LocalProjectFactory();
    ProjectGroup group = f.createProjectGroup();
    Project p = f.createProject(group, "tmp", "tmp");

    _group.addProject(p, ProjectGroup.NO_PARENT);
    TreePath path = _treeStructureChangedEvent.getTreePath();
    assertEquals(_group.getRootProject(), path.getPath()[0]);
    assertEquals(1, path.getPathCount());
  }


  public void testRemove() {
    _group.removeProject(ID_HIER_3);
    TreePath path = _treeStructureChangedEvent.getTreePath();
    assertEquals(_group.getRootProject(), path.getPath()[0]);
    assertEquals(_group.getProject(ID_HIER_1), path.getPath()[1]);
    assertEquals(2, path.getPathCount());
  }


  public void treeNodesChanged(TreeModelEvent e) {
  }

  
  /*
   * Not yet used.  Uses treeStructureChanged() instead.
   */
  public void treeNodesInserted(TreeModelEvent e) {
  }

  
  /*
   * Not yet used.  Uses treeStructureChanged() instead.
   */
  public void treeNodesRemoved(TreeModelEvent e) {
  }

  
  public void treeStructureChanged(TreeModelEvent e) {
    _treeStructureChangedEvent = e;
  }


  private ProjectGroup _group;
  private ProjectGroupTreeModel _model;

  private TreeModelEvent _treeStructureChangedEvent;

  private static final UUID ID_HIER_1 =
    UUID.fromString("66a04623-056e-11d7-b289-e0cd170f00c2");
  private static final UUID ID_HIER_2 =
    UUID.fromString("07502364-056f-11d7-b289-e0cd170f00c2");
  private static final UUID ID_HIER_3 =
    UUID.fromString("6cfdea2b-5d9c-11d7-8b65-9ca753b1b6a8");
  private static final UUID ID_HIER_4 =
    UUID.fromString("6d00341c-5d9c-11d7-8b65-9ca753b1b6a8");
  private static final UUID ID_HIER_5 =
    UUID.fromString("6d00f76d-5d9c-11d7-8b65-9ca753b1b6a8");
}

