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

import net.sourceforge.hourglass.framework.*;
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

    private final Logger log = LogManager.getLogger(getClass());;
    private final Map<UUID, Project> _projectMap = new HashMap<>();
    private final Map<Project,Project> _parentMap = new HashMap<>();
    /** Map of Project -> domain -> attr_name -> value */
    private final Map<Project, ProjectMap> _attributes = new HashMap<>();
    private final List<ProjectGroupListener> _listeners = new LinkedList<>();

    private final MutableProject _rootProject;

    public LocalProjectGroup() {
        _rootProject = (MutableProject) new
		LocalProjectFactory().createProject(this,
			gu().getString(Strings.ROOT_NAME),
			gu().getString(Strings.ROOT_DESCRIPTION));
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
        var parent = _projectMap.get(parentId);
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
        var copy = new LinkedList<>(p.getChildren());
        for (final Project eachChild : copy)
        {
            removeProject(eachChild);
        }
        boolean wasRemoved = (_projectMap.remove(p.getId()) != null);
        if (wasRemoved) {
            var parent = _parentMap.remove(p);
            ((MutableProject) parent).removeChildProject(p);
            fireProjectsChanged(parent);
        }
        return wasRemoved;
    }

    public boolean removeProject(UUID id) {
        return removeProject(getProject(id));
    }

    public Project getProject(UUID id) {
        return _projectMap.get(id);
    }

    public Collection<Project> getProjects() {
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
        return _parentMap.get(p);
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
        for (final ProjectGroupListener l : _listeners)
        {
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
        for (final ProjectGroupListener l : _listeners)
        {
            l.projectsChanged(parent);
        }
    }

    @Deprecated
    private
    Logger getLogger() {
        return log;
    }

  private static Utilities gu() {
	  return Utilities.getInstance();
  }

    public String getAttribute(Project project, String domain, String name) {
        var m = retrieveAttributeMap(project, domain);
        if (m == null) {
            return null;
        }

        return m.get(name);
    }

    public boolean removeAttribute(Project project, String domain, String name) {
        
        var attributeMap = createOrRetrieveAttributeMap(project, domain);
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

    // TODO: Return Collection<String>
    public
    Iterator<String> getAttributeDomains(Project project) {
        var domainMap = _attributes.get(project);
        if (domainMap == null) {
            return Collections.EMPTY_SET.iterator();
        }

        return domainMap.keySet().iterator();
    }

    // TODO: Return Collection<String>
    public
    Iterator<String> getAttributeKeys(Project project, String domain) {
        var map = retrieveAttributeMap(project, domain);
        return (map == null) ? Collections.EMPTY_SET.iterator() : map.keySet().iterator();
    }
    
    private AttributeMap retrieveAttributeMap(Project project, String domain)
    {
        var prjMap = _attributes.get(project);
        if (prjMap == null)
        {
            return null;
        }
        return prjMap.get(domain);
    }

    private
    AttributeMap createOrRetrieveAttributeMap(Project project, String domain) {
        var prjMap = createOrRetrieveProjectMap(project);
        var domainMap = createOrRetrieveDomainMap(prjMap, domain);
        return domainMap;
    }
    
    private
    ProjectMap createOrRetrieveProjectMap(Project project) {
    	return _attributes.computeIfAbsent(project, (p) -> new ProjectMap());
    }

    private
    AttributeMap createOrRetrieveDomainMap(ProjectMap source, String domain) {
        return source.computeIfAbsent(domain, (d) -> new AttributeMap());
    }
}
