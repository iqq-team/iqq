package iqq.app.ui.frame;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.image.WebImage;
import com.alee.extended.panel.*;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebPasswordField;
import com.sun.awt.AWTUtilities;
import iqq.api.bean.IMAccount;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPanel;
import iqq.app.ui.IMFrame;
import iqq.app.ui.component.StatusButton;
import iqq.app.ui.component.TitleComponent;
import iqq.app.ui.frame.panel.LoginPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 这只是一个登录界面模型，使用代码布局，如果使用IDE部分是不可以的，不好控制和使用组件
 * 那个背景图片可以使用现在QQ平时更换的背景图片，处理一下就可以换了哈
 * 这个界面的好处是可以随时更新背景图片
 *
 * 存在问题：
 *  LINUX下放大后再还原会那个背景出现问题，正想办法解决，重新设置背景或者怎么的
 *  现在这个问题设置不能最大化，先这样子处理
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-15
 * License  : Apache License 2.0
 */
public class LoginFrame extends IMFrame {
    private static final Logger LOG = LoggerFactory.getLogger(LoginFrame.class);
    private IMContentPanel contentWrap;

    public LoginFrame(IMContext context) {
        super(context);
        initUI();
        installSkin(getSkinService());
    }

    private void initUI() {
        contentWrap = new LoginPanel(this);

        // 登录面板
        setContentPane(contentWrap);
        setTitle(getI18nService().getMessage("app.name"));
        setDefaultCloseOperation(WebFrame.EXIT_ON_CLOSE);
        setUndecorated(true);                             // 去了默认边框
        setLocationRelativeTo(null);                      // 居中
        setPreferredSize(new Dimension(400, 290));        // 首选大小
        // 把窗口设置为透明
        AWTUtilities.setWindowOpaque(this, false);
        pack();
    }

    @Override
    public void installSkin(SkinService skinService) {
        setIconImage(getSkinService().getIconByKey("window/titleWIcon").getImage());
        this.contentWrap.installSkin(skinService);
        super.installSkin(skinService);
    }

    public void login(IMAccount account) {
        System.out.println("username: " + account.getLoginName());
        System.out.println("password: " + account.getPassword());
        new MainFrame(getContext()).setVisible(true);
        dispose();
    }

}
