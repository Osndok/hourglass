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
 * Last modified on $Date: 2005/05/09 04:02:49 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass;

/**
 * Constants for the Swing UI.
 */
// TODO [MGT; May 1, 2005]: Move to parent package (in next non-patch release).
public interface Constants {

	public final String HOURGLASS_DIR_RELATIVE = ".hourglass";

	public final String HOURGLASS_HOME_PROP = "hg.home";
	public static final String ARCHIVE_NAME_PROP = "hg.archive.name";

	final String DEFAULT_ARCHIVE_NAME = "data";

	public static final String SWINGUI_RESOURCE_BUNDLE = "net/sourceforge/hourglass/swingui/resources";

	public static final int ERROR_DIALOG_COLUMN_WIDTH = 60;

	public static final String EMPTY_STRING = "";

	public static final String SPACE = " ";

}
