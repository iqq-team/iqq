package iqq.app.ui.frame;

import com.alee.extended.image.WebImage;
import com.alee.extended.label.WebLinkLabel;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.EmptyPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.text.WebTextField;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.IMFrame;
import iqq.app.ui.action.IMActionHandler;
import iqq.app.ui.action.IMActionHandlerProxy;
import iqq.app.ui.component.TitleComponent;
import iqq.app.util.UIUtils;

import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-6-17
 * License  : Apache License 2.0
 */
public class VerifyFrame extends IMFrame {
    private VerifyPane verifyPane;

    public VerifyFrame() {
        initUI();
        initContent();
    }

    @IMActionHandler
    public void processRefesh(WebImage verifyImage) {
        System.out.println("processRefesh");
    }

    @IMActionHandler
    public void processVerify(WebTextField verifyFld) {
        System.out.println("processVerify:" + verifyFld.getText());
    }

    @IMActionHandler
    public void processCancel() {
        dispose();
    }

    private void initContent() {
        verifyPane = new VerifyPane(this);
        setIMContentPane(verifyPane);
    }

    private void initUI() {
        setTitle(getI18nService().getMessage("veriry.verify"));
        setDefaultCloseOperation(WebFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);                      // 居中
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
        // 背景
        verifyPane.setPainter(skinService.getPainterByKey("skin/background"));
    }

    public class VerifyPane extends IMContentPane {
        VerifyFrame verifyFrame;
        WebImage verifyImage;
        WebLinkLabel veriryReset;
        WebTextField verifyFld;

        WebButton confirmBtn;
        WebButton cancelBtn;

        public VerifyPane(VerifyFrame verifyFrame) {
            super();
            this.verifyFrame = verifyFrame;
            addContent();
        }

        private void addContent() {
            // 上面是标题栏，下面为内容显示
            TitleComponent titleComponent = new TitleComponent(verifyFrame);
            titleComponent.setShowSkinButton(false);
            titleComponent.setShowMaximizeButton(false);
            titleComponent.setShowSettingButton(false);
            add(titleComponent, BorderLayout.PAGE_START);
            add(new CenterPanel(createContent()), BorderLayout.CENTER);
        }

        /**
         * @return
         */
        private WebPanel createContent() {
            verifyImage = new WebImage(verifyFrame.getResourceService().getIcon("icons/login/captcha.jpg"));
            veriryReset = new WebLinkLabel(verifyFrame.getI18nService().getMessage("veriry.reset"));
            verifyFld = new WebTextField();
            confirmBtn = new WebButton();
            cancelBtn = new WebButton();

            confirmBtn.setText(verifyFrame.getI18nService().getMessage("confirm"));
            cancelBtn.setText(verifyFrame.getI18nService().getMessage("cancel"));

            verifyImage.setPreferredSize(new Dimension(100, 40));
            confirmBtn.setPreferredSize(new Dimension(100, 30));
            cancelBtn.setPreferredSize(new Dimension(100, 30));

            GroupPanel imgPanel = new GroupPanel(true, verifyImage,
                    new EmptyPanel(20, 10), veriryReset);
            GroupPanel btnPanel = new GroupPanel(true, confirmBtn,
                    new EmptyPanel(20, 10), cancelBtn);

            WebPanel txtPanel = new WebPanel(new BorderLayout());
            txtPanel.add(verifyFld, BorderLayout.CENTER);
            txtPanel.setOpaque(false);

            Insets insets = new Insets(5, 10, 5, 10);
            imgPanel.setMargin(new Insets(5, 12, 5, 0));
            txtPanel.setMargin(insets);
            btnPanel.setMargin(insets);

            // 看不清楚，直接提交，重新显示验证码
            veriryReset.addActionListener(new IMActionHandlerProxy(verifyFrame, "processRefesh", verifyImage));
            confirmBtn.addActionListener(new IMActionHandlerProxy(verifyFrame, "processVerify", verifyFld));
            cancelBtn.addActionListener(new IMActionHandlerProxy(verifyFrame, "processCancel"));

            return new GroupPanel(0, false, imgPanel, txtPanel,
                    new CenterPanel(btnPanel));
        }
    }
}
