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
 * CVS Revision $Revision: 1.3 $
 * Last modified on $Date: 2005/05/02 00:04:13 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A timer object that fires regular timing events.
 *
 * @author Mike Grant
 */
public class Timer extends Thread {


  /**
   * Constructs a new timer.
   */
  public Timer() {
    _listeners = new ArrayList();
    _running = true;
    _calendar = new GregorianCalendar();
    _lastMinute = FIELD_UNDEFINED;
    _lastHour = FIELD_UNDEFINED;
    _lastDay = FIELD_UNDEFINED;
  }


  /**
   * Adds a listener to this timer.
   */
  public synchronized void addTimerListener(TimerListener tl) {
    _listeners.add(tl);
  }


  /**
   * Removes a listener from this timer.
   */
  public synchronized void removeTimerListener(TimerListener tl) {
    _listeners.remove(tl);
  }

  private void fireEvents() {
    _calendar.setTime(new Date());
    int currentMinute = _calendar.get(Calendar.MINUTE);
    int currentHour = _calendar.get(Calendar.HOUR_OF_DAY);
    int currentDay = _calendar.get(Calendar.DAY_OF_YEAR);

    if (_lastMinute == FIELD_UNDEFINED) {
      _lastMinute = currentMinute;
    }
    if (_lastHour == FIELD_UNDEFINED) {
      _lastHour = currentHour;
    }
    if (_lastDay == FIELD_UNDEFINED) {
      _lastDay = currentDay;
    }      
    
    Iterator i = _listeners.iterator();
    while (i.hasNext()) {
      TimerListener tl = (TimerListener) i.next();
      tl.secondChanged();
      if (currentMinute != _lastMinute) {
        tl.minuteChanged();
      }
      if (currentHour != _lastHour) {
        tl.hourChanged();
      }
      if (currentDay != _lastDay) {
        tl.dayChanged();
      }
    }

    _lastDay = currentDay;
    _lastHour = currentHour;
    _lastMinute = currentMinute;
  }


  public void run() {
    getLogger().debug("Timer starting.");
    while (_running) {
      fireEvents();
      try {
        sleep(ONE_SECOND);
      }
      catch (InterruptedException e) {
        getLogger().debug("Timer interrupted.");
        // swing is shutting down, DO NOT open a window to present this exception to the user
      }
    }
  }


  public void scheduleCleanStop() {
    getLogger().debug("Scheduling timer thread to stop");
    _running = false;
    interrupt();
  }


  private
  Logger getLogger() {
    if (_logger == null) {
      _logger = LogManager.getLogger(getClass());
    }
    return _logger;
  }
      
  private Calendar _calendar;

  private static final long ONE_SECOND = 1000;
  private static final int FIELD_UNDEFINED = -1;
  private int _lastMinute;
  private int _lastHour;
  private int _lastDay;

  private List _listeners;
  private boolean _running;
  private Logger _logger;
}
