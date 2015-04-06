package iqq.app.ui.frame.panel.login;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.image.WebImage;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebPasswordField;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.IMPanel;
import iqq.app.ui.action.IMActionHandlerProxy;
import iqq.app.ui.component.StatusButton;
import iqq.app.ui.component.TitleComponent;
import iqq.app.ui.frame.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-5
 * License  : Apache License 2.0
 */
public class LoginPane extends IMContentPane {

    private LoginFrame frame;
    private IMPanel headerPanel = new IMPanel();
    private IMPanel middlePanel = new IMPanel();
    private IMPanel footerPanel = new IMPanel();
    private WebButton loginBtn;
    private WebComboBox accountCbx;
    private WebPasswordField pwdFld;
    private WebDecoratedImage face;
    private WebLabel regLbl;
    private WebLabel findPwdLbl;
    private WebCheckBox rePwdCkb;
    private WebCheckBox autoLoginCkb;

    public LoginPane(LoginFrame frame) {
        this.frame = frame;
        this.add(createHeader(), BorderLayout.NORTH);
        this.add(createFooter(), BorderLayout.SOUTH);
        this.add(createMiddle(), BorderLayout.CENTER);

        this.setRound(50);
    }

    /**
     * 安装皮肤
     *
     * @param skinService
     */
    @Override
    public void installSkin(SkinService skinService) {
        // 登录界面的背景，可以处理QQ印象图片
        this.setPainter(skinService.getPainterByKey("login/background"));

        // 密码框小图标
        WebImage keyIcon = new WebImage(skinService.getIconByKey("login/keyIcon"));
        pwdFld.setTrailingComponent(keyIcon);

        // 标签字体颜色
        regLbl.setForeground(skinService.getColorByKey("login/labelColor"));
        findPwdLbl.setForeground(skinService.getColorByKey("login/labelColor"));

        // 选择框字体颜色
        rePwdCkb.setForeground(skinService.getColorByKey("login/checkBoxColor"));
        autoLoginCkb.setForeground(skinService.getColorByKey("login/checkBoxColor"));

        // 底部背景
        footerPanel.setPainter(skinService.getPainterByKey("login/footerBackground"));
    }

    // 上面部分在里面添加上去的
    private WebPanel createHeader() {
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(-1, 90));

        // 我自己写了个标题组件，透明的，可以添加到每个窗口上，可以封装为默认继承
        TitleComponent titleComponent = new TitleComponent(frame);
        titleComponent.setShowSkinButton(false);
        titleComponent.setShowMaximizeButton(false);
        headerPanel.add(titleComponent, BorderLayout.NORTH);

        return headerPanel;
    }

    // 中间，头像和输入框在这里面实现
    private WebPanel createMiddle() {
        Dimension dimFld = new Dimension(170, 30);

        middlePanel.setOpaque(false);
        middlePanel.setPreferredSize(new Dimension(320, 120));
        //middlePanel.setBackground(Color.BLUE);

        WebPanel left = new WebPanel();
        WebPanel right = new WebPanel();
        left.setOpaque(false);
        left.setMargin(0, 18, 0, 8);
        right.setMargin(19, 0, 10, 0);
        right.setOpaque(false);


        face = new WebDecoratedImage();
        // 头像
        ImageIcon icon = frame.getResourceService().getIcon("icons/login/qq_icon.png");
        face.setImage(icon.getImage().getScaledInstance(80, 80, 100));
        face.setShadeWidth(2);
        face.setRound(3);
        face.setBorderColor(Color.WHITE);
        left.add(face);

        // 账号输入一行
        String[] items = { "6208317", "917362009"};
        regLbl = new WebLabel(frame.getI18nService().getMessage("login.regAccount"));
        accountCbx = new WebComboBox(items);
        accountCbx.setEditable(true);
        accountCbx.setPreferredSize(dimFld);

        // 间隙5, 水平true，排为一组过去
        GroupPanel accountGroup = new GroupPanel(5, true, accountCbx, regLbl);
        accountGroup.setOpaque(false);

        // 密码输入一行
        findPwdLbl = new WebLabel(frame.getI18nService().getMessage("login.forgetPwd"));
        pwdFld = new WebPasswordField();
        pwdFld.setPreferredSize(dimFld);

        // 间隙5, 水平true，排为一组过去
        GroupPanel pwdGroup = new GroupPanel(5, true, pwdFld, findPwdLbl);

        // 选项一行
        rePwdCkb = new WebCheckBox(frame.getI18nService().getMessage("login.rememberPwd"));
        autoLoginCkb = new WebCheckBox("自动登录");
        final StatusButton statusBtn = new StatusButton(null);
        rePwdCkb.setFontSize(12);
        autoLoginCkb.setFontSize(12);

        // 间隙5, 水平true，排为一组过去
        GroupPanel optionGroup = new GroupPanel(5, true,  statusBtn, rePwdCkb, autoLoginCkb);

        // 把三行，垂直来放进去
        right.add(new GroupPanel(5, false, accountGroup, pwdGroup, optionGroup), BorderLayout.NORTH);

        middlePanel.add(left, BorderLayout.WEST);
        middlePanel.add(right, BorderLayout.CENTER);

        // 登录处理
        loginBtn.addActionListener(new IMActionHandlerProxy(frame, "login", accountCbx, pwdFld));
        /*
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IMAccount account = new IMAccount();
                account.setLoginName(accountCbx.getSelectedItem().toString());
                account.setPassword(new String(pwdFld.getPassword()));
                account.setRememberPwd(rePwdCkb.isSelected());
                frame.login(account);
            }
        });
        */
        return middlePanel;
    }

    private WebPanel createFooter() {

//        footerPanel.setOpaque(false);
        footerPanel.setPreferredSize(new Dimension(-1, 40));

        loginBtn = new WebButton(frame.getI18nService().getMessage("login.login"));
        loginBtn.setPreferredHeight(30);
        loginBtn.setPreferredWidth(150);
        loginBtn.setMargin(5);

        footerPanel.add(new CenterPanel(loginBtn), BorderLayout.CENTER);

        return footerPanel;
    }
}
