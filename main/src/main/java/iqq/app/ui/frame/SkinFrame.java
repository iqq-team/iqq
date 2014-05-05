package iqq.app.ui.frame;

import com.alee.extended.image.GalleryTransferHandler;
import com.alee.extended.image.WebImageGallery;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.sun.awt.AWTUtilities;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.core.service.impl.SkinServiceImpl;
import iqq.app.ui.IMFrame;
import iqq.app.ui.component.TitleComponent;
import iqq.app.ui.skin.Skin;
import iqq.app.ui.skin.SkinManager;
import iqq.app.util.XmlUtils;
import org.dom4j.DocumentException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * 皮肤设置界面
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-5
 * License  : Apache License 2.0
 */
public class SkinFrame extends IMFrame implements Skin {
    private WebPanel contentPanel = new WebPanel();
    private WebPanel headerPanel = new WebPanel();
    private WebPanel middlePanel = new WebPanel();
    private WebPanel footerPanel = new WebPanel();

    public SkinFrame(IMContext context) {
        super(context);
        initUI();
        installSkin(getSkinService());
    }

    private void initUI() {
        contentPanel.add(createHeader(), BorderLayout.NORTH);
        contentPanel.add(createFooter(), BorderLayout.SOUTH);
        contentPanel.add(createMiddle(), BorderLayout.CENTER);
        setContentPane(contentPanel);

        setTitle(getI18nService().getMessage("app.skinSetting"));
        setIconImage(getSkinService().getIconByKey("skin/skinIcon").getImage());
        setContentPane(contentPanel);
        setDefaultCloseOperation(WebFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);                             // 去了默认边框
        setLocationRelativeTo(null);                      // 居中
        setPreferredSize(new Dimension(380, 280));        // 首选大小
        // 把窗口设置为透明
        AWTUtilities.setWindowOpaque(this, false);
        pack();
    }

    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);
        // 背景
        contentPanel.setPainter(skinService.getPainterByKey("skin/background"));
    }

    private WebPanel createHeader() {
        headerPanel.setOpaque(false);
        TitleComponent titleComponent = new TitleComponent(this);
        titleComponent.setShowSettingButton(false);
        titleComponent.setShowMaximizeButton(false);
        titleComponent.setShowSkinButton(false);
        titleComponent.setShowMinimizeButton(false);
        headerPanel.add(titleComponent, BorderLayout.NORTH);
        return headerPanel;
    }


    private WebPanel createMiddle() {
        middlePanel.setOpaque(false);
        final WebImageGallery wig = new WebImageGallery ();
        // skin目录下的所有背景
        File dir = new File(SkinServiceImpl.DEFAULT_SKIN_DIR + "skin" + File.separator + "bg");
        for(int i=dir.listFiles().length; i>=0; i--) {
            ImageIcon icon = new ImageIcon( dir.getAbsolutePath() + File.separator + i + ".9.png");
            icon = new ImageIcon(icon.getImage().getScaledInstance(80, 80, 100));
            wig.addImage (icon);
        }

        wig.setPreferredColumnCount ( 3 );
        wig.setTransferHandler ( new GalleryTransferHandler( wig ) );
        wig.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                wig.setEnabled(false);
                SkinService skin = getSkinService();
                int index = wig.getSelectedIndex();
                if (index >= 0) {
                    try {
                        XmlUtils.setNodeText(skin.getDefaultConfig(), "skin/background", "skin/bg/" + index + ".9.png");
                    } catch (DocumentException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    SkinManager.installAll(skin);
                }
                wig.setEnabled(true);
            }
        });

        middlePanel.add(wig.getView(false), BorderLayout.CENTER);
        return middlePanel;
    }

    private WebPanel createFooter() {
        footerPanel.setOpaque(false);
        return footerPanel;
    }

}
