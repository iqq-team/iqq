package iqq.app.ui.frame;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.image.WebImage;
import com.alee.extended.painter.ColorPainter;
import com.alee.extended.panel.*;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebPasswordField;
import com.sun.awt.AWTUtilities;
import iqq.app.ui.IMFrame;
import iqq.app.ui.component.StatusButton;
import iqq.app.ui.component.TitleComponent;
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
    public LoginFrame() {
        super();
        initUI();
    }

    private void initUI() {
        setTitle("iQQ");
        setIconImage(skinService.getIconByKey("window/titleWIcon").getImage());
        // 登录面板
        setContentPane(new ContentPanel(this));
        setDefaultCloseOperation(WebFrame.EXIT_ON_CLOSE);
        setUndecorated(true);                             // 去了默认边框
        setLocationRelativeTo(null);                      // 居中
        setPreferredSize(new Dimension(400, 290));        // 首选大小
        // 把窗口设置为透明
        AWTUtilities.setWindowOpaque(this, false);
        pack();
    }



    /*
    private void login(UIAccount account) {
        System.out.println("username: " + account.getUsername());
        System.out.println("password: " + account.getPassword());
        System.out.println("status: " + account.getStatus());
        System.out.println("isRememPwd: " + account.isRememPwd());
        System.out.println("isAutoLogin: " + account.isAutoLogin());
    }
    */

    // 登录界面主要是在这里面实现，包括背景
    class ContentPanel extends WebComponentPanel {

        LoginFrame ui;
        WebButton loginBtn;

        ContentPanel(LoginFrame ui) {
            this.ui = ui;
            // 登录界面的背景，可以处理QQ印象图片
            this.setPainter(ui.getSkinService().getPainterByKey("login/background"));
            this.add(createHeader(), BorderLayout.NORTH);
            this.add(createFooter(), BorderLayout.SOUTH);
            this.add(createMiddle(), BorderLayout.CENTER);
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
            middlePanel.setPreferredSize(new Dimension(320, 120));
            //middlePanel.setBackground(Color.BLUE);

            WebPanel left = new WebPanel();
            WebPanel right = new WebPanel();
            left.setOpaque(false);
            left.setMargin(0, 18, 0, 8);
            right.setMargin(19, 0, 10, 0);
            right.setOpaque(false);

            // 头像
            ImageIcon icon = ui.getResourceService().getIcon("icons/login/avatar.jpg");
            WebDecoratedImage face = new WebDecoratedImage(icon.getImage().getScaledInstance(80, 80, 100));
            face.setShadeWidth(2);
            face.setRound(3);
            face.setBorderColor(Color.WHITE);
            left.add(face);

            // 账号输入一行
            String[] items = { "6208317", "917362009"};
            WebLabel regLbl = new WebLabel("注册账号");
            final WebComboBox accoutCbx = new WebComboBox(items);
            accoutCbx.setEditable(true);
            accoutCbx.setPreferredSize(dimFld);
            regLbl.setForeground(ui.getSkinService().getColorByKey("login/labelColor"));

            // 间隙5, 水平true，排为一组过去
            GroupPanel accountGroup = new GroupPanel(5, true, accoutCbx, regLbl);
            accountGroup.setOpaque(false);

            // 密码输入一行
            WebLabel findPwdLbl = new WebLabel("找回密码");
            final WebPasswordField pwdFld = new WebPasswordField();
            pwdFld.setPreferredSize(dimFld);
            WebImage keyIcon = new WebImage(ui.getSkinService().getIconByKey("login/keyIcon"));
            pwdFld.setTrailingComponent(keyIcon);
            findPwdLbl.setForeground(ui.getSkinService().getColorByKey("login/labelColor"));
            // 间隙5, 水平true，排为一组过去
            GroupPanel pwdGroup = new GroupPanel(5, true, pwdFld, findPwdLbl);

            // 选项一行
            final WebCheckBox rePwdCkb = new WebCheckBox("记住密码");
            final WebCheckBox autoLoginCkb = new WebCheckBox("自动登录");
            final StatusButton statusBtn = new StatusButton(null);
            rePwdCkb.setFontSize(12);
            autoLoginCkb.setFontSize(12);
            rePwdCkb.setForeground(ui.getSkinService().getColorByKey("login/checkBoxColor"));
            autoLoginCkb.setForeground(ui.getSkinService().getColorByKey("login/checkBoxColor"));
            // 间隙5, 水平true，排为一组过去
            GroupPanel optionGroup = new GroupPanel(5, true,  statusBtn, rePwdCkb, autoLoginCkb);

            // 把三行，垂直来放进去
            right.add(new GroupPanel(5, false, accountGroup, pwdGroup, optionGroup), BorderLayout.NORTH);

            middlePanel.add(left, BorderLayout.WEST);
            middlePanel.add(right, BorderLayout.CENTER);

            // 登录处理
            loginBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    /*
                    UIAccount account = new UIAccount();
                    account.setUsername(accoutCbx.getSelectedItem().toString());
                    account.setPassword(new String(pwdFld.getPassword()));
                    account.setRememPwd(rePwdCkb.isSelected());
                    account.setAutoLogin(autoLoginCkb.isSelected());
                    ui.login(account);
                    */
                }
            });
            return middlePanel;
        }

        private WebPanel createFooter() {
            WebPanel footerPanel = new WebPanel();
            //footerPanel.setOpaque(false);
            footerPanel.setPainter(ui.getSkinService().getPainterByKey("login/FooterPackground"));
            footerPanel.setPreferredSize(new Dimension(-1, 40));

            loginBtn = new WebButton("登录");
            loginBtn.setPreferredHeight(30);
            loginBtn.setPreferredWidth(150);
            loginBtn.setMargin(5);
            WebButton leftBtn = new WebButton(ui.getSkinService().getIconByKey("login/settingIcon"));
            leftBtn.setRolloverIcon(ui.getSkinService().getIconByKey("login/settingActvie"));
            leftBtn.setPreferredHeight(30);
            leftBtn.setPreferredWidth(30);
            leftBtn.setOpaque(false);
            leftBtn.setVerticalAlignment(SwingConstants.TOP);
            leftBtn.setPainter(new ColorPainter(new Color(0,0,0,0)));
            WebLabel rightLbl = new WebLabel("ABC");
            rightLbl.setPreferredHeight(30);
            rightLbl.setPreferredWidth(30);

            //footerPanel.add(leftBtn, BorderLayout.WEST);
            //footerPanel.add(rightLbl, BorderLayout.EAST);
            footerPanel.add(new CenterPanel(loginBtn), BorderLayout.CENTER);

            return footerPanel;
        }

    }

}
