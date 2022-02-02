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
 * Last modified on $Date: 2003/08/09 06:42:19 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A DateFormat for the ISO 8601 standard.
 *
 * This date format is not thread-safe.
 *
 * @author Mike Grant
 */
public class ISO8601DateFormat extends DateFormat {

  /**
   * Creates a new ISO8601DateFormat
   */
  public ISO8601DateFormat() {
    _dateFormatDelegate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
  }

  
  public StringBuffer format(Date date, 
                             StringBuffer appendTo, 
                             FieldPosition fieldPos) {

    String delegateResult = _dateFormatDelegate.format(date);
    int offset = getTimezoneOffsetInMinutes(date);

    appendTo.append(delegateResult);
    appendTo.append(getSign(offset));
    appendTo.append(getHours(offset)).append(':');
    appendTo.append(getMinutes(offset));

    return appendTo;
  }


  private String getHours(int minutes) {
    int result = Math.abs(minutes) / 60;

    /*
     * If single digit, append a zero to the beginning.
     */
    return ((result >= 10) ? "" : "0") + result;
  }

  private char getSign(int minutes) {
    return (minutes < 0) ? '-' : '+';
  }

  private String getMinutes(int minutes) {
    int result = Math.abs(minutes) % 60;

    /*
     * If single digit, append a zero to the beginning.
     */
    return ((result >= 10) ? "" : "0") + result;
  }


  protected Calendar getDelegateCalendar() {
    if (_calendar == null) {
      _calendar = new GregorianCalendar();
    }
    return _calendar;
  }


  /**
   * Returns the timezone offset of the given date for <b>this</b> timezone.
   *
   * @return the offset in minutes
   */
  private int getTimezoneOffsetInMinutes(Date d) {
    final Calendar c = getDelegateCalendar();
    
    c.setTime(d);
    int result = c.getTimeZone().getOffset
      (c.get(Calendar.ERA),
       c.get(Calendar.YEAR),
       c.get(Calendar.MONTH),
       c.get(Calendar.DAY_OF_MONTH),
       c.get(Calendar.DAY_OF_WEEK),
       c.get(Calendar.MILLISECOND)) / MSECONDS_PER_MINUTE;

    return result;
  }


  /**
   * Returns the timezone offset specified by the date string
   *
   * @return the offset in milliseconds
   */
  private int getTimezoneOffsetInMilliseconds(String s) {
    String sign = s.substring(s.length() - 6, s.length() - 5);
    String hourStr = s.substring(s.length() - 5, s.length() - 3);
    String minStr = s.substring(s.length() - 2, s.length());

    int result = 
      (Integer.parseInt(hourStr) * 60 +
       Integer.parseInt(minStr)) *
      (sign.equals("-") ? -60000 : 60000);

    return result;
  }


  private String removeTimezone(String s) {
    String result = s.substring(0, s.length() - 6);
    return result;
  }


  public Date parse(String s, ParsePosition parsePos) {
    if (parsePos.getIndex() != 0) {
      getLogger().error("Nonzero parse index.");
      return null;
    }

    int offset = getTimezoneOffsetInMilliseconds(s);
    String delegateString = removeTimezone(s);

    try {
      Date tmpDate = _dateFormatDelegate.parse(delegateString);
      /*
       * The result is the parsed date, minux the TZ offset, plus our offset.
       */
      Date result = new Date
        (tmpDate.getTime() - offset +
         getTimezoneOffsetInMinutes(tmpDate) * 60000);

      parsePos.setIndex(s.length());
      return result;
    }
    catch (ParseException pex) {
      getLogger().error("ParseException in delegate:", pex);
      return null;
    }
  }

  @Deprecated
  private
  Logger getLogger()
  {
    return log;
  }


  private DateFormat _dateFormatDelegate;
  private final Logger log = LogManager.getLogger(getClass());
  private Calendar _calendar;

  private static final int MSECONDS_PER_MINUTE = 60000;

}
