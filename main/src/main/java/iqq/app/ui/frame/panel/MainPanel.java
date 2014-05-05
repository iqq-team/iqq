package iqq.app.ui.frame.panel;

import com.alee.laf.panel.WebPanel;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPanel;
import iqq.app.ui.component.TitleComponent;
import iqq.app.ui.frame.MainFrame;
import iqq.app.ui.frame.SkinFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-5
 * License  : Apache License 2.0
 */
public class MainPanel extends IMContentPanel {

    private MainFrame frame;
    private WebPanel headerPanel = new WebPanel();
    private WebPanel middlePanel = new WebPanel();
    private WebPanel footerPanel = new WebPanel();

    public MainPanel(MainFrame mainFrame) {
        frame = mainFrame;

        this.add(createHeader(), BorderLayout.NORTH);
        this.add(createFooter(), BorderLayout.SOUTH);
        this.add(createMiddle(), BorderLayout.CENTER);
    }

    private WebPanel createHeader() {
        headerPanel.setOpaque(false);
        TitleComponent titleComponent = new TitleComponent(frame);
        titleComponent.setShowSettingButton(false);
        titleComponent.setShowMaximizeButton(false);
        titleComponent.getSkinButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SkinFrame skinFrame = new SkinFrame(frame.getContext());
                skinFrame.setVisible(true);
            }
        });
        headerPanel.add(titleComponent, BorderLayout.NORTH);

        return headerPanel;
    }

    private WebPanel createMiddle() {
        middlePanel.setOpaque(false);
        return middlePanel;
    }

    private WebPanel createFooter() {
        footerPanel.setOpaque(false);
        return footerPanel;
    }

    @Override
    public void installSkin(SkinService skinService) {
        // 背景
        this.setPainter(skinService.getPainterByKey("skin/background"));
    }
}
