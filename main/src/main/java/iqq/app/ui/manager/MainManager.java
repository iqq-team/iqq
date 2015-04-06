package iqq.app.ui.manager;

import iqq.app.ui.frame.MainFrame;
import org.springframework.stereotype.*;
import org.springframework.stereotype.Component;

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
@Component
public class MainManager {
    private SystemTray tray;
    private TrayIcon icon;
    private MainFrame mainFrame;

    public void show() {
        if (mainFrame == null) {
            mainFrame = new MainFrame();
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
        if (!mainFrame.isVisible()) mainFrame.setVisible(true);
    }

    public void hide() {
        mainFrame.dispose();
    }

    public void enableTray() {
        if (SystemTray.isSupported()) {

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
