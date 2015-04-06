package iqq.app.ui.manager;

import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.rootpane.WebFrame;
import iqq.app.core.service.ResourceService;
import iqq.app.ui.frame.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-12
 * License  : Apache License 2.0
 */
@Component
public class MainManager {
    private final Logger logger = LoggerFactory.getLogger(MainManager.class);
    private SystemTray tray;
    private TrayIcon icon;
    private PopupMenu menu;
    private MainFrame mainFrame;

    @Resource
    private ResourceService resourceService;

    public void show() {
        if (mainFrame == null) {
            mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    hide();
                }
            });
            enableTray();
        }
        if (!mainFrame.isVisible()) {
            mainFrame.setVisible(true);
        }
    }

    public void hide() {
        if (mainFrame.isVisible()) {
            mainFrame.setVisible(false);
        }
    }

    public void enableTray() {
        if (SystemTray.isSupported() && tray == null) {
            menu = new PopupMenu();
            MenuItem restore = new MenuItem("  Restore  ");
            restore.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    show();
                }
            });
            MenuItem exit = new MenuItem("  Exit  ");
            exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            menu.add(restore);
            menu.add(exit);
            tray = SystemTray.getSystemTray();
            icon = new TrayIcon(mainFrame.getIconImage(), "IQQ", menu);
            icon.setImageAutoSize(true);
            try {
                tray.add(icon);
                icon.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            show();
                        }
                    }
                });
            } catch (AWTException e) {
                logger.error("SystemTray add icon.", e);
            }
        }

    }

}
