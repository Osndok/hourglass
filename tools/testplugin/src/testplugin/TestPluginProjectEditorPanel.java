/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2004 Michael K. Grant <mike@localhost>
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
 * Last modified on $Date: 2004/08/29 23:17:31 $ by $Author: mgrant79 $
 *
 */
package testplugin;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.sourceforge.hourglass.framework.Project;
import net.sourceforge.hourglass.plugins.Plugin;
import net.sourceforge.hourglass.plugins.PluginProjectEditorPanel;

public class TestPluginProjectEditorPanel extends PluginProjectEditorPanel {

    private JTextField m_tf;

    public TestPluginProjectEditorPanel(Plugin plugin) {
        super(plugin, "Test Panel");
        setLayout(new FlowLayout());
        add(new JLabel("Project Test Value: "));
        
        m_tf = new JTextField(10);
        add(m_tf);
    }

    public void initialize(Project prj) {
        // Initialze any values
        m_tf.setText(prj.getAttribute("testplugin", "test_value"));
    }

    public void persistChanges(Project prj) {
        // Persist values back to the project
        prj.setAttribute("testplugin", "test_value", m_tf.getText());
    }

    public void cancelChanges(Project prj) {
        // Any custom cancellation code goes here.
    }

}
