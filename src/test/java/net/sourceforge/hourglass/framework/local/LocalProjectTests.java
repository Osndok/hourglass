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
 * Last modified on $Date: 2008/10/25 15:22:52 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework.local;

import java.text.ParseException;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.hourglass.TestUtilities;
import net.sourceforge.hourglass.framework.MutableProject;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectFactory;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.ProjectTests;
import net.sourceforge.hourglass.framework.TimeSpan;
import net.sourceforge.hourglass.framework.TimeSpanOverlapException;

import java.util.UUID;


/**
 * Unit tests for LocalProject
 * 
 * @author Mike Grant
 */
public class LocalProjectTests extends ProjectTests {

    public static Test suite() {
        TestSuite result = new TestSuite(LocalProjectTests.class);
        result.setName("Local project tests");
        return result;
    }

    protected ProjectFactory createProjectFactory() {
        return new LocalProjectFactory();
    }

    public void setUp() throws Exception {
        super.setUp();
        _projectGroup = (LocalProjectGroup) getProjectFactory().createProjectGroup();
        _project = (LocalProject) getProjectFactory().createProject(_projectGroup);
        initializeOverlapData();
        addTimeSpans(_project);
        TestUtilities.getInstance().clearCaches();
        initializeHierProjects();
    }

    public void initializeHierProjects() throws Exception {
        hierProjects = new LocalProject[5];
        ProjectGroup pg = getSampleDataHier();

        hierProjects[0] = (LocalProject) pg.getProject(UUID.fromString(
                "66a04623-056e-11d7-b289-e0cd170f00c2"));
        hierProjects[1] = (LocalProject) pg.getProject(UUID.fromString(
                "07502364-056f-11d7-b289-e0cd170f00c2"));
        hierProjects[2] = (LocalProject) pg.getProject(UUID.fromString(
                "6cfdea2b-5d9c-11d7-8b65-9ca753b1b6a8"));
        hierProjects[3] = (LocalProject) pg.getProject(UUID.fromString(
                "6d00341c-5d9c-11d7-8b65-9ca753b1b6a8"));
        hierProjects[4] = (LocalProject) pg.getProject(UUID.fromString(
                "6d00f76d-5d9c-11d7-8b65-9ca753b1b6a8"));
        for (int i = 0; i < 5; ++i) {
            assertNotNull(hierProjects[i]);
        }
    }

    /**
     * Adds sample time spans to the given project.
     * 
     * The time spans total 25.5 hours.
     */
    private void addTimeSpans(MutableProject p) throws ParseException {
        p.addTimeSpan(new TimeSpan(createDate("2002-11-20T01:00:00.000"),
                createDate("2002-11-20T01:30:00.000")));
        p.addTimeSpan(new TimeSpan(createDate("2002-11-20T02:00:00.000"),
                createDate("2002-11-20T03:30:00.000")));
        p.addTimeSpan(new TimeSpan(createDate("2002-11-21T01:00:00.000"),
                createDate("2002-11-22T00:30:00.000")));
    }

    /**
     * Tests that two projects do not have the same ID.
     */
    public void testUniqueId() {
        Project p1 = getProjectFactory().createProject(_projectGroup);
        Project p2 = getProjectFactory().createProject(_projectGroup);
        assertFalse("IDs should not be the same.", p1.getId() == p2.getId());
    }

    /**
     * Tests the getTotalTime() method.
     */
    public void testTotalTime() {
        assertEquals(91800000, _project.getTotalTime(false));
    }

    /**
     * Tests the getTotalTime() method in recursive mode.
     */
    public void testTotalTimeRecursive() {
        assertEquals(1845, hierProjects[0].getTotalTime(true));
        assertEquals(369, hierProjects[1].getTotalTime(true));
        assertEquals(1107, hierProjects[2].getTotalTime(true));
        assertEquals(369, hierProjects[3].getTotalTime(true));
        assertEquals(369, hierProjects[4].getTotalTime(true));
    }

    /**
     * Tests the getTimeSince() method in recursive mode.
     */
    public void testTimeSinceRecursive() throws ParseException {
        Date d = createDate("2002-12-01T01:00:00.069", "-06:00");
        assertEquals(1500, hierProjects[0].getTimeSince(d, true));
        assertEquals(300, hierProjects[1].getTimeSince(d, true));
        assertEquals(900, hierProjects[2].getTimeSince(d, true));
        assertEquals(300, hierProjects[3].getTimeSince(d, true));
        assertEquals(300, hierProjects[4].getTimeSince(d, true));
    }

