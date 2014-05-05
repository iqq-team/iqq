package iqq.app.ui.frame;

import com.alee.laf.rootpane.WebFrame;
import com.sun.awt.AWTUtilities;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMFrame;
import iqq.app.ui.frame.panel.MainPanel;

import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-4
 * License  : Apache License 2.0
 */
public class MainFrame extends IMFrame {

    private MainPanel contentPanel;
    public MainFrame(IMContext context) {
        super(context);

        initUI();
        installSkin(getSkinService());
    }

    private void initUI() {
        contentPanel = new MainPanel(this);

        setTitle(getI18nService().getMessage("app.name"));
        setContentPane(contentPanel);
        setDefaultCloseOperation(WebFrame.EXIT_ON_CLOSE);
        setUndecorated(true);                             // 去了默认边框
        setLocationRelativeTo(null);                      // 居中
        setPreferredSize(new Dimension(300, 650));        // 首选大小
        // 把窗口设置为透明
        AWTUtilities.setWindowOpaque(this, false);
        pack();
    }

    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);
        this.contentPanel.installSkin(skinService);
        setIconImage(skinService.getIconByKey("window/titleWIcon").getImage());
    }
}
