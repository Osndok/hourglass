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
 * Last modified on $Date: 2005/05/08 00:16:22 $ by $Author: mgrant79 $
 */
package net.sourceforge.hourglass.framework;

/**
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class HourglassExceptionBase {

	private String m_key;

	private String[] m_args;

	public HourglassExceptionBase() {
		// do nothing
	}

	public HourglassExceptionBase(String key, String[] args) {
		m_key = key;
		m_args = args;
	}

	public String getKey() {
		return m_key;
	}

	public String[] getArgs() {
		return m_args;
	}

	public static String toString(IHourglassException self) {
		StringBuffer result = new StringBuffer(self.getClass().getName());
		String message = self.getLocalizedMessage();
		if (self.getKey() != null) {
			result.append("(").append(self.getKey()).append(")");
		}
		if (self.getArgs() != null) {
			result.append(" [").append(Utilities.join(self.getArgs(), ", ")).append(
					"]");
		}
		if (message != null) {
			result.append(": " + self.getLocalizedMessage());
		}

		return result.toString();

	}

}
