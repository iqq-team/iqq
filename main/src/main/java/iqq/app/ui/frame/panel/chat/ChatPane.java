package iqq.app.ui.frame.panel.chat;

import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.border.TabContentPanelBorder;
import iqq.app.ui.component.TitleComponent;
import iqq.app.ui.frame.ChatFrame;
import org.sexydock.tabs.jhrome.JhromeTabBorderAttributes;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

import javax.swing.*;
import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-10
 * License  : Apache License 2.0
 */
public class ChatPane extends IMContentPane {

    private ChatFrame frame;
    private JTabbedPane tabbedPane;
    private JButton settingButton;

    public ChatPane(ChatFrame chatFrame) {
        super();

        frame = chatFrame;

        initTabbedPane();
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    private void initTabbedPane() {
        TitleComponent titleComponent = new TitleComponent(frame);
        titleComponent.setShowSkinButton(false);
        titleComponent.setShowSettingButton(false);
        //titleComponent.setShowTitle(false);
        add(titleComponent, BorderLayout.NORTH);

        JhromeTabBorderAttributes.SELECTED_BORDER.topColor = new Color(255, 255, 255, 120);
        JhromeTabBorderAttributes.SELECTED_BORDER.bottomColor = new Color(255, 255, 255, 0);
        JhromeTabBorderAttributes.SELECTED_BORDER.shadowColor = new Color( 55 , 55 , 55 , 50 );
        JhromeTabBorderAttributes.SELECTED_BORDER.outlineColor = new Color( 55 , 55 , 55 , 100);
        JhromeTabBorderAttributes.UNSELECTED_BORDER.topColor = new Color(255, 255, 255, 40);
        JhromeTabBorderAttributes.UNSELECTED_BORDER.bottomColor = new Color(255, 255, 255, 0);
        JhromeTabBorderAttributes.UNSELECTED_BORDER.shadowColor = new Color( 55 , 55 , 55 , 20 );
        JhromeTabBorderAttributes.UNSELECTED_BORDER.outlineColor = new Color( 55 , 55 , 55 , 100 );
        JhromeTabBorderAttributes.UNSELECTED_ROLLOVER_BORDER.topColor = new Color( 255 , 255 , 255 , 160);
        JhromeTabBorderAttributes.UNSELECTED_ROLLOVER_BORDER.bottomColor = new Color( 255 , 255 , 255 , 50);
        JhromeTabbedPaneUI ui = new JhromeTabbedPaneUI();
        tabbedPane = new JTabbedPane();
        tabbedPane.setUI(ui);
        tabbedPane.setOpaque(false);
        tabbedPane.putClientProperty(JhromeTabbedPaneUI.TAB_CLOSE_BUTTONS_VISIBLE, true);
        tabbedPane.putClientProperty(JhromeTabbedPaneUI.NEW_TAB_BUTTON_VISIBLE, true);
        tabbedPane.putClientProperty(JhromeTabbedPaneUI.CONTENT_PANEL_BORDER, new TabContentPanelBorder());
        settingButton = ui.getNewTabButton();
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);
        // 背景
        this.setPainter(skinService.getPainterByKey("skin/background"));
        settingButton.setIcon(skinService.getIconByKey("chat/settingIcon", 14, 14));
    }

}
