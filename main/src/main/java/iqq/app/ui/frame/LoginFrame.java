package iqq.app.ui.frame;

import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebPasswordField;
import iqq.api.bean.IMStatus;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMFrame;
import iqq.app.ui.action.IMActionHandler;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import iqq.app.ui.event.args.LoginInfoParam;
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
    private LoginPane contentWrap;

    public LoginFrame() {
        initUI();
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
        LoginInfoParam param = new LoginInfoParam();
        param.setUsername(b.getSelectedItem().toString());
        param.setPassword(new String(p.getPassword()));
        param.setStatus(IMStatus.ONLINE);

        UIEvent event = new UIEvent();
        event.setType(UIEventType.lOGIN_REQUEST);
        event.setTarget(param);

        eventService.broadcast(event);
    }

    @UIEventHandler(UIEventType.LOGIN_ERROR)
    public void processLoginError(UIEvent uiEvent){
        WebOptionPane.showMessageDialog(contentWrap, "登陆失败: " + uiEvent.getTarget(), "提示", WebOptionPane.INFORMATION_MESSAGE);
    }

    @UIEventHandler(UIEventType.LOGIN_SUCCESS)
    public void processLoginSuccess(UIEvent uiEvent){
        new MainFrame().setVisible(true);
        this.setVisible(false);
    }

    @UIEventHandler(UIEventType.IMAGE_VERIFY_NEED)
    public void processNeedVerify(UIEvent uiEvent){
        WebOptionPane.showMessageDialog(contentWrap, "需要验证", "提示", WebOptionPane.INFORMATION_MESSAGE);
    }

}
