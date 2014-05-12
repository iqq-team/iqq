package iqq.app.ui.frame;

import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebPasswordField;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.IMFrame;
import iqq.app.ui.action.IMActionHandler;
import iqq.app.ui.frame.panel.login.LoginPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;

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
    private IMContentPane contentWrap;

    public LoginFrame(IMContext context) {
        super(context);
        initUI();
        installSkin(getSkinService());
    }

    private void initUI() {
        contentWrap = new LoginPane(this);

        // 登录面板
        setIMContentPane(contentWrap);
        setTitle(getI18nService().getMessage("app.name"));
        setDefaultCloseOperation(WebFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);                      // 居中
        setPreferredSize(new Dimension(400, 290));        // 首选大小
        pack();
    }

    @Override
    public void installSkin(SkinService skinService) {
        setIconImage(getSkinService().getIconByKey("window/titleWIcon").getImage());
        this.contentWrap.installSkin(skinService);
        super.installSkin(skinService);
    }

    @IMActionHandler
    public void login(ActionEvent e, WebComboBox b, WebPasswordField p) {
        System.out.println("username: " + e);
        System.out.println("username2: " + b);
        System.out.println("username2: " + p);

        //new MainFrame(getContext()).setVisible(true);
        //dispose();
    }

}
