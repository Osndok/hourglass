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
 * CVS Revision $Revision: 1.2 $
 * Last modified on $Date: 2005/05/08 00:16:22 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

/**
 * The base class for all Hourglass exceptions.
 * 
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public class HourglassRuntimeException extends RuntimeException implements IHourglassException {
	
	private HourglassExceptionBase m_base;
	
	public HourglassRuntimeException() {
		// do nothing
	}

	public HourglassRuntimeException(String message, Throwable cause, String key, String[] args) {
		super(message, cause);
		m_base = new HourglassExceptionBase(key, args);
	}

	public HourglassRuntimeException(Throwable cause, String key, String[] args) {
		this(null, cause, key, args);
	}
	
	public String toString() {
		return HourglassExceptionBase.toString(this);
	}
	
	public String getKey() {
		return m_base.getKey();
	}
	
	public String[] getArgs() {
		return m_base.getArgs();
	}

}
