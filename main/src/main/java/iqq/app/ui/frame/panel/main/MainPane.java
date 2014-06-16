package iqq.app.ui.frame.panel.main;

import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.frame.MainFrame;

import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-5
 * License  : Apache License 2.0
 */
public class MainPane extends IMContentPane {

    private MainFrame frame;
    private HeaderPanel headerPanel;
    private MiddlePanel middlePanel;
    private FooterPanel footerPanel;

    public MainPane(MainFrame mainFrame) {
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

    public HeaderPanel getHeaderPanel() {
        return headerPanel;
    }

    public void setHeaderPanel(HeaderPanel headerPanel) {
        this.headerPanel = headerPanel;
    }

    public MiddlePanel getMiddlePanel() {
        return middlePanel;
    }

    public void setMiddlePanel(MiddlePanel middlePanel) {
        this.middlePanel = middlePanel;
    }

    public FooterPanel getFooterPanel() {
        return footerPanel;
    }

    public void setFooterPanel(FooterPanel footerPanel) {
        this.footerPanel = footerPanel;
    }
}
