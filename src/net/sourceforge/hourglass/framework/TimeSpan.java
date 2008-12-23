/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Portions Copyright (C) 2003 Neil Thier <nthier@alumni.uwaterloo.edu>
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
 * Last modified on $Date: 2003/03/21 05:30:25 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;


/**
 * A time span, represented by a start and end date.
 *
 * The general contract of a TimeSpan is that neither the start nor
 * the end date may be null, and the end date may not be before the
 * start date (although the end date may be exactly the start date).
 *
 * @author Mike Grant
 * @author Neil Thier
 */
public class TimeSpan implements Comparable {
  

  /**
   * Creates a TimeSpan from startDate to endDate.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @throws IllegalArgumentException if the contract not met.
   */
  public TimeSpan(Date startDate, Date endDate) {

    checkConstructorArguments(startDate, endDate);
    _startDate = startDate;
    _endDate = endDate;
  }


  /**
   * Checks that the arguments to the constructor meet the contract.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @throws IllegalArgumentException if the contract is not met.
   */
  public void checkConstructorArguments(Date startDate, Date endDate) {

    if (startDate == null) {
      throw new IllegalArgumentException("Start date cannot be null.");
    }
    else if (endDate == null) {
      throw new IllegalArgumentException("End date cannot be null.");
    }
    else if (endDate.getTime() < startDate.getTime()) {
      throw new IllegalArgumentException
        ("End date can not be before start date.");
    }
  }


  /**
   * Returns the length of the time span in milliseconds.
   */
  public long getLength() {
    return _endDate.getTime() - _startDate.getTime();
  }


  /**
   * Returns the start date.
   */
  public Date getStartDate() {
    return _startDate;
  }


  /**
   * Returns the end date.
   */
  public Date getEndDate() {
    return _endDate;
  }


  public boolean equals(Object o) {
    if (o instanceof TimeSpan) {
      return compareTo(o) == 0;
    }
    else {
      return false;
    }
  }  

  /**
   * Checks if this timespan is from the specified date
   *
   * @param d the date to check
   * @return true iff d has the same day, month and year of the timespan
   */
  public boolean isFromDate(Date d) {
    Calendar start = new GregorianCalendar();
    start.setTime(_startDate);
    Calendar end = new GregorianCalendar();
    end.setTime(_endDate);
    Calendar other = new GregorianCalendar();
    other.setTime(d);

    // since the contract of TimeSpan doesn't say that the start and end
    // must be the same day, must check both
    return 
      ((start.get(Calendar.DAY_OF_MONTH) == 
	other.get(Calendar.DAY_OF_MONTH)) &&
      (start.get(Calendar.MONTH) == other.get(Calendar.MONTH)) &&
      (start.get(Calendar.YEAR) == other.get(Calendar.YEAR)))
      ||
      ((end.get(Calendar.DAY_OF_MONTH) == 
	other.get(Calendar.DAY_OF_MONTH)) &&
      (end.get(Calendar.MONTH) == other.get(Calendar.MONTH)) &&
      (end.get(Calendar.YEAR) == other.get(Calendar.YEAR)));
  }
  
  /**
   * Compares this TimeSpan to another one.
   *
   * Returns a negative, zero, or positive result if this time span is
   * less than, equal to, or greater than, respectively, the TimeSpan
   * passed as a parameter.
   *
   * Time spans are equal if and only if their start and end dates are
   * equal.  A time span, t1, is less than another, t2, if either t1's
   * start date is less than t2's, or if t1 and t2 have the same start
   * date, but t1 has a lesser end date.  In any other case, t1 is
   * defined to be greater than t2.
   * 
   * @param o the TimeSpan to compare to this one.
   * @throws ClassCastException if o is not a TimeSpan.  
   */
  public int compareTo(Object o) {
    TimeSpan ts = (TimeSpan) o;
    int start = getStartDate().compareTo(ts.getStartDate());
    if (start != 0) {
      return start;
    }
    
    return getEndDate().compareTo(ts.getEndDate());
  }


  /**
   * Returns the hash code of this time span.
   *
   * The hash is computed as follows.  Let <code>x</code> be the
   * product of the start date and the end date of this time span,
   * where both dates are taken in milliseconds since the ephoc.  The
   * hash code is the XOR of the low 32 bits of <code>x</code> with
   * the high 32 bits of <code>x</code>.
   */
  public int hashCode() {
    long product = (_startDate.getTime() * _endDate.getTime());
    return (int) (product ^ (product >>> 32));
  }

  public String toString() {
    return "TimeSpan(" + getStartDate() + ", " + getEndDate() + ")";
  }

  private Date _startDate;
  private Date _endDate;

}
