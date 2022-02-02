package net.sourceforge.hourglass.swingui;

import javax.swing.JOptionPane;

public
class ExceptionHandler
{
    public static
    void showUser(Throwable t)
    {
        t.printStackTrace();
        var s = t.toString();
        if (s.length() > 1000)
        {
            s = s.substring(0, 1000);
        }
        var mainFrame = ClientState.getInstance().getSummaryFrame();
        JOptionPane.showMessageDialog(mainFrame, s);
    }
}
