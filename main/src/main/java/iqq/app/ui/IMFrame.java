package iqq.app.ui;/**
 * Created by zhihui_chen on 14-4-15.
 */

import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import iqq.app.core.service.SkinService;
import org.nutz.ioc.loader.annotation.Inject;

import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-15
 * License  : Apache License 2.0
 */
public abstract class IMFrame extends WebFrame {

    @Inject
    private SkinService skinService;
    protected WebPanel contentPanel = new WebPanel();

    public IMFrame() {
        // 背景只带了阴影，就只是一个底而已
        String filePath = getAppDir() + "skins/default/background/window_bg.9.png";

        //contentPanel.setPainter(new NinePatchIconPainter(filePath));
        super.setContentPane(contentPanel);
    }

    @Override
    public void setContentPane(Container contentPane) {
        contentPanel.add(contentPane);
    }

    protected String getAppDir() {
        String filePath = IMFrame.class.getResource("").getPath();
        filePath = filePath.replace("main/target/classes/iqq/im/ui/", "");
        return filePath;
    }
}
