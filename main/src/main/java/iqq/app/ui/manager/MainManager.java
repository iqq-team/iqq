package iqq.app.ui.manager;

import iqq.app.core.context.IMContext;
import iqq.app.ui.frame.MainFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-12
 * License  : Apache License 2.0
 */
public class MainManager {
    private static SystemTray tray;
    private static TrayIcon icon;
    private static MainFrame mainFrame;

    public static void show() {
        if(mainFrame == null) {
            mainFrame = new MainFrame(IMContext.me());
            mainFrame.setVisible(true);
            mainFrame.addWindowStateListener(new WindowStateListener() {
                @Override
                public void windowStateChanged(WindowEvent e) {
                    if (e.getNewState() == Frame.ICONIFIED) {
                        hide();
                    }
                }
            });
        }
        if(!mainFrame.isVisible()) mainFrame.setVisible(true);
    }

    public static void hide() {
        mainFrame.dispose();
    }

    public static void enableTray() {
        if(SystemTray.isSupported()) {

            PopupMenu pop = new PopupMenu();
            MenuItem restore = new MenuItem("Restore");
            restore.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    show();
                }
            });
            pop.add(restore);

            tray = SystemTray.getSystemTray();
            icon = new TrayIcon(mainFrame.getIconImage(), "IQQ", pop);
            icon.setImageAutoSize(true);
            try {
                tray.add(icon);
            } catch (AWTException e) {
            }
        }

    }

}
