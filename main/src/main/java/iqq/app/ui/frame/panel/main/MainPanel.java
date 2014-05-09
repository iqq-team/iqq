package iqq.app.ui.frame.panel.main;

import iqq.app.core.service.SkinService;
import iqq.app.ui.IMPanel;
import iqq.app.ui.frame.MainFrame;

import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-5
 * License  : Apache License 2.0
 */
public class MainPanel extends IMPanel {

    private MainFrame frame;
    private IMPanel headerPanel;
    private IMPanel middlePanel;
    private IMPanel footerPanel;

    public MainPanel(MainFrame mainFrame) {
        frame = mainFrame;

        headerPanel = new HeaderPanel(frame);
        middlePanel = new MiddlePanel(frame);
        footerPanel = new FooterPanel(frame);

        this.add(headerPanel, BorderLayout.NORTH);
        this.add(footerPanel, BorderLayout.SOUTH);
        this.add(middlePanel, BorderLayout.CENTER);
    }

    @Override
    public void installSkin(SkinService skinService) {
        // 背景
        this.setPainter(skinService.getPainterByKey("skin/background"));
        headerPanel.installSkin(skinService);
        middlePanel.installSkin(skinService);
        footerPanel.installSkin(skinService);
    }
}
