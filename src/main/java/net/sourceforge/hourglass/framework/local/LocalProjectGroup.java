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
 * CVS Revision $Revision: 1.11 $
 * Last modified on $Date: 2008/10/25 15:22:52 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework.local;

import net.sourceforge.hourglass.framework.IllegalParentException;
import net.sourceforge.hourglass.framework.MutableProject;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.ProjectGroupListener;
import net.sourceforge.hourglass.framework.TimeSpan;
import net.sourceforge.hourglass.framework.Utilities;
import net.sourceforge.hourglass.swingui.Strings;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;


/**
 * A local implementation of ProjectGroup.
 */
public class LocalProjectGroup implements ProjectGroup {

    public LocalProjectGroup() {
        _projectMap = new HashMap();
        _parentMap = new HashMap();
        _listeners = new LinkedList();
        _rootProject = (MutableProject) new
		LocalProjectFactory().createProject(this,
			gu().getString(Strings.ROOT_NAME),
			gu().getString(Strings.ROOT_DESCRIPTION));
        _attributes = new HashMap();
    }

    public void addProject(Project p, Project parent) {
        _projectMap.put(p.getId(), p);
        MutableProject projectParent = (parent == NO_PARENT)
                ? _rootProject
                : (MutableProject) parent;
        projectParent.addChildProject(p);
        projectParent.sortChildren();
        _parentMap.put(p, projectParent);
        fireProjectsChanged(projectParent);
    }

    public void addProject(Project p, UUID parentId) {
        Project parent = (Project) _projectMap.get(parentId);
        if (parentId != null && parent == null) {
            getLogger().error("No parent with UUID " + parentId);
        }
        addProject(p, parent);
    }

    public boolean removeProject(Project p) {
        getLogger().debug("Removing " + p);
        if (p == null || !_projectMap.containsKey(p.getId())) {
            return false;
        }
        /*
         * Recursively remove child projects. Use a copy of the collection to
         * avoid concurrent modification problems.
         */
        List copy = new LinkedList(p.getChildren());
        Iterator i = copy.iterator();
        while (i.hasNext()) {
            Project eachChild = (Project) i.next();
            removeProject(eachChild);
        }
        boolean wasRemoved = (_projectMap.remove(p.getId()) != null);
        if (wasRemoved) {
            Project parent = (Project) _parentMap.remove(p);
            ((MutableProject) parent).removeChildProject(p);
            fireProjectsChanged(parent);
        }
        return wasRemoved;
    }

    public boolean removeProject(UUID id) {
        return removeProject(getProject(id));
    }

    public Project getProject(UUID id) {
        return (Project) _projectMap.get(id);
    }

    public Collection getProjects() {
        return _projectMap.values();
    }

    public void setProjectName(Project p, String name) {
        ((MutableProject) p).setName(name);
        MutableProject parent = (MutableProject) getParent(p);
        fireProjectAttributesChanged(p);
        if (parent != null) {
            parent.sortChildren();
            fireProjectsChanged(parent);
        }
    }

    public void setProjectDescription(Project p, String desc) {
        ((MutableProject) p).setDescription(desc);
        fireProjectAttributesChanged(p);
    }

    public void addTimeSpan(Project p, TimeSpan t) {
        ((MutableProject) p).addTimeSpan(t);
    }

    public void removeTimeSpan(Project p, TimeSpan t) {
        ((MutableProject) p).removeTimeSpan(t);
    }

    public Project getRootProject() {
        return _rootProject;
    }

    public Project getParent(Project p) {
        return (Project) _parentMap.get(p);
    }

    public void addProjectGroupListener(ProjectGroupListener l) {
        _listeners.add(l);
    }

    public void removeProjectGroupListener(ProjectGroupListener l) {
        _listeners.remove(l);
    }

