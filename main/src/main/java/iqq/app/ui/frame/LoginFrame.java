package iqq.app.ui.frame;

import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebPasswordField;
import iqq.api.bean.IMStatus;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMFrame;
import iqq.app.ui.action.IMActionHandler;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import iqq.app.ui.event.args.LoginInfoParam;
import iqq.app.ui.frame.panel.login.LoginPane;
import iqq.app.ui.manager.FrameManager;
import iqq.app.ui.manager.MainManager;
import iqq.app.util.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 这只是一个登录界面模型，使用代码布局，如果使用IDE部分是不可以的，不好控制和使用组件
 * 那个背景图片可以使用现在QQ平时更换的背景图片，处理一下就可以换了哈
 * 这个界面的好处是可以随时更新背景图片
 * <p>
 * 存在问题：
 * LINUX下放大后再还原会那个背景出现问题，正想办法解决，重新设置背景或者怎么的
 * 现在这个问题设置不能最大化，先这样子处理
 * <p>
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-15
 * License  : Apache License 2.0
 */
public class LoginFrame extends IMFrame {
    private final Logger logger = LoggerFactory.getLogger(LoginFrame.class);
    private LoginPane contentPane;

    public LoginFrame() {
        super();

        initUI();
    }

    private void initUI() {
        contentPane = new LoginPane(this);

        // 登录面板
        setIMContentPane(contentPane);
        setTitle(getI18nService().getMessage("app.name"));
        setDefaultCloseOperation(WebFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 290));        // 首选大小
        // 居中
        Dimension screenSize = UIUtils.getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width / 2;         // 获取屏幕的宽
        int screenHeight = screenSize.height / 2;       // 获取屏幕的高
        setLocation(screenWidth - getPreferredSize().width / 2, screenHeight - getPreferredSize().height / 2);
        pack();

    }

    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);
        this.contentPane.installSkin(skinService);
        setIconImage(getSkinService().getIconByKey("window/titleWIcon").getImage());
    }

    @IMActionHandler
    public void login(ActionEvent e, WebComboBox b, WebPasswordField p) {
        LoginInfoParam param = new LoginInfoParam();
        param.setUsername(b.getSelectedItem().toString());
        param.setPassword(new String(p.getPassword()));
        param.setStatus(IMStatus.ONLINE);

        UIEvent event = new UIEvent();
        event.setType(UIEventType.LOGIN_REQUEST);
        event.setTarget(param);

        broadcastUIEvent(event);
    }

    @UIEventHandler(UIEventType.LOGIN_ERROR)
    public void processLoginError(UIEvent uiEvent) {
        WebOptionPane.showMessageDialog(contentPane, "登陆失败: " + uiEvent.getTarget(), "提示", WebOptionPane.INFORMATION_MESSAGE);
    }

    @UIEventHandler(UIEventType.LOGIN_SUCCESS)
    public void processLoginSuccess(UIEvent uiEvent) {
        IMContext.getBean(MainManager.class).show();
        IMContext.getBean(FrameManager.class).disposeLogin();
    }

    @UIEventHandler(UIEventType.IMAGE_VERIFY_NEED)
    public void processNeedVerify(UIEvent uiEvent) {
        IMContext.getBean(FrameManager.class).showVerify();
        WebOptionPane.showMessageDialog(contentPane, "需要验证", "提示", WebOptionPane.INFORMATION_MESSAGE);
    }

}
