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
 * CVS Revision $Revision: 1.8 $
 * Last modified on $Date: 2008/11/26 20:55:03 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import net.sourceforge.hourglass.framework.HourglassPreferences;
import net.sourceforge.hourglass.framework.TimeSpan;
import net.sourceforge.hourglass.framework.TimeSpanOverlapException;

import org.apache.log4j.Logger;

/**
 * A swing component to display and edit a TimeSpan.
 * 
 * @author Mike Grant
 * @author Neil Thier
 */
public class TimeSpanPlate extends JPanel {

    public TimeSpanPlate(ActivityPanel p) {
        this(p, null);
    }


    public TimeSpanPlate(ActivityPanel p, TimeSpan t) {
        _timeSpan = t;
        _activityPanel = p;
        initializeComponents();
    }

    public void clear() {
        getStartTime().setText("");
        getEndTime().setText("");
        _timeSpan = null;
    }

    /**
     * Returns the TimeSpan represented by this component.
     */
    public TimeSpan getTimeSpan() {
        return _timeSpan;
    }


    /**
     * Sets the TimeSpan represented by this component.
     */
    public void setTimeSpan(TimeSpan t) {
        _timeSpan = t;
        getStartTime().setText(formatTime(t.getStartDate()));
        getEndTime().setText(formatTime(t.getEndDate()));
    }

    /**
     * Formats a date for the start/end times in the plate
     */
    protected String formatTime(Date d) {
        return getTimeFormat().format(d);
    }


    /**
     * Displays a start time in the component.
     */
    public void setStartTime(Date time) {
        getStartTime().setText(formatTime(time));
    }

    private void initializeComponents() {
        setLayout(getCardLayout());
        add(getViewPanel(), CARD_VIEW);
        add(getEditPanel(), CARD_EDIT);
        getCardLayout().show(this, CARD_VIEW);
    }


    private CardLayout getCardLayout() {
        if (_cardLayout == null) {
            _cardLayout = new CardLayout();
        }
        return _cardLayout;
    }


    private JPanel getViewPanel() {
        if (_viewPanel == null) {
            _viewPanel = new JPanel(new GridLayout(1, 2, 1, 0));
            _viewPanel.add(getStartTime());
            _viewPanel.add(getEndTime());
        }
        return _viewPanel;
    }


    private JPanel getEditPanel() {
        if (_editPanel == null) {
            _editPanel = new JPanel(new GridLayout(1, 2, 1, 0));
            _editPanel.add(getStartSpinner());
            _editPanel.add(getEndSpinner());
            _editPanel.setFocusable(true);
        }
        return _editPanel;
    }


    private JSpinner getStartSpinner() {
        if (_startSpinner == null) {
            _startSpinner = createNewDateSpinner();
            getSpinnerField(_startSpinner).addMouseListener(getEditModeMouseListener());
        }
        return _startSpinner;
    }


    private JSpinner getEndSpinner() {
        if (_endSpinner == null) {
            _endSpinner = createNewDateSpinner();
        }
        return _endSpinner;
    }

    public void updateSpinners() {
        updateSpinner(_startSpinner);
        updateSpinner(_endSpinner);
    }

    private void updateSpinner(JSpinner spinner) {
        getSpinnerField(spinner).removeKeyListener(getSpinnerKeyAdapter());
        getSpinnerField(spinner).removeMouseListener(getEditModeMouseListener());
        getSpinnerField(spinner).removeFocusListener(getEditFocusListener());
        spinner.setEditor(createSpinnerEditor(spinner));
    }


    private JTextField getStartSpinnerField() {
        return getSpinnerField(getStartSpinner());
    }


    private JTextField getEndSpinnerField() {
        return getSpinnerField(getEndSpinner());
    }


    private JTextField getSpinnerField(JSpinner s) {
        JSpinner.DateEditor de = (JSpinner.DateEditor) s.getEditor();
        return de.getTextField();
    }


    private void abortEdits() {
        setEditMode(false);
    }


