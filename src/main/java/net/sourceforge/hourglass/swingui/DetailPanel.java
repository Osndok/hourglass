/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Copyright (C) 2009 Eric Lavarde <ewl@users.sourceforge.net>
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
 * CVS Revision $Revision: 1.16 $
 * Last modified on $Date: 2006/02/14 20:33:41 $ by $Author: nthier $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.sourceforge.hourglass.framework.DateUtilities;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.ProjectGroupListener;
import net.sourceforge.hourglass.framework.TimeSpan;

/**
 * Displays detailed stats on projects.
 * 
 * @author Mike Grant
 */
public class DetailPanel extends JPanel implements ClientStateListener,
		ProjectGroupListener, TimerListener {

	public DetailPanel() {
		super(new BorderLayout());
		_labels = new JLabel[5][2];
		initializeComponents();
	}

	private void initializeComponents() {
		JPanel inner = new JPanel(new GridBagLayout());

		GridBagConstraints c_lbl = new GridBagConstraints();
		c_lbl.anchor = GridBagConstraints.WEST;
		c_lbl.fill = GridBagConstraints.NONE;
		c_lbl.insets = new Insets(0, 3, 0, 3);
		GridBagConstraints c_mid = new GridBagConstraints();
		c_mid.insets = new Insets(0, 3, 0, 3);
		GridBagConstraints c_end = new GridBagConstraints();
		c_end.gridwidth = GridBagConstraints.REMAINDER;
		c_end.insets = new Insets(0, 3, 0, 3);

		GridBagConstraints c_sep = new GridBagConstraints();
		c_sep.gridwidth = GridBagConstraints.REMAINDER;
		c_sep.fill = GridBagConstraints.HORIZONTAL;

		inner.setBorder(BorderFactory.createLoweredBevelBorder());

		inner.add(new JSeparator(), c_sep.clone());

		inner.add(getLabel(Strings.PROJECT_COLON), c_lbl.clone());
		inner.add(getLabel(Strings.THIS), c_mid.clone());
		inner.add(getLabel(Strings.ALL), c_end.clone());

		inner.add(new JSeparator(), c_sep.clone());

		inner.add(getLabel(Strings.TODAY_COLON), c_lbl.clone());
		inner.add(getTimeLabel(TODAY, THIS), c_mid.clone());
		inner.add(getTimeLabel(TODAY, ALL), c_end.clone());

		inner.add(getLabel(Strings.THIS_WEEK_COLON), c_lbl.clone());
		inner.add(getTimeLabel(WEEK, THIS), c_mid.clone());
		inner.add(getTimeLabel(WEEK, ALL), c_end.clone());

		inner.add(getLabel(Strings.THIS_MONTH_COLON), c_lbl.clone());
		inner.add(getTimeLabel(MONTH, THIS), c_mid.clone());
		inner.add(getTimeLabel(MONTH, ALL), c_end.clone());

		inner.add(getLabel(Strings.THIS_YEAR_COLON), c_lbl.clone());
		inner.add(getTimeLabel(YEAR, THIS), c_mid.clone());
		inner.add(getTimeLabel(YEAR, ALL), c_end.clone());

		inner.add(getLabel(Strings.TOTAL_COLON), c_lbl.clone());
		inner.add(getTimeLabel(TOTAL, THIS), c_mid.clone());
		inner.add(getTimeLabel(TOTAL, ALL), c_end.clone());

		add(inner, BorderLayout.CENTER);
	}

	private JLabel getLabel(String key) {
		return new JLabel(gu().getString(key));
	}

	private JLabel getTimeLabel(int time, int type) {
		if (_labels[time][type] == null) {
			_labels[time][type] = new JLabel("00:00");
			_labels[time][type].setFont(_labels[time][type].getFont()
					.deriveFont(Font.PLAIN));
		}
		return _labels[time][type];
	}

	public void secondChanged() {
	}

	public void hourChanged() {
	}

	public void dayChanged() {
	}

	public void minuteChanged() {
		updateStatistics();
	}

	public void projectsChanged(Project parent) {
		updateStatistics();
	}

	public void projectGroupChanged(ProjectGroup projects) {
		updateStatistics();

		// This is a little bit ugly, but mostly harmless.
		// If we don't do this, we
		// won't be listening to new projects as they're loaded.
		projects.addProjectGroupListener(this);
	}

	public void projectAttributesChanged(Project p) {
	}

	public void newTimeSpanStarted(Date d) {
	}

	public void currentTimeSpanStopped(TimeSpan ts) {
	}

	public void currentTimeSpanAborted() {
		updateStatistics();
	}

	public void selectedProjectChanged(Project p) {
		updateActiveStatistics();
	}

	private void updateStatistics() {
		updateActiveStatistics();
		updateTotalStatistics();
	}

	private void updateTotalStatistics() {
		/*
		 * Implement.
		 */
	}

	private void updateActiveStatistics() {
                Date now = new Date();

		// update the totals for this project
		if (getActiveProject() == null) {
			getTimeLabel(TOTAL, THIS).setText("00:00");
			getTimeLabel(TODAY, THIS).setText("00:00");
			getTimeLabel(WEEK, THIS).setText("00:00");
			getTimeLabel(MONTH, THIS).setText("00:00");
			getTimeLabel(YEAR, THIS).setText("00:00");
		} else {
			long total = getActiveProject().getTotalTime(true);
			getTimeLabel(TOTAL, THIS).setText(
					formatTime(getTimePlusCurrent(total)));

			total = getActiveProject().getTimeBetween(
					DateUtilities.getBeginningOfDay(new Date()), now, true);
			getTimeLabel(TODAY, THIS).setText(
					formatTime(getTimePlusCurrent(total)));

			total = getActiveProject().getTimeBetween(
					DateUtilities.getMostRecentlyOccuringDay(new Date(),
							Calendar.SUNDAY), now, true);
			getTimeLabel(WEEK, THIS).setText(
					formatTime(getTimePlusCurrent(total)));

			total = getActiveProject().getTimeBetween(
					DateUtilities.getBeginningOfMonth(new Date()), now, true);
			getTimeLabel(MONTH, THIS).setText(
					formatTime(getTimePlusCurrent(total)));

			total = getActiveProject().getTimeBetween(
					DateUtilities.getBeginningOfYear(new Date()), now, true);
			getTimeLabel(YEAR, THIS).setText(
					formatTime(getTimePlusCurrent(total)));
		}

		// update the totals for all projects

		// for the "all" total, use the zero-time (1970), as we really
		// can't have project data before that
		Date all = new Date(0);
		long total = getAllProjectTimeSince(all, now);
		getTimeLabel(TOTAL, ALL).setText(formatTime(getTimePlusCurrent(total)));

		total = getAllProjectTimeSince(DateUtilities
				.getBeginningOfDay(new Date()), now);
		getTimeLabel(TODAY, ALL).setText(formatTime(getTimePlusCurrent(total)));

		total = getAllProjectTimeSince(DateUtilities
				.getMostRecentlyOccuringDay(new Date(), Calendar.SUNDAY), now);
		getTimeLabel(WEEK, ALL).setText(formatTime(getTimePlusCurrent(total)));

		total = getAllProjectTimeSince(DateUtilities
				.getBeginningOfMonth(new Date()), now);
		getTimeLabel(MONTH, ALL).setText(formatTime(getTimePlusCurrent(total)));

		total = getAllProjectTimeSince(DateUtilities
				.getBeginningOfYear(new Date()), now);
		getTimeLabel(YEAR, ALL).setText(formatTime(getTimePlusCurrent(total)));
	}

	/**
	 * Adds the time from the open timespan to its argument.
	 */
	private long getTimePlusCurrent(long ms) {
		if (ClientState.getInstance().isRunningProjectSelected()) {
			long now = System.currentTimeMillis();
			long elapsed = now
					- ClientState.getInstance().getOpenTimeSpanStartMillis();
			return ms + elapsed;
		} else {
			return ms;
		}
	}

	/**
	 * Adds up the time on all projects since the date argument to the
         * current date.
	 */
	private long getAllProjectTimeSince(Date d, Date now) {
		long time = 0;
		Iterator it = ClientState.getInstance().getProjects().iterator();
		while (it.hasNext()) {
			Project p = (Project) it.next();
			time += p.getTimeBetween(d, now, false);
		}
		return time;
	}

	private Project getActiveProject() {
		return ClientState.getInstance().getSelectedProject();
	}

	/**
	 * Formats a millisecond amount int hh:mm.
	 */
	private String formatTime(long ms) {
		return DateUtilities.HOUR_MINUTE_FORMATTER.formatTime(ms);
	}

  private static Utilities gu() {
	  return Utilities.getInstance();
  }

	private JLabel[][] _labels;

	private static final int THIS = 0;

	private static final int ALL = 1;

	private static final int TOTAL = 0;

	private static final int TODAY = 1;

	private static final int WEEK = 2;

	private static final int MONTH = 3;

	private static final int YEAR = 4;
}
