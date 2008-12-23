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
 * Thrown when a timespan overlaps some other timespan in the project
 * to which it is being added.
 *
 * @author Mike Grant <mike@acm.jhu.edu> 
 */
public class TimeSpanOverlapException extends HourglassRuntimeException {


  /**
   * Constructs a TimeSpanOverlapException with the given timespan and
   * project.
   *
   * @param timeSpan the <code>TimeSpan</code> that caused the violation
   */
  public TimeSpanOverlapException(TimeSpan timeSpan, Project project) {
  	super(timeSpan + "overlaps another TimeSpan.", null, ErrorKeys.ERROR_KEY_TIME_SPAN_OVERLAP, null);
    _timeSpan = timeSpan;
    _project = project;
  }


  /**
   * Returns the <code>TimeSpan</code> that caused the violation.
   */
  public TimeSpan getTimeSpan() {
    return _timeSpan;
  }


  /**
   * Returns the <code>Project</code> in which the violation was
   * caused.
   */
  public Project getProject() {
    return _project;
  }


  private TimeSpan _timeSpan;
  private Project _project;
}
