package iqq.app.ui.frame;/**
 * Created by zhihui_chen on 14-4-15.
 */

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.panel.BorderPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.WebComponentPanel;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
    public LoginFrame() {
        super();
        setContentPane(new ContentPanel(this));
        setTitle("iQQ");
        setIconImage(skinService.getIconByKey("window/titleIcon").getImage());
        setDefaultCloseOperation(WebFrame.EXIT_ON_CLOSE);
        setUndecorated(true);                             // 去了默认边框
        setLocationRelativeTo(null);                      // 居中
        setPreferredSize(new Dimension(400, 290));        // 首选大小
        setRound(5);
        AWTUtilities.setWindowOpaque(this, false);
        // 把窗口设置为透明
        pack();
    }

    // 登录界面主要是在这里面实现，包括背景
    class ContentPanel extends WebComponentPanel {

        LoginFrame ui;

        ContentPanel(LoginFrame ui) {
            this.ui = ui;
            // 登录界面的背景，可以处理QQ印象图片
            this.setPainter(ui.getSkinService().getPainterByKey("login/background"));
            this.add(createHeader(), BorderLayout.NORTH);
            this.add(createMiddle(), BorderLayout.CENTER);
            this.add(createFooter(), BorderLayout.SOUTH);
        }

        // 上面部分在里面添加上去的
        private WebPanel createHeader() {
            WebPanel headerPanel = new WebPanel();
            headerPanel.setOpaque(false);
            headerPanel.setPreferredSize(new Dimension(-1, 90));

            // 我自己写了个标题组件，透明的，可以添加到每个窗口上，可以封装为默认继承
            TitleComponent titleComponent = new TitleComponent(ui);
            titleComponent.getWindowButtons().getWebButton(1).setEnabled(false);
            headerPanel.add(titleComponent, BorderLayout.NORTH);

            return headerPanel;
        }

        // 中间，头像和输入框在这里面实现
        private WebPanel createMiddle() {
            Dimension dimFld = new Dimension(170, 30);

            WebPanel middlePanel = new WebPanel();
            middlePanel.setOpaque(false);
            middlePanel.setPreferredSize(new Dimension(320, 100));
            //middlePanel.setBackground(Color.BLUE);

            WebPanel left = new WebPanel();
            WebPanel right = new WebPanel();
            left.setOpaque(false);
            left.setMargin(5, 18, 10, 8);
            right.setMargin(22, 0, 10, 0);
            right.setOpaque(false);

            // 头像
            ImageIcon icon = ui.getResourceService().getIcon("login/avatar.jpg");
            WebDecoratedImage face = new WebDecoratedImage(icon.getImage().getScaledInstance(88, 88, 100));
            face.setShadeWidth(2);
            face.setRound(3);
            face.setBorderColor(Color.WHITE);
            left.add(face);

            // 账号输入一行
            WebLabel regLbl = new WebLabel("注册账号");
            WebTextField accountFld = new WebTextField();
            accountFld.setPreferredSize(dimFld);
            regLbl.setForeground(ui.getSkinService().getColorByKey("login/labelColor"));
            // 间隔5, 水平true，排为一组过去
            GroupPanel accountGroup = new GroupPanel(5, true, accountFld, regLbl);
            accountGroup.setOpaque(false);

            // 密码输入一行
            WebLabel findPwdLbl = new WebLabel("找回密码");
            WebPasswordField pwdFld = new WebPasswordField();
            pwdFld.setPreferredSize(dimFld);
            findPwdLbl.setForeground(ui.getSkinService().getColorByKey("login/labelColor"));
            // 间隔5, 水平true，排为一组过去
            GroupPanel pwdGroup = new GroupPanel(5, true, pwdFld, findPwdLbl);

            // 选项一行
            WebCheckBox rePwdCkb = new WebCheckBox("记住密码");
            WebCheckBox autoLoginCkb = new WebCheckBox("自动登录");
            rePwdCkb.setForeground(ui.getSkinService().getColorByKey("login/checkBoxColor"));
            autoLoginCkb.setForeground(ui.getSkinService().getColorByKey("login/checkBoxColor"));
            // 间隔5, 水平true，排为一组过去
            GroupPanel optionGroup = new GroupPanel(5, true, rePwdCkb, autoLoginCkb);

            // 把三行，垂直来放进去
            right.add(new GroupPanel(5, false, accountGroup, pwdGroup, optionGroup), BorderLayout.NORTH);

            middlePanel.add(left, BorderLayout.WEST);
            middlePanel.add(right, BorderLayout.CENTER);
            return middlePanel;
        }

        private WebPanel createFooter() {
            WebPanel footerPanel = new WebPanel();
            footerPanel.setOpaque(false);
            footerPanel.setPreferredSize(new Dimension(-1, 30));
            footerPanel.add(new WebButton("LOGIN"));
            return footerPanel;
        }

    }

}
