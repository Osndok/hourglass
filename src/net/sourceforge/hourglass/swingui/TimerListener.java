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
 * CVS Revision $Revision: 1.1 $
 * Last modified on $Date: 2003/03/21 05:30:25 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

/**
 * A listener that reacts to timing events from Timer.
 *
 * @author Mike Grant
 */
public interface TimerListener {

  /**
   * Indicates that a new second has elapsed.
   *
   * This method should be able to execute extremely quickly.
   */
  void secondChanged();


  /**
   * Indicates that a new minute has elapsed.
   */
  void minuteChanged();


  /**
   * Indicates that a new hour has elapsed.
   */
  void hourChanged();


  /**
   * Indicates that a new day has elapsed.
   */
  void dayChanged();
    

}
