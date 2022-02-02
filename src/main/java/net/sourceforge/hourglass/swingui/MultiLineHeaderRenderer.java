/*
 * Based on public domain code from: 
 * http://www.objects.com.au/java/examples/src/table/MultiLineHeaderRenderer.java
 *
 * ------------------------------------------------------------------
 *
 * CVS Revision $Revision: 1.3 $
 * Last modified on $Date: 2005/05/02 00:04:13 $ by $Author: mgrant79 $
 *
 */

package net.sourceforge.hourglass.swingui;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;


/**
 * @version 1.0 11/09/98
 */
public class MultiLineHeaderRenderer extends JList implements TableCellRenderer {
    public MultiLineHeaderRenderer() {
        setOpaque(true);
        setForeground(UIManager.getColor("TableHeader.foreground"));
        setBackground(UIManager.getColor("TableHeader.background"));
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setFont(UIManager.getFont("TableHeader.font"));
        ListCellRenderer renderer = getCellRenderer();
        ((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
        setCellRenderer(renderer);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        setFont(table.getFont());
        String str = (value == null) ? "" : value.toString();
        BufferedReader br = new BufferedReader(new StringReader(str));
        String line;
        Vector v = new Vector();
        try {
            while ((line = br.readLine()) != null) {
                v.addElement(line);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            ExceptionHandler.showUser(ex);
        }
        setListData(v);
        return this;
    }
    
    public static void setNewInstanceAsTableHeaderRenderer(JTable table) {
        TableCellRenderer renderer = new MultiLineHeaderRenderer();
        for (int i = 0; i < table.getColumnCount(); ++i) {
            TableColumn eachColumn = table.getColumnModel().getColumn(i);
            eachColumn.setHeaderRenderer(renderer);
        }
    }
}