    /**
     * Commits the changes made in edit mode to the active project.
     */
    private void commitEdits() {

        try {
            getStartSpinner().commitEdit();
            getEndSpinner().commitEdit();
        } catch (ParseException pex) {
        	getLogger().debug("At least one wrong Spinner value, Start '" + getStartSpinnerField().getText()
        			+ "' and Stop '" + getEndSpinnerField().getText() + "'");
            abortEdits();
            return;
        }

        Date newStartTime = (Date) getStartSpinner().getValue();
        Date newEndTime = (Date) getEndSpinner().getValue();
        Date today = getEditDate();

        /**
         * Take the existing date and splice it with the new times to get the
         * replacement dates.
         */
        Date newStartDate = Utilities.getInstance().spliceDate(today, newStartTime);
        Date newEndDate = Utilities.getInstance().spliceDate(today, newEndTime);

        try {
            TimeSpan replacement = new TimeSpan(newStartDate, newEndDate);
            getLogger().debug("About to replace " + getTimeSpan() + " with " + replacement);
            setEditMode(false);
            ClientState.getInstance().replaceTimeSpan(getTimeSpan(), replacement);
        } catch (IllegalArgumentException iaex) {
            JOptionPane.showMessageDialog(ClientState.getInstance().getSummaryFrame(),
                    "Start time must be before end time.", "Error", JOptionPane.ERROR_MESSAGE);

            abortEdits();
        } catch (TimeSpanOverlapException tsex) {
        	Utilities.getInstance().showError(ClientState.getInstance().getSummaryFrame(), tsex);
            abortEdits();
        }
    }

    private Date getEditDate() {
        return _activityPanel.getEditDate();
    }

    private JSpinner createNewDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        model.setCalendarField(Calendar.MINUTE);
        final JSpinner result = new JSpinner(model);
        JSpinner.DateEditor de = createSpinnerEditor(result);

        result.setEditor(de);
        result.setPreferredSize(getStartTime().getPreferredSize());

