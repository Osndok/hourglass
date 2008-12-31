/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Portions Copyright (C) 2003 Neil Thier <nthier@alumni.uwaterloo.ca>
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
 * CVS Revision $Revision: 1.12 $
 * Last modified on $Date: 2005/05/08 00:16:30 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.sourceforge.hourglass.framework.ErrorKeys;
import net.sourceforge.hourglass.framework.Prefs;
import net.sourceforge.hourglass.framework.HourglassPreferences;
import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.framework.TimeSpan;


/**
 * Shows start and end time activity for a project.
 * 
 * @author Mike Grant
 * @author Neil Thier
 */
public class ActivityPanel extends JPanel implements ClientStateListener, HourglassPreferences.Listener {


    public ActivityPanel() {
        super(new BorderLayout());
        m_plates = new TimeSpanPlate[NUM_SPAN_PLATES];
        initializeComponents();
        ClientState.getInstance().addClientStateListener(this);
        HourglassPreferences.getInstance().addListener(this, new String[] {
		Prefs.TIME_FORMAT_TYPE });
    }


    private void initializeComponents() {

        JPanel gridPanel = new JPanel(new GridLayout(NUM_SPAN_PLATES, 1, 0, 1));
        setBorder(BorderFactory.createTitledBorder(Utilities.getInstance().getString(Strings.ACTIVITY_PANEL_HEADER)));
        add(gridPanel, BorderLayout.CENTER);

        for (int i = 0; i < NUM_SPAN_PLATES; ++i) {
            gridPanel.add(m_plates[i] = new TimeSpanPlate(this));
        }
        
        add(getEditHistoryButton(), BorderLayout.SOUTH);

        setEnabled(false);
    }


    private JButton getEditHistoryButton() {
        if (m_editHistoryButton == null) {
            m_editHistoryButton = new JButton(getEditHistoryAction());
        }
        return m_editHistoryButton;
    }


    public void preferenceChanged(String id) {
        if (Prefs.TIME_FORMAT_TYPE.equals(id)) {
            m_timeFormat = HourglassPreferences.getInstance().createTimeFormat();
            refreshPlates(ClientState.getInstance().getSelectedProject());
            for (int i = 0; i < m_plates.length; ++i) {
                m_plates[i].updateSpinners();
            }
        }
    }

    private TimeSpanPlate getNextAvailPlate() {
        return m_plates[getNextAvailIdx()];
    }


    private int getNextAvailIdx() {
        for (int i = 0; i < NUM_SPAN_PLATES; ++i) {
            if (m_plates[i].getTimeSpan() == null) {
                return i;
            }
        }
        return NONE_AVAILABLE;
    }

    /**
     * Get all the time spans for today
     * 
     * @return an array of time spans that are from todays date
     */
    protected TimeSpan[] getTimeSpansForDate(Project p, Date date) {
        if (p == null) {
            return new TimeSpan[0];
        }
        TimeSpan[] spans = (TimeSpan[]) p.getTimeSpans().toArray(new TimeSpan[0]);
        LinkedList todays = new LinkedList();

        // now we need to find the ones for todays date
        // to save time, lets work backwards. couldn't think of a nice way
        // to do this while in a collection, so took spans as an array
        for (int i = spans.length - 1; i >= 0; i--) {
            if (spans[i].isFromDate(date)) {
                todays.addFirst(spans[i]);
            }
        }

        return (TimeSpan[]) todays.toArray(new TimeSpan[0]);
    }

    /**
     * Shows all the spans in the array
     */
    protected void setTimeSpans(TimeSpan[] spans) {
        for (int i = 0; i < NUM_SPAN_PLATES; i++) {
            if (i < spans.length) {
                m_plates[i].setTimeSpan(spans[i]);
            } else {
                m_plates[i].clear();
            }
        }
    }

    public void selectedProjectChanged(Project activeProject) {
        refreshPlates(activeProject);
    }

    public void refreshPlates(Project activeProject) {
        ClientState cs = ClientState.getInstance();
        if (cs.getSummaryFrame() != null) {
	        TimeSpan[] todays = getTimeSpansForDate(activeProject, cs.getSummaryFrame().getEditDate());
	        setTimeSpans(todays);
	        setEnabled(activeProject != null);
	        if (cs.isRunning() && cs.isRunningProjectSelected()) {
	        	TimeSpanPlate plate = getNextAvailPlate();
				plate.setStartTime(cs.getOpenTimeSpanStart());
				plate.setEnabled(false);
	        }
        }
    }


    public void projectGroupChanged(ProjectGroup group) {
    }

    public void newTimeSpanStarted(Date d) {
        ClientState cs = ClientState.getInstance();
        if (m_plates[NUM_SPAN_PLATES - 1].getTimeSpan() != null) {
			cs.resetCurrentTimeSpan();
            Utilities.getInstance().showError(getParent(), ErrorKeys.ERROR_KEY_TIME_SLOTS_FULL, null);
            return;
        }
        refreshPlates(cs.getRunningProject());
    }

    public void currentTimeSpanStopped(TimeSpan ts) {
        getNextAvailPlate().setTimeSpan(ts);
        setEnabled(true);
    }


    /**
     * Enables or disables all time span plates.
     */
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        for (int i = 0; i < NUM_SPAN_PLATES; ++i) {
            m_plates[i].setEnabled(isEnabled);
        }
        getEditHistoryAction().setEnabled(isEnabled);
    }

    public DateFormat getTimeFormat() {
        if (m_timeFormat == null) {
            m_timeFormat = HourglassPreferences.getInstance().createTimeFormat();
        }

        return m_timeFormat;
    }
    
    private Action getEditHistoryAction() {
        if (m_editHistoryAction == null) {
            m_editHistoryAction = new AbstractAction(Utilities.getInstance().getString(Strings.EDIT_TIME_HISTORY)) {
                {
                    putValue(SMALL_ICON, Utilities.getInstance().getIcon(Strings.IMAGE_EDIT_TIME_HISTORY));
                }
                public void actionPerformed(ActionEvent e) {
                    SummaryFrame frame = ClientState.getInstance().getSummaryFrame();
                    getEditHistoryButton().setText(Utilities.getInstance().getString(
                            frame.toggleEditHistoryMode() ?
                                    Strings.EDIT_TIME_HISTORY_DONE :
                                    Strings.EDIT_TIME_HISTORY));
                                        
                            
                }
            };
        }
        return m_editHistoryAction;
    }
    
    /**
     * Returns the edit date from the summary frame.
     * @see SummaryFrame#getEditDate()
     */
    public Date getEditDate() {
        return ClientState.getInstance().getSummaryFrame().getEditDate();
    }
    
    private TimeSpanPlate[] m_plates;
    private DateFormat m_timeFormat;
    private Action m_editHistoryAction;
    private JButton m_editHistoryButton;

    private static final int NUM_SPAN_PLATES = 20;

    private static final int NONE_AVAILABLE = -1;

	public void currentTimeSpanAborted() {	}
}
