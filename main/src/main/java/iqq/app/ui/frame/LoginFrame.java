package iqq.app.ui.frame;/**
 * Created by zhihui_chen on 14-4-15.
 */

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.panel.WebComponentPanel;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextField;
import com.alee.utils.ImageUtils;
import com.sun.awt.AWTUtilities;
import iqq.app.core.annotation.IMModule;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMFrame;
import iqq.app.ui.component.TitleComponent;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import javax.swing.*;
import java.awt.*;

/**
 * 这只是一个登录界面模型，使用代码布局，如果使用IDE部分是不可以的，不好控制和使用组件
 * 那个背景图片可以使用现在QQ平时更换的背景图片，处理一下就可以换了哈
 * 这个界面的好处是可以随时更新背景图片
 *
 * 存在问题：
 *  放大后会那个背景出现问题，正想办法解决，重新设置背景或者怎么的
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-4-15
 * License  : Apache License 2.0
 */
@IMModule
@IocBean()
public class LoginFrame extends IMFrame {
    @Inject
    private SkinService skinService;

    public LoginFrame() {
        super();
        setContentPane(new ContentPanel(this));
        setTitle("iQQ");
        //setIconImage(ImageUtils.loadImage(this.getClass(), getAppDir() + "skins/default/window/title_icon.png").getImage());
        setDefaultCloseOperation(WebFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);                      // 居中
        setUndecorated(true);                             // 去了默认边框
        setPreferredSize(new Dimension(400, 290));
        setRound(5);
        pack();
        // 把窗口设置为透明
        AWTUtilities.setWindowOpaque(this, false);
        setVisible(true);
    }

    public static void main(String[] args) {
        WebLookAndFeel.install();
        new LoginFrame();
    }


    // 登录界面主要是在这里面实现，包括背景
    class ContentPanel extends WebComponentPanel {

        LoginFrame ui;

        ContentPanel(LoginFrame ui) {
            this.ui = ui;
            // 登录界面的背景，可以处理QQ印象图片
            //this.setPainter(new NinePatchIconPainter(getAppDir() + "skins/default/background/login_bg.9.png"));
            //this.add(createHeader(), BorderLayout.NORTH);
            //this.add(createMiddle(), BorderLayout.CENTER);
            //this.add(createFooter(), BorderLayout.SOUTH);
            this.add(new WebLabel(""));
        }

        // 上面部分在里面添加上去的
        private WebPanel createHeader() {
            WebPanel headerPanel = new WebPanel();
            headerPanel.setOpaque(false);
            headerPanel.setPreferredSize(new Dimension(-1, 100));

            // 我自己写了个标题组件，透明的，可以添加到每个窗口上，可以封装为默认继承
            headerPanel.add(new TitleComponent(ui), BorderLayout.NORTH);

            return headerPanel;
        }

        // 中间，头像和输入框在这里面实现
        private WebPanel createMiddle() {
            WebPanel middlePanel = new WebPanel();
            middlePanel.setOpaque(false);
            middlePanel.setPreferredSize(new Dimension(-1, 100));
            middlePanel.setPreferredSize(new Dimension(320, 200));

            WebPanel left = new WebPanel();
            WebPanel right = new WebPanel();
            middlePanel.add(left, BorderLayout.WEST);
            middlePanel.add(right, BorderLayout.EAST);

            // 头像
            ImageIcon icon = ImageUtils.loadImage(this.getClass(), getAppDir() + "resources/avatar.jpg");
            WebDecoratedImage face = new WebDecoratedImage(icon.getImage().getScaledInstance(88, 88, 100));
            face.setShadeWidth(2);
            face.setRound(3);
            face.setBorderColor(Color.WHITE);
            left.add(face);
            left.setOpaque(false);
            left.setMargin(-1, 30, -1, 30);

            WebLabel accoutLbl = new WebLabel("账号：");
            WebLabel pwdLbl = new WebLabel("密码：");

            WebTextField accout = new WebTextField();
            WebPasswordField pwd = new WebPasswordField();

            return middlePanel;
        }

        private WebPanel createFooter() {
            WebPanel footerPanel = new WebPanel();
            footerPanel.setOpaque(false);
            footerPanel.setPreferredSize(new Dimension(-1, 20));
            return footerPanel;
        }

    }

}