    public void setParent(Project child, Project parent) throws IllegalParentException {
        /*
         * If the parent is a subproject of the child, setting the parent would
         * create a cycle.
         */
        if (isSubproject(parent, child)) {
            throw new IllegalParentException();
        }
        MutableProject oldParent = (MutableProject) getParent(child);
        MutableProject newParent = (parent == NO_PARENT) ? _rootProject : (MutableProject) parent;
        oldParent.removeChildProject(child);
        _parentMap.remove(child);
        fireProjectsChanged(oldParent);
        newParent.addChildProject(child);
        newParent.sortChildren();
        _parentMap.put(child, newParent);
        fireProjectsChanged(newParent);
    }

    /**
     * Returns true if child is a subproject of parent (or child is equal to
     * parent).
     */
    public boolean isSubproject(Project child, Project parent) {
        if (child == null) {
            return false;
        }
        else if (child.equals(parent)) {
            return true;
        }
        else {
            return isSubproject(getParent(child), parent);
        }
    }

    /**
     * Fires a projectAttributesChanged() to all listeners.
     * 
     * @param p
     *          the project whose attributes have changed.
     */
    protected void fireProjectAttributesChanged(Project p) {
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            ProjectGroupListener l = (ProjectGroupListener) i.next();
            l.projectAttributesChanged(p);
        }
    }

    /**
     * Fires a projectAttributesChanged() to all listeners.
     * 
     * @param parent
     *          the parent project under which the change occurred.
     */
    protected void fireProjectsChanged(Project parent) {
        Iterator i = _listeners.iterator();
        while (i.hasNext()) {
            ProjectGroupListener l = (ProjectGroupListener) i.next();
            l.projectsChanged(parent);
        }
    }

    private
    Logger getLogger() {
        if (_logger == null) {
            _logger = LogManager.getLogger(getClass());
        }
        return _logger;
    }

  private static Utilities gu() {
	  return Utilities.getInstance();
  }

    public String getAttribute(Project project, String domain, String name) {
        Map m = retrieveAttributeMap(project, domain);
        if (m == null) {
            return null;
        }
        else {
            return (String) m.get(name);
        }
    }

    public boolean removeAttribute(Project project, String domain, String name) {
        
        Map attributeMap = createOrRetrieveAttributeMap(project, domain);
        boolean removed = (attributeMap.remove(name) != null);
        if (removed) {
            // If the attribute map for this domain is empty, remove it.
            if (attributeMap.size() == 0) {
                createOrRetrieveProjectMap(project).remove(domain);
            }
        }
        return removed;
    }

    public void setAttribute(Project project, String domain, String name, String value) {
        createOrRetrieveAttributeMap(project, domain).put(name, value);
    }

    public Iterator getAttributeDomains(Project project) {
        Map domainMap = (Map) _attributes.get(project);
        if (domainMap != null) {
            return domainMap.keySet().iterator();
        }
        else {
            return Collections.EMPTY_SET.iterator();
        }
    }

    public Iterator getAttributeKeys(Project project, String domain) {
        Map map = retrieveAttributeMap(project, domain);
        return (map == null) ? Collections.EMPTY_SET.iterator() : map.keySet().iterator();
    }
    
    private Map retrieveAttributeMap(Project project, String domain) {
        Map prjMap = (Map) _attributes.get(project);
        if (prjMap != null) {
            Map domainMap = (Map) prjMap.get(domain);
            if (domainMap != null) {
                return domainMap;
            }
        }
        return null;
    }

    private Map createOrRetrieveAttributeMap(Project project, String domain) {
        Map prjMap = createOrRetrieveProjectMap(project);
        Map domainMap = createOrRetrieveDomainMap(prjMap, domain);
        return domainMap;
    }
    
    private Map createOrRetrieveProjectMap(Project project) {
    	if (!_attributes.containsKey(project)) {
    		_attributes.put(project, new HashMap());
    	}
    	return (Map) _attributes.get(project);
    }

    private Map createOrRetrieveDomainMap(Map source, String domain) {
        if (!source.containsKey(domain)) {
            source.put(domain, new HashMap());
        }
        return (Map) source.get(domain);
    }

    private Logger _logger;
    private Map _projectMap;
    private Map _parentMap;
    /** Map of Project -> domain -> attr_name -> value */
    private Map _attributes;
    private List _listeners;
    private MutableProject _rootProject;
}
