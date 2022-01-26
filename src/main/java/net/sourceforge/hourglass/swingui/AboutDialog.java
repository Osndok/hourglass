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
 * Last modified on $Date: 2005/03/06 00:25:17 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.swingui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import java.awt.event.ActionEvent;

import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * Help::About dialog.
 *
 * @author Mike Grant <mike@acm.jhu.edu>
 */
public class AboutDialog extends JDialog {

  public AboutDialog(Frame owner) {
    super(owner, "About Hourglass", true);
    initializeComponents();
    pack();
    setLocationRelativeTo(owner);
  }


  private void initializeComponents() {
    getContentPane().add(getInformationPanel(), BorderLayout.CENTER);
    getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
    getRootPane().setDefaultButton(getOkButton());
    getOkButton().grabFocus();
  }


  private JPanel getButtonPanel() {
    if (_buttonPanel == null) {
      _buttonPanel = new JPanel();
      _buttonPanel.add(getOkButton());
    }
    return _buttonPanel;
  }


  private JButton getOkButton() {
    if (_okButton == null) {
      _okButton = new JButton(new AbstractAction() {
          {
            putValue(NAME, "Ok");
            putValue(MNEMONIC_KEY, new Integer('o'));
          }
          public void actionPerformed(ActionEvent ae) {
            setVisible(false);
          }
        });
    }
    return _okButton;
  }


  private JPanel getInformationPanel() {
    if (_informationPanel == null) {
      _informationPanel = new JPanel();
      JScrollPane jsp = new JScrollPane(getTextPane());
      jsp.setPreferredSize(new Dimension(500, 200));
      _informationPanel.add(jsp);
    }
    return _informationPanel;
  }


  private JTextPane getTextPane() {
    if (_textPane == null) {
      _textPane = new JTextPane();
      _textPane.setEditorKit
        (_textPane.getEditorKitForContentType("text/html"));
      _textPane.setEditable(false);
      _textPane.setFocusable(false);
      try {
        _textPane.setPage
          (ClassLoader.getSystemResource
           ("net/sourceforge/hourglass/swingui/helpabout.html"));
      }
      catch (IOException ioex) {
        _textPane.setText("Couldn't load help::about information.");
      }
    }
    return _textPane;
  }
  

  private JButton _okButton;
  private JPanel _buttonPanel;
  private JPanel _informationPanel;
  private JTextPane _textPane;
}

    
