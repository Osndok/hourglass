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
 * CVS Revision $Revision: 1.12 $
 * Last modified on $Date: 2008/10/25 15:22:52 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.hourglass.framework.MutableProject;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.TimeSpan;
import net.sourceforge.hourglass.framework.TimeSpanOverlapException;
import net.sourceforge.hourglass.framework.Utilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;


/**
 * A locally stored project.
 * 
 * @author Mike Grant
 */
public class LocalProject implements MutableProject {

    public LocalProject(UUID u) {
        _uuid = u;
        _timespans = new TreeSet<>();
        _children = new ArrayList<>();
        _childrenReadOnly = Collections.unmodifiableList(_children);
        _desc = _name = "";
    }

    /**
     * Returns the ID of this object.
     */
    public UUID getId() {
        return _uuid;
    }

    /**
     * Adds the given time span to the project.
     */
    public void addTimeSpan(TimeSpan ts) {
        /**
         * NB: checkForOvelap throws, which means we don't add the timespan, which means we lose data if they are editing.
         * TODO: turn off checking when we're initializing from persisted form.
         * It's a big performance hit (n^2 checks).
         */
        if (false)
        {
            checkForOverlap(ts);
        }
        _timespans.add(ts);
    }

    /**
     * Throws a TimeSpanOverlapException if the given time span overlaps one
     * already in the project. A TimeSpan, t1, overlaps another, t2, if either
     * of the following conditions are true:
     * 
     * <ol>
     * <li>t1.start <= t2.start && t1.end > t2.start</li>
     * <li>t1.start < t2.end && t1.start >= t2.start</li>
     * <li>t1.start == t2.start && t1.end == t2.end</li>
     * </ol>
     * 
     * @param timeSpan
     *          the timeSpan with which to check for overlap.
     */
    @Deprecated
    public void checkForOverlap(TimeSpan timeSpan) {
        for (final TimeSpan curr : _timespans)
        {
            checkForLeftOverlap(timeSpan, curr, timeSpan);
            checkForLeftOverlap(curr, timeSpan, timeSpan);
            /*
             * How can we generalize this into 2 cases, similar to the ones
             * above, instead of explicitly treating equality as a special
             * case? Can someone work on this, or show that it can't be
             * generalized? The two checks above work for everything except
             * zero-length duplicates. Perhaps the solution is to disallow
             * zero-length timespans altogether, but that seems too restrictive
             * to me.
             *
             * For now, we'll do the check.
             */
            if (timeSpan.equals(curr))
            {
                throw new TimeSpanOverlapException(timeSpan, this);
            }
        }
    }

    /**
     * Checks t1 and t1 for left overlap, using main to report exceptions.
     */
    private void checkForLeftOverlap(TimeSpan t1, TimeSpan t2, TimeSpan main) {
        if (t1.getStartDate().compareTo(t2.getStartDate()) <= 0
                && t1.getEndDate().compareTo(t2.getStartDate()) > 0) {
            getLogger().debug("Found overlap of " + t1 + " and " + t2);
            throw new TimeSpanOverlapException(main, this);
        }
    }

    /**
     * Returns the cumulative time that has been put into this project.
     */
    public long getTotalTime(boolean isRecursive) {
        return getTimeSince(0, isRecursive);
    }

    /**
     * Returns all the timespan objects in this project.
     * 
     * @return an Set of timespans
     */
    public Set<TimeSpan> getTimeSpans() {
        return _timespans;
    }

    /**
     * Returns the time put into the project since the specified date/time.
     * 
     * @return the time in milliseconds elapsed since the given date/time.
     */
    public long getTimeSince(Date d, boolean isRecursive) {
        return getTimeSince(d.getTime(), isRecursive);
    }

    /**
     * Returns the time put into the project since the specified date/time.
     * 
     * @param rawDate
     *          a long representing the data as seconds since the epoch
     * @return the time in milliseconds elapsed since the given date/time.
     */
    public long getTimeSince(long rawDate, boolean isRecursive) {
        /*
         * NOTE: Look into using headSet and tailSet here.
         */
        long result = 0;

        for (final TimeSpan ts : _timespans)
        {
            /*
             * If d <= end, then this time span makes _some_ contribution.
             */
            if (rawDate <= ts.getEndDate().getTime())
            {
                /*
                 * If d >= start, the span makes a partial contribution, equal
                 * to the time between d and the end date.
                 */
                if (rawDate >= ts.getStartDate().getTime())
                {
                    result += ts.getEndDate().getTime() - rawDate;
                }
                /*
                 * Otherwise, the span makes a full contribution.
                 */
                else
                {
                    result += ts.getLength();
                }
            }
        }
        if (isRecursive) {
            for (final Project eachChild : _children)
            {
                result += eachChild.getTimeSince(rawDate, isRecursive);
            }
        }
        return result;
    }

    /**
     * Returns the time put into the project between the two given intervals.
     * 
     * @return the time in milliseconds.
     */
    public long getTimeBetween(Date start, Date end, boolean isRecursive) {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Start time > end time.");
        }
        /*
         * Candidate for optimization. We essentially get the total time since
         * start, and subtract off the total time since the end.
         */
        return getTimeSince(start, isRecursive) - getTimeSince(end, isRecursive);
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null.");
        }
        _name = name;
    }

    public String getDescription() {
        return _desc;
    }

    public void setDescription(String desc) {
        if (desc == null) {
            throw new IllegalArgumentException("Description may not be null.");
        }
        _desc = desc;
    }

    public String toString() {
        return "LocalProject_" + getId() + "(" + getName() + ")";
    }

    public int hashCode() {
        return getId().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Project)) {
            return false;
        }
        Project p = (Project) o;
        return p.getId().equals(getId());
    }

    public boolean removeTimeSpan(TimeSpan target) {
        return _timespans.remove(target);
    }

    public void addChildProject(Project p) {
        getLogger().debug("Adding " + p);
        _children.add(p);
    }

    public boolean removeChildProject(Project p) {
        return _children.remove(p);
    }

    public void clearChildren() {
        _children.clear();
    }

    public List<Project> getChildren() {
        return _childrenReadOnly;
    }

    public void sortChildren() {
        Collections.sort(_children, Utilities.getInstance().getProjectNameComparator());
    }

    private
    Logger getLogger() {
        if (_logger == null) {
            _logger = LogManager.getLogger(getClass());
        }
        return _logger;
    }

    public ProjectGroup getProjectGroup() {
        return _projectGroup;
    }

    void setProjectGroup(ProjectGroup p) {
        _projectGroup = p;
    }

    public String getAttribute(String domain, String name) {
        return getProjectGroup().getAttribute(this, domain, name);
    }

    public void setAttribute(String domain, String name, String value) {
        getProjectGroup().setAttribute(this, domain, name, value);
    }

    public boolean removeAttribute(String domain, String name) {
        return getProjectGroup().removeAttribute(this, domain, name);
    }

    public Iterator getAttributeDomains() {
        return getProjectGroup().getAttributeDomains(this);
    }

    public Iterator getAttributeKeys(String domain) {
        return getProjectGroup().getAttributeKeys(this, domain);
    }

    private final UUID _uuid;
    private final SortedSet<TimeSpan> _timespans;
    private final List<Project> _children;
    private final List<Project> _childrenReadOnly;

    private String _desc;
    private String _name;
    private ProjectGroup _projectGroup;
    private Logger _logger;
}
