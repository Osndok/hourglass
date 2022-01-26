/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2004 Michael K. Grant <mike@localhost>
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
 * Last modified on $Date: 2005/05/02 00:04:14 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.hourglass.HourglassTestCase;

/**
 * Basic project tests.
 *
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public abstract class ProjectTests extends HourglassTestCase {
    
    private ProjectFactory m_projectFactory;
    
    protected abstract ProjectFactory createProjectFactory();
    
    public void setUp() throws Exception {
        m_projectFactory = createProjectFactory();
    }
    
    public ProjectFactory getProjectFactory() {
        return m_projectFactory;
    }
    
    public void testAttributes() {
        ProjectGroup group = getProjectFactory().createProjectGroup();
        Project p1 = getProjectFactory().createProject(group);
        Project p2 = getProjectFactory().createProject(group);
        
        assertNull(p1.getAttribute("domain", "name"));
        assertNull(p2.getAttribute("domain", "name"));
        
        p1.setAttribute("domain", "name", "value");
        assertEquals("value", p1.getAttribute("domain", "name"));
        assertNull(p2.getAttribute("domain", "name"));
        
        p1.setAttribute("domain", "name2", "value2");
        assertEquals("value2", p1.getAttribute("domain", "name2"));
        assertEquals("value", p1.getAttribute("domain", "name"));
        assertNull(p1.getAttribute("domain2", "name"));
        assertNull(p1.getAttribute("domain2", "name2"));
        
        p1.setAttribute("domain", "name2", "valuenew");        
        assertEquals("valuenew", p1.getAttribute("domain", "name2"));
        
        p1.setAttribute("domain2", "name", "value3");
        assertEquals("value3", p1.getAttribute("domain2", "name"));
        assertEquals("value", p1.getAttribute("domain", "name"));
        
        assertTrue(p1.removeAttribute("domain2", "name"));
        assertNull(p1.getAttribute("domain2", "name"));
        assertFalse(p1.removeAttribute("domain2", "name"));
    }
    
    public void testDomainAndAttributeIterators() {
        ProjectGroup group = getProjectFactory().createProjectGroup();
        Project p = getProjectFactory().createProject(group);
        
        assertFalse(p.getAttributeDomains().hasNext());
        assertFalse(p.getAttributeKeys("dummy_domain").hasNext());
        
        p.setAttribute("domain", "key1", "value1");
        p.setAttribute("domain", "key2", "value2");
        p.setAttribute("domain", "key3", "value3");
        p.setAttribute("domain2", "key4", "value4");
        
        assertIterates(new String[] { "domain", "domain2" }, p.getAttributeDomains());
        assertIterates(new String[] { "key1", "key2", "key3" }, p.getAttributeKeys("domain"));
        assertIterates(new String[] { "key4" }, p.getAttributeKeys("domain2"));
        
        // When the domain is empty, it should disappear.
        p.removeAttribute("domain2", "key4");
        assertIterates(new String[] { "domain" }, p.getAttributeDomains());
    }
    
    private void assertIterates(String[] expected, Iterator actual) {
        List expList = new ArrayList(Arrays.asList(expected));
        while (actual.hasNext()) {
            assertTrue(expList.remove(actual.next()));
        }
        assertEquals(0, expList.size());
    }
    
    
    
}
