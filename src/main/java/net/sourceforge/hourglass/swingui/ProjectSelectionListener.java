package net.sourceforge.hourglass.swingui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public
class ProjectSelectionListener extends MouseAdapter
        implements TreeSelectionListener
{
    private static final
    Logger log = LoggerFactory.getLogger(ProjectSelectionListener.class);

    /**
     * Reacts to selection changes in the project list.
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        var clientState = ClientState.getInstance();
        var s = clientState.getSummaryFrame();
        clientState.setSelectedProject(s.getSelectedProject());
    }

    public void mousePressed(MouseEvent e) {
        var clientState = ClientState.getInstance();
        var s = clientState.getSummaryFrame();
        var tree = s.getProjectTree();
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        var selPath = tree.getPathForLocation(e.getX(), e.getY());
        if(selRow != -1) {
            if(e.getClickCount() == 1) {
                //mySingleClick(selRow, selPath);
                log.info("single click: #{}, {}", selRow, selPath);
            }
            else if(e.getClickCount() == 2) {
                //myDoubleClick(selRow, selPath);
                log.info("double click: #{}, {}", selRow, selPath);
            }
        }
    }
}
