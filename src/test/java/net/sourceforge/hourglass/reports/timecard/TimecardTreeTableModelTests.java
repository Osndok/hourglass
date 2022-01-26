/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2004 Michael K. Grant <mike@acm.jhu.edu>
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
 * CVS Revision $Revision: 1.1 $
 * Last modified on $Date: 2004/08/13 02:58:43 $ by $Author: mgrant79 $
 */
package net.sourceforge.hourglass.reports.timecard;

import java.util.Date;

import net.sourceforge.hourglass.HourglassTestCase;
import net.sourceforge.hourglass.framework.DateUtilities;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;

/**
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class TimecardTreeTableModelTests extends HourglassTestCase {
	
	private TimecardTreeTableModel m_model;
	private ProjectGroup m_sampleDataHier;

	public TimecardTreeTableModelTests(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		Date dateStart = createDate("2002-12-01T00:00:00.000", "-06:00");
		Date dateEnd = createDate("2002-12-03T00:00:00.000", "-06:00");
		m_sampleDataHier = getSampleDataHier();
		m_model = new TimecardTreeTableModel(m_sampleDataHier, 
				dateStart, dateEnd, DateUtilities.DECIMAL_HOUR_FORMATTER); 
	}
	
	public void testSummaryRow() {
		assertEquals(2, m_model.getChildCount(TimecardTreeTableModel.TREE_ROOT));
		assertEquals("Project 1",
				((Project) m_model.getChild(TimecardTreeTableModel.TREE_ROOT, 0)).getName());
		assertEquals(m_sampleDataHier.getRootProject(), 
				m_model.getChild(TimecardTreeTableModel.TREE_ROOT, 1));		
	}
	
}