        return result;
    }


    private JSpinner.DateEditor createSpinnerEditor(final JSpinner result) {
        JSpinner.DateEditor de = new JSpinner.DateEditor(
                result, HourglassPreferences.getInstance().getTimeFormatString());

        de.getTextField().addKeyListener(getSpinnerKeyAdapter());
        de.getTextField().addMouseListener(getEditModeMouseListener());
        de.getTextField().addFocusListener(getEditFocusListener());
        
        return de;
    }


    private KeyListener getSpinnerKeyAdapter() {
        if (_spinnerKeyListener == null) {
            _spinnerKeyListener = new KeyAdapter() {
                public void keyPressed(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    	//	|| ke.getKeyCode() == KeyEvent.VK_TAB) {
                    	correctTimeField(ke);
                        commitEdits();
                    } else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        abortEdits();
                    }
                }
            };
        }
        return _spinnerKeyListener;
    }


    private JTextField getStartTime() {
        if (_startTime == null) {
            _startTime = new JTextField(FIELD_SIZE);
            _startTime.addFocusListener(getViewFocusListener());
            _startTime.addMouseListener(createListenerFor(getPopupMenu()));
        }
        return _startTime;
    }

    private JTextField getEndTime() {
        if (_endTime == null) {
            _endTime = new JTextField(FIELD_SIZE);
            _endTime.addFocusListener(getViewFocusListener());
            _endTime.addMouseListener(createListenerFor(getPopupMenu()));
        }
        return _endTime;
    }


    /**
     * Enables or disables editing of the plate.
     */
    public void setEnabled(boolean isEnabled) {
        _startTime.setEnabled(isEnabled);
        _endTime.setEnabled(isEnabled);
    }


    /**
     * Puts or removed the plate into or out of edit mode.
     * 
     * @param component
     *            component to focus if going into edit mode.
     */
    private void setEditMode(boolean editMode, JComponent component) {

        setEditMode(editMode);
        if (editMode == true) {
            component.requestFocus();
        }

    }


    /**
     * Puts or removes the plate into or out of edit mode.
     */
    private void setEditMode(boolean editMode) {
        _lockEditMode = false;
        if (editMode == true) {
            getActivityPanel().setEnabled(false);
            if (getTimeSpan() != null) {
                getStartSpinner().setValue(getTimeSpan().getStartDate());
                getEndSpinner().setValue(getTimeSpan().getEndDate());
            } else {
                /*
                 * We're adding a timespan by editing a blank plate. We'll
                 * default to the current time for both fields.
                 */
                Date today = getEditDate();
                getStartSpinner().setValue(today);
                getEndSpinner().setValue(today);
            }
            getCardLayout().show(this, CARD_EDIT);
        } else {
            getCardLayout().show(this, CARD_VIEW);
            getActivityPanel().setEnabled(true);
        }
        ClientState.getInstance().setEditMode(editMode);
    }


    private Logger getLogger() {
        if (_logger == null) {
            _logger = Logger.getLogger(getClass());
        }
        return _logger;
    }


    private FocusListener getViewFocusListener() {
        if (_viewFocusListener == null) {
            _viewFocusListener = new FocusListener() {

                public void focusGained(FocusEvent fe) {
                    getLogger().debug("ViewFocus gained: " + fe.paramString());
                    if (!isEditMode()) {
                        getLogger().debug("Not in edit mode");
                        JSpinner jsp;
                        if (fe.getSource() == getStartTime()) {
                            jsp = getStartSpinner();
                        } else {
                            jsp = getEndSpinner();
                        }

                        JComponent innerField = ((JSpinner.DateEditor) jsp.getEditor())
                                .getTextField();

                        setEditMode(true, innerField);
                    }
                }

                public void focusLost(FocusEvent fe) {
                    getLogger().debug("ViewFocus lost: " + fe.paramString());
                }
            };
        }
        return _viewFocusListener;
    }
    /** Function to validate (and improve) the time entry at the moment
     *  where an event happens. Currently, integers are transformed into 
     *  valid time entries (e.g. '9' into '9:00').
     *  A possible extension would be to accept entries like 'noon', etc...
     *  @param eo the event from which we get the source field to check.
     */
    private void correctTimeField(EventObject eo) {
        JTextField jtf;
        if ( eo.getSource() instanceof JTextField ) {
        	jtf = (JTextField) eo.getSource();
        } else {
        	return;
        }
        String txt = jtf.getText();
        if (Pattern.matches("^\\d\\d?$",txt) && Integer.parseInt(txt) < 24) {
        	// txt is a valid hour number (1 or 2 digits, smaller than 24)
        	jtf.setText(txt + ":00"); // we make a real hour out of it
        }
    }

    private FocusListener getEditFocusListener() {
        if (_editFocusListener == null) {
            _editFocusListener = new FocusListener() {

                public void focusGained(FocusEvent fe) {
                    getLogger().debug("EditFocus gained: " + fe.paramString());
                    if (fe.getSource() instanceof JTextField) {
                    	getLogger().debug("EditFocus gained: Select All!");
                    	// this should select the complete value but doesn't work
                    	((JTextField)fe.getSource()).selectAll();
                    }
                }

                public void focusLost(FocusEvent fe) {
                    getLogger().debug("EditFocus lost: " + fe.paramString());
                    correctTimeField(fe);
                    boolean isStartToEnd = 
                        fe.getSource() == getStartSpinnerField() && fe.getOppositeComponent() == getEndSpinnerField();
                    boolean isEndToStart = 
                    	fe.getSource() == getEndSpinnerField() && fe.getOppositeComponent() == getStartSpinnerField();
                    boolean isFocusChangeAcceptable = isStartToEnd || isEndToStart || _lockEditMode;

                    // if the TimeSpanPlate loses focus, we commit the edits
                    if (isEditMode() && !isFocusChangeAcceptable) {
                        commitEdits();
                    }
                }
            };
        }
        return _editFocusListener;
    }


    private ActivityPanel getActivityPanel() {
        return _activityPanel;
    }


    private boolean isEditMode() {
        return ClientState.getInstance().isEditMode();
    }


    private JPopupMenu getPopupMenu() {
        if (_popupMenu == null) {
            _popupMenu = new JPopupMenu();
            _popupMenu.add(new AbstractAction() {
                {
                    putValue(NAME, "Edit");
                    putValue(MNEMONIC_KEY, new Integer('e'));
                }

                public void actionPerformed(ActionEvent ae) {
                    setEditMode(true, getStartSpinnerField());
                }
            });

            _popupMenu.add(new AbstractAction() {
                {
                    putValue(NAME, "Delete");
                    putValue(MNEMONIC_KEY, new Integer('d'));
                }

                public void actionPerformed(ActionEvent ae) {
                    deleteThisTimeSpanFromProject();
                }
            });
        }
        return _popupMenu;
    }


    private JPopupMenu getEditModePopupMenu() {
        if (_editModePopupMenu == null) {
            _editModePopupMenu = new JPopupMenu() {
                public void setVisible(boolean visible) {
                    super.setVisible(visible);

                    /*
                     * If the menu is hiding, unlock edit mode.
                     */
                    if (!visible) {
                        getLogger().debug("Unlocking edit mode.");
                        _lockEditMode = false;
                    }
                }
            };

            _editModePopupMenu.add(new AbstractAction() {
                {
                    putValue(NAME, "Save Changes");
                    putValue(MNEMONIC_KEY, new Integer('s'));
                }

                public void actionPerformed(ActionEvent ae) {
                    commitEdits();
                }
            });

            _editModePopupMenu.add(new AbstractAction() {
                {
                    putValue(NAME, "Discard Changes");
                    putValue(MNEMONIC_KEY, new Integer('c'));
                }

                public void actionPerformed(ActionEvent ae) {
                    abortEdits();
                }
            });

            _editModePopupMenu.add(new AbstractAction() {
                {
                    putValue(NAME, "Delete");
                    putValue(MNEMONIC_KEY, new Integer('d'));
                }

                public void actionPerformed(ActionEvent ae) {
                    deleteThisTimeSpanFromProject();
                }
            });
        }
        return _editModePopupMenu;
    }


    private void deleteThisTimeSpanFromProject() {
        if (isEditMode()) {
            abortEdits();
        }
        if (getTimeSpan() != null) {
            ClientState.getInstance().replaceTimeSpan(getTimeSpan(), null);
        }
    }


    private MouseListener createListenerFor(final JPopupMenu menu) {
        MouseListener result = new MouseAdapter() {

            public void mousePressed(MouseEvent me) {
                maybeShowPopup(me);
            }

            public void mouseReleased(MouseEvent me) {
                maybeShowPopup(me);
            }

            private void maybeShowPopup(MouseEvent me) {
                if (me.isPopupTrigger()) {
                    menu.show(me.getComponent(), me.getX(), me.getY());
                }
            }
        };
        return result;
    }


    private MouseListener getEditModeMouseListener() {
        if (_editModeMouseListener == null) {
            _editModeMouseListener = new MouseAdapter() {

                public void mousePressed(MouseEvent me) {
                    maybeShowPopup(me);
                }

                public void mouseReleased(MouseEvent me) {
                    maybeShowPopup(me);
                }

                private void maybeShowPopup(MouseEvent me) {
                    if (me.isPopupTrigger()) {
                        _lockEditMode = true;
                        getLogger().debug("Locking edit mode.");
                        getEditModePopupMenu().show(me.getComponent(), me.getX(), me.getY());
                    }
                }
            };
        }
        return _editModeMouseListener;
    }


    private DateFormat getTimeFormat() {
        return _activityPanel.getTimeFormat();
    }


    private Logger _logger;
    private FocusListener _viewFocusListener;
    private FocusListener _editFocusListener;

    private TimeSpan _timeSpan;
    private JTextField _startTime;
    private JTextField _endTime;
    private JPanel _editPanel;
    private JPanel _viewPanel;
    private CardLayout _cardLayout;
    private ActivityPanel _activityPanel;
    private JSpinner _startSpinner;
    private JSpinner _endSpinner;
    private JPopupMenu _popupMenu;
    private JPopupMenu _editModePopupMenu;
    private MouseListener _editModeMouseListener;
    private KeyListener _spinnerKeyListener;

    private boolean _lockEditMode;
    
    private static final int FIELD_SIZE = 6;

    private static final String CARD_EDIT = "edit";
    private static final String CARD_VIEW = "view";
}