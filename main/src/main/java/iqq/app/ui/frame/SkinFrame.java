package iqq.app.ui.frame;

import com.alee.extended.image.GalleryTransferHandler;
import com.alee.extended.image.WebImageGallery;
import com.alee.laf.panel.WebPanel;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.core.service.impl.SkinServiceImpl;
import iqq.app.ui.IMContentPane;
import iqq.app.ui.IMFrame;
import iqq.app.ui.component.TitleComponent;
import iqq.app.ui.manager.SkinManager;
import iqq.app.ui.skin.Skin;
import iqq.app.util.UIUtils;
import iqq.app.util.XmlUtils;
import org.dom4j.DocumentException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * 皮肤设置界面
 * <p>
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-5
 * License  : Apache License 2.0
 */
public class SkinFrame extends IMFrame implements Skin {
    private IMContentPane contentPane = new IMContentPane();
    private WebPanel headerPanel = new WebPanel();
    private WebPanel middlePanel = new WebPanel();
    private WebPanel footerPanel = new WebPanel();

    public SkinFrame() {
        initUI();
    }

    private void initUI() {
        contentPane.add(createHeader(), BorderLayout.NORTH);
        contentPane.add(createFooter(), BorderLayout.SOUTH);
        contentPane.add(createMiddle(), BorderLayout.CENTER);
        setIMContentPane(contentPane);

        setTitle(getI18nService().getMessage("app.skinSetting"));
        setIMContentPane(contentPane);
        setPreferredSize(new Dimension(380, 280));        // 首选大小
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
        contentPane.setPainter(skinService.getPainterByKey("skin/background"));
        setIconImage(getSkinService().getIconByKey("skin/skinIcon").getImage());
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
        final WebImageGallery wig = new WebImageGallery();
        // skin目录下的所有背景
        File dir = new File(SkinServiceImpl.DEFAULT_SKIN_DIR + "skin" + File.separator + "bg");
        for (int i = dir.listFiles().length; i >= 0; i--) {
            ImageIcon icon = new ImageIcon(dir.getAbsolutePath() + File.separator + i + ".9.png");
            icon = new ImageIcon(icon.getImage().getScaledInstance(80, 80, 100));
            wig.addImage(icon);
        }

        wig.setPreferredColumnCount(3);
        wig.setTransferHandler(new GalleryTransferHandler(wig));
        wig.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
                    IMContext.getBean(SkinManager.class).installAll(skin);
                }
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
