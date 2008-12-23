/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Neil Thier <nthier@alumni.uwaterloo.ca>
 * Portions Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
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
 * Last modified on $Date: 2004/06/27 07:51:42 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Defines commonly used date operations.
 * 
 * @author Neil Thier
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class DateUtilities {

	public static final String SHORT_DATE_FORMAT_STRING = "MM/dd/yyyy";
	public static final long MS_PER_MINUTE = 60000;
	public static final long MS_PER_HOUR = MS_PER_MINUTE * 60;

	public static abstract class TimeFormatter {
		public abstract String formatTime(long ms);
	}

	public static final TimeFormatter DECIMAL_HOUR_FORMATTER = new TimeFormatter() {
		private NumberFormat _numberFormat;
		{
			_numberFormat = NumberFormat.getInstance();
			_numberFormat.setMaximumFractionDigits(2);
			_numberFormat.setMinimumFractionDigits(2);
		}
		public String formatTime(long ms) {
			double hours = (double) ms / (double) MS_PER_HOUR;
			return _numberFormat.format(hours);
		}
	};
	
	public static final TimeFormatter HOUR_MINUTE_FORMATTER = new TimeFormatter() {
		public String formatTime(long ms) {
			// TODO [MGT, 25 Oct 2003]: Taken from DetailPanel--refactor.
			long hours = ms / MS_PER_HOUR;
			long minutes = (ms - hours * MS_PER_HOUR) / MS_PER_MINUTE;

			return ((hours < 10) ? "0" : "") + hours + ":"
					+ ((minutes < 10) ? "0" : "") + minutes;
		}
	};

	public static Date getBeginningOfDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();
	}

	public static Date getEndOfDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(getBeginningOfDay(date));
		c.add(Calendar.DATE, 1);
		c.add(Calendar.MILLISECOND, -1);
		return c.getTime();
	}

	/**
	 * Returns the most recent date prior to the date specified and having the
	 * specified day of week. I.e. "get last sunday" or "get last monday."
	 */
	public static Date getMostRecentlyOccuringDay(Date date, int dayOfWeek) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		if (date.before(c.getTime())) {
			c.add(Calendar.WEEK_OF_YEAR, -1);
			return c.getTime();
		} else {
			return c.getTime();
		}
	}

	public static Date getBeginningOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();
	}

	public static Date getBeginningOfYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_YEAR, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();
	}

	public static Date getCurrentWeekStart() {
		return getMostRecentlyOccuringDay(new Date(), Calendar.MONDAY);
	}

	public static Date getCurrentWeekEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getCurrentWeekStart());
		calendar.add(Calendar.DATE, 6);
		Date endDate = calendar.getTime();
		return endDate;
	}

	public static DateFormat createShortDateFormat() {
		return new SimpleDateFormat(SHORT_DATE_FORMAT_STRING);
	}

}