    /**
     * Tests the getTimeBetween() method in recursive mode.
     */
    public void testTimeBetweenRecursive() throws ParseException {
        Date d1 = createDate("2002-12-01T01:00:00.069", "-06:00");
        Date d2 = createDate("2002-12-01T03:00:00.073", "-06:00");
        assertEquals(1250, hierProjects[0].getTimeBetween(d1, d2, true));
        assertEquals(250, hierProjects[1].getTimeBetween(d1, d2, true));
        assertEquals(750, hierProjects[2].getTimeBetween(d1, d2, true));
        assertEquals(250, hierProjects[3].getTimeBetween(d1, d2, true));
        assertEquals(250, hierProjects[4].getTimeBetween(d1, d2, true));
    }

    /**
     * Tests the getTimeSince() method.
     */
    public void testTimeSince() throws ParseException {
        assertEquals(1800000, _project.getTimeSince(createDate("2002-11-22T00:00:00.000"), false));
    }

    /**
     * Tests the getTimeBetween() method.
     */
    public void testTimeBetween() throws ParseException {
        Date before = createDate("2002-11-19T00:00:00.000");
        Date after = createDate("2002-11-30T00:00:00.000");
        Date start = createDate("2002-11-20T01:00:00.000");
        Date end = createDate("2002-11-22T00:30:00.000");
        Date betw1_2 = createDate("2002-11-20T01:45:00.000");
        Date betw2_3 = createDate("2002-11-21T00:00:00.000");
        Date inside1 = createDate("2002-11-20T01:15:00.000");
        Date inside2 = createDate("2002-11-20T03:00:00.000");
        assertEquals(91800000, _project.getTimeBetween(before, after, false));
        assertEquals(91800000, _project.getTimeBetween(start, after, false));
        assertEquals(91800000, _project.getTimeBetween(before, end, false));
        assertEquals(91800000, _project.getTimeBetween(start, end, false));
        assertEquals(1800000, _project.getTimeBetween(before, betw1_2, false));
        assertEquals(7200000, _project.getTimeBetween(before, betw2_3, false));
        assertEquals(5400000, _project.getTimeBetween(betw1_2, betw2_3, false));
        assertEquals(90000000, _project.getTimeBetween(betw1_2, after, false));
        assertEquals(84600000, _project.getTimeBetween(betw2_3, end, false));
        assertEquals(900000, _project.getTimeBetween(before, inside1, false));
        assertEquals(5400000, _project.getTimeBetween(before, inside2, false));
        assertEquals(4500000, _project.getTimeBetween(inside1, inside2, false));
        assertEquals(90900000, _project.getTimeBetween(inside1, end, false));
        assertEquals(86400000, _project.getTimeBetween(inside2, after, false));
        assertEquals(1800000, _project.getTimeBetween(inside2, betw2_3, false));
        assertEquals(0, _project.getTimeBetween(betw2_3, betw2_3, false));
        try {
            _project.getTimeBetween(inside2, betw1_2, false);
            fail("start > end did not throw IllegalArgumentException.");
        }
        catch (IllegalArgumentException iaex) {
            // should get here
        }
    }

    /**
     * Tests the <code>removeTimeSpan()</code> method
     * 
     * @see MutableProject#removeTimeSpan(TimeSpan)
     */
    public void testRemoveTimeSpan() throws ParseException {
        TimeSpan newTimeSpan = new TimeSpan(createDate("2003-02-21T00:00:00.000"),
                createDate("2003-02-21T00:01:00.000"));
        TimeSpan existingTimeSpan = new TimeSpan(createDate("2002-11-20T02:00:00.000"),
                createDate("2002-11-20T03:30:00.000"));
        assertFalse(_project.removeTimeSpan(newTimeSpan));
        assertTrue(_project.removeTimeSpan(existingTimeSpan));
        assertEquals(86400000, _project.getTotalTime(false));
        _project.addTimeSpan(newTimeSpan);
        assertEquals(86460000, _project.getTotalTime(false));
        assertTrue(_project.removeTimeSpan(newTimeSpan));
        assertEquals(86400000, _project.getTotalTime(false));
        assertFalse(_project.removeTimeSpan(newTimeSpan));
    }

