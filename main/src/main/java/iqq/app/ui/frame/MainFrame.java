package iqq.app.ui.frame;

import com.alee.laf.rootpane.WebFrame;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMFrame;
import iqq.app.ui.frame.panel.main.MainPane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

/**
 * 主界面，分为上/中/下的内容面板
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-4
 * License  : Apache License 2.0
 */
public class MainFrame extends IMFrame {

    private SystemTray tray;
    private TrayIcon icon;

    private MainPane contentPane;
    public MainFrame(IMContext context) {
        super(context);

        initUI();
        installSkin(getSkinService());
        //initTray();
    }

    private void initTray() {
        if(SystemTray.isSupported()) {

            PopupMenu pop = new PopupMenu();
            MenuItem restore = new MenuItem("Restore");
            restore.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                }
            });
            pop.add(restore);

            tray = SystemTray.getSystemTray();
            icon = new TrayIcon(getIconImage(), "IQQ", pop);
            icon.setImageAutoSize(true);
            try {
                tray.add(icon);
            } catch (AWTException e) {
            }
        }
        addWindowStateListener(new WindowStateListener() {

            @Override
            public void windowStateChanged(WindowEvent e) {
                if(e.getNewState() == Frame.ICONIFIED) {
                    setVisible(false);
                }
            }
        });
    }

    private void initUI() {
        // 主面板，放所有显示内容
        contentPane = new MainPane(this);
        setTitle(getI18nService().getMessage("app.name"));
        setIMContentPane(contentPane);
        setDefaultCloseOperation(WebFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);                      // 居中
        setPreferredSize(new Dimension(300, 650));        // 首选大小
        pack();
    }

    /**
     * 安装皮肤
     *
     * @param skinService
     */
    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);
        this.contentPane.installSkin(skinService);
        setIconImage(skinService.getIconByKey("window/titleWIcon").getImage());
    }

}
