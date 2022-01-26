/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 * Copyright (C) 2009 Eric Lavarde <ewl@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * --------------------------------------------------------------------
 * 
 * CVS Revision $Revision: 1.13 $ Last modified on $Date: 2005/03/06 00:25:04 $
 * by $Author: mgrant79 $
 *  
 */
package net.sourceforge.hourglass.reports.timecard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.hourglass.framework.DateUtilities;
import net.sourceforge.hourglass.framework.ProjectGroup;
import net.sourceforge.hourglass.swingui.MultiLineHeaderRenderer;
import net.sourceforge.hourglass.swingui.ProjectTreeCellRenderer;
import net.sourceforge.hourglass.swingui.Strings;
import net.sourceforge.hourglass.swingui.Utilities;
import net.sourceforge.hourglass.swingui.treetable.JTreeTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Swing UI for the timecard report.
 * 
 * @author Michael K. Grant <mike@acm.jhu.edu>
 */
public class TimecardReportPanel extends JPanel {


    private static final int PREFERRED_PROJ_COLUNM_WIDTH = 300;
    private static final int PREFERRED_DATE_COLUMN_WIDTH = 50;
	protected static final Border TOTAL_BORDER = new AbstractBorder() {
		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, c.getWidth(), 0);
		}
	};

    /**
     * Creates a new timecard report
     * 
     * @param projectGroup
     *            the project on which to create the report
     * @param frame
     *            the containing JFrame
     */
    public TimecardReportPanel(ProjectGroup projectGroup, JFrame frame) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        _dateFormat = DateUtilities.createShortDateFormat();
        initializeComponents(projectGroup);
        _projectGroup = projectGroup;
        _frame = frame;
    }

    // TODO [MGT, 25 Oct 2003]: refactor until Utilities
    private JComponent createHeader() {
        JPanel result = new JPanel(new BorderLayout());

        JLabel headerLabel = new JLabel(
			gu().getString(Strings.TIMECARD_REPORT));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 2, 5));
        headerLabel.setBackground(Color.WHITE);
        headerLabel.setOpaque(true);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 18.0f));

        result.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        result.add(headerLabel, BorderLayout.CENTER);
        return result;
    }

    private void initializeComponents(ProjectGroup projectGroup) {

        // Start the report at last monday.
        Date startDate = DateUtilities.getCurrentWeekStart();
        Date endDate = DateUtilities.getCurrentWeekEnd();

        JPanel contentPanel = new JPanel(new BorderLayout());
        JComponent treeComponent = createTreeComponent(projectGroup, startDate, endDate,
                DateUtilities.HOUR_MINUTE_FORMATTER);
        contentPanel.add(treeComponent, BorderLayout.CENTER);
        contentPanel.add(createHeader(), BorderLayout.NORTH);
        contentPanel.add(createOptionPanel(startDate, endDate), BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }


	private JPanel createButtonPanel() {
        JPanel result = new JPanel();
        JButton updateButton = new JButton(getUpdateAction());
        result.add(updateButton);
        // TODO [MGT, 26 Oct 2003]: get this to work (throws NPE)
        //getRootPane().setDefaultButton(updateButton);
        result.add(new JButton(getCloseAction()));

        return result;
    }


    private JPanel createOptionPanel(Date startDate, Date endDate) {
        JPanel result = new JPanel(new BorderLayout());
        JPanel contentPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c_radio = new GridBagConstraints();
        c_radio.fill = GridBagConstraints.NONE;
        c_radio.anchor = GridBagConstraints.WEST;
        c_radio.gridwidth = GridBagConstraints.REMAINDER;
        c_radio.insets = new Insets(0, 20, 0, 0);

        GridBagConstraints c_lbl = (GridBagConstraints) c_radio.clone();
        c_lbl.gridwidth = 1;
        c_lbl.insets = new Insets(10, 10, 10, 10);

        JRadioButton hhMmFormat = new JRadioButton(
			gu().getString(Strings.TIMECARD_USE_FORMAT,
					new String[] {"hh:mm"}));
        hhMmFormat.setActionCommand(HHMM_FORMAT);
        JRadioButton decimalFormat = new JRadioButton(
			gu().getString(Strings.TIMECARD_USE_FORMAT,
					new String[] {"#.00"}));
        decimalFormat.setActionCommand(DECIMAL_FORMAT);

        _buttonGroup = new ButtonGroup();
        _buttonGroup.add(hhMmFormat);
        _buttonGroup.add(decimalFormat);
        _buttonGroup.setSelected(hhMmFormat.getModel(), true);

        _startDateField = new JTextField(10);
        _endDateField = new JTextField(10);
        _startDateField.setText(_dateFormat.format(startDate));
        _endDateField.setText(_dateFormat.format(endDate));


        contentPanel.add(new JLabel(
		gu().getString(Strings.TIMECARD_START_DATE)), c_lbl);
        contentPanel.add(_startDateField);
        contentPanel.add(hhMmFormat, c_radio);
        contentPanel.add(new JLabel(
		gu().getString(Strings.TIMECARD_END_DATE)), c_lbl);
        contentPanel.add(_endDateField);
        contentPanel.add(decimalFormat, c_radio);

        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 15));

        result.add(contentPanel, BorderLayout.WEST);

        result.setBorder(BorderFactory.createTitledBorder(
				gu().getString(Strings.OPTIONS)));

        return result;
    }

    private JComponent createTreeComponent(final ProjectGroup projectGroup, 
    		Date startDate, Date endDate,
            DateUtilities.TimeFormatter timeFormatter) {
        _model = new TimecardTreeTableModel(projectGroup, startDate, endDate, timeFormatter);
        _treeTable = new JTreeTable(_model);

        JTree innerTree = _treeTable.getTreeTableCellRenderer();
        innerTree.setCellRenderer(new ProjectTreeCellRenderer() {
        	public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				JLabel result = (JLabel) super.getTreeCellRendererComponent(
						tree, value, sel, expanded, leaf, row, hasFocus);
				result.setBorder(null);
                result.setFont(UIManager.getFont("Table.font"));
            	if (value != null && value.equals(projectGroup.getRootProject())) {
            		result.setFont(UIManager.getFont("Table.font").deriveFont(Font.BOLD));
                    result.setBorder(TOTAL_BORDER);
					result.setText(
						gu().getString(Strings.TOTAL));
					result.setIcon(null);
            	}
				return result;
			}
        });

        innerTree.setRootVisible(false);
        _model.setTree(innerTree);

        setColumnWidths();

        JScrollPane result = new JScrollPane(_treeTable);
        result.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        _treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        _treeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel result = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                result.setHorizontalAlignment(JLabel.RIGHT);
                result.setFont(UIManager.getFont("Table.font"));
            	result.setBorder(null);
                if (column == table.getColumnCount() - 1 ||
                    row == table.getRowCount() - 1) {
                    result.setFont(result.getFont().deriveFont(Font.BOLD));
                    result.setBorder((row == table.getRowCount() - 1) ? TOTAL_BORDER : null);
                }
                return result;
            }
        });
        MultiLineHeaderRenderer.setNewInstanceAsTableHeaderRenderer(_treeTable);

        return result;
    }

    private Action getCloseAction() {
        if (_closeAction == null) {
            _closeAction = new AbstractAction() {

                {
                    putValue(NAME, gu().getString(Strings.TIMECARD_CLOSE));
                    putValue(MNEMONIC_KEY,
			gu().getMnemonicAsInt(Strings.TIMECARD_CLOSE));
                }

                public void actionPerformed(ActionEvent ae) {
                    _frame.dispose();
                }
            };
        }
        return _closeAction;
    }

    private Action getUpdateAction() {
        if (_updateAction == null) {
            _updateAction = new AbstractAction() {

                {
                    putValue(NAME, gu().getString(Strings.TIMECARD_UPDATE));
                    putValue(MNEMONIC_KEY,
			gu().getMnemonicAsInt(Strings.TIMECARD_UPDATE));
                }

                public void actionPerformed(ActionEvent ae) {
                    try {
                        synchronized (_dateFormat) {
                            // TODO [MGT, 25 Oct 2003]: Use utilities
                            Date startDate = _dateFormat.parse(_startDateField.getText());
                            Date endDate = _dateFormat.parse(_endDateField.getText());
                            if (endDate.before(startDate)) {
                                JOptionPane.showMessageDialog(TimecardReportPanel.this,
                                gu().getString(Strings.ERROR_KEY_DATE_START_AFTER_END),
                                gu().getString(Strings.ERROR),
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            else {
                                DateUtilities.TimeFormatter formatter = (_buttonGroup.getSelection()
                                        .getActionCommand().equals(DECIMAL_FORMAT)
                                        ? DateUtilities.DECIMAL_HOUR_FORMATTER
                                        : DateUtilities.HOUR_MINUTE_FORMATTER);

                                _model.reinitialize(_projectGroup, startDate, endDate, formatter);
                                _treeTable.refreshTreeTable();
                                MultiLineHeaderRenderer.setNewInstanceAsTableHeaderRenderer(_treeTable);
                                setColumnWidths();
                            }
                        }
                    }
                    catch (ParseException e) {
                        JOptionPane.showMessageDialog(TimecardReportPanel.this,
                                gu().getString(Strings.ERROR_KEY_PARSING_DATE,
					new String[] {gu().getString(
					Strings.SHORT_DATE_FORMAT_STRING)}),
                                gu().getString(Strings.ERROR),
				JOptionPane.ERROR_MESSAGE);
                        __logger.error("getUpdateAction", e);
                    }
                }
            };
        }
        return _updateAction;
    }


    private void setColumnWidths() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                TableColumn projectColumn = _treeTable.getColumnModel().getColumn(0);
                projectColumn.setPreferredWidth(PREFERRED_PROJ_COLUNM_WIDTH);
                for (int i = 1; i < _treeTable.getColumnCount(); ++i) {
                    TableColumn col = _treeTable.getColumnModel().getColumn(i);
                    col.setPreferredWidth(PREFERRED_DATE_COLUMN_WIDTH);
                }
            }
        });
    }


    private DateFormat _dateFormat;

    private static final String HHMM_FORMAT = "HHMM_FORMAT";
    private static final String DECIMAL_FORMAT = "DECIMAL_FORMAT";

    private JTextField _startDateField;
    private JTextField _endDateField;
    private JTreeTable _treeTable;
    private Action _updateAction;
    private Action _closeAction;
    private ButtonGroup _buttonGroup;
    private JFrame _frame;

    private TimecardTreeTableModel _model;
    private ProjectGroup _projectGroup;


  private static Utilities gu() {
	  return Utilities.getInstance();
  }

    private static final Logger __logger = LogManager.getLogger(TimecardReportPanel.class);

}