    /**
     * Test the <code>checkForOverlap()</code> method.
     * 
     * @see LocalProject#checkForOverlap(TimeSpan)
     */
    public void testCheckForOverlap() {
        for (int i = 0; i < 5; ++i) {
            try {
                _project.checkForOverlap(_overlap[i]);
                fail("_overlap[" + i + "] did not throw TimeSpanOverlapException.");
            }
            catch (TimeSpanOverlapException tsex) {
                assertEquals(_overlap[i], tsex.getTimeSpan());
                assertEquals(_project, tsex.getProject());
            }
        }
        /*
         * Will throw a runtime TimeSpanOverlapException if it fails.
         */
        _project.checkForOverlap(_nonOverlap);
    }

    /**
     * Tests that zero-length duplicate timespans overlap.
     */
    public void testCheckForZeroLenDuplicateOverlap() throws ParseException {
        TimeSpan ts = new TimeSpan(createDate("2003-02-26T01:00:00.000"),
                createDate("2003-02-26T01:00:00.000"));
        _project.addTimeSpan(ts);
        try {
            _project.addTimeSpan(ts);
            fail("Zero-len duplicate does not throw TimeSpanOverlapException.");
        }
        catch (TimeSpanOverlapException tsex) {
            assertEquals(ts, tsex.getTimeSpan());
            assertEquals(_project, tsex.getProject());
        }
    }

    /**
     * Test that the <code>addTimeSpan()</code> method deals with
     * TimeSpanOverlapExceptions appropriately.
     * 
     * @see LocalProject#addTimeSpan(TimeSpan)
     */
    public void testAddTimeSpanOverlapExceptions() {
        for (int i = 0; i < 5; ++i) {
            try {
                _project.addTimeSpan(_overlap[i]);
                fail("_overlap[" + i + "] did not throw TimeSpanOverlapException.");
            }
            catch (TimeSpanOverlapException tsex) {
                assertEquals(_overlap[i], tsex.getTimeSpan());
                assertEquals(_project, tsex.getProject());
            }
        }
        /*
         * Will throw a runtime TimeSpanOverlapException if it fails.
         */
        _project.addTimeSpan(_nonOverlap);
        assertEquals(93600000, _project.getTotalTime(false));
    }

    private void initializeOverlapData() throws ParseException {
        _overlap = new TimeSpan[6];
        /*
         * Exact fit
         */
        _overlap[0] = new TimeSpan(createDate("2002-11-20T01:00:00.000"),
                createDate("2002-11-20T01:30:00.000"));
        /*
         * Offset to right.
         */
        _overlap[1] = new TimeSpan(createDate("2002-11-20T02:15:00.000"),
                createDate("2002-11-20T03:45:00.000"));
        /*
         * Offset to left
         */
        _overlap[2] = new TimeSpan(createDate("2002-11-20T00:15:00.000"),
                createDate("2002-11-20T01:15:00.000"));
        /*
         * Contained
         */
        _overlap[3] = new TimeSpan(createDate("2002-11-20T01:05:00.000"),
                createDate("2002-11-20T01:10:00.000"));
        /*
         * Multiple
         */
        _overlap[4] = new TimeSpan(createDate("2002-11-20T00:00:00.000"),
                createDate("2002-11-20T04:00:00.000"));
        /*
         * Non-overlapping, but at bounds.
         */
        _nonOverlap = new TimeSpan(createDate("2002-11-20T01:30:00.000"),
                createDate("2002-11-20T02:00:00.000"));
    }

    public void testNoNullNameAndDescription() {
        LocalProject p = (LocalProject) getProjectFactory().createProject(_projectGroup);
        assertNotNull(p.getName());
        assertNotNull(p.getDescription());
        try {
            p.setName(null);
            fail("Allowed to set null name.");
        }
        catch (IllegalArgumentException iaex) {
            // should get here.
        }
        try {
            p.setDescription(null);
            fail("Allowed to set null name.");
        }
        catch (IllegalArgumentException iaex) {
            // should get here.
        }
    }

    private LocalProject _project;
    private LocalProject[] hierProjects;
    private LocalProjectGroup _projectGroup;
    private TimeSpan[] _overlap;
    private TimeSpan _nonOverlap;
}
