package iqq.app.ui.frame;

import com.alee.laf.rootpane.WebFrame;
import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMEntity;
import iqq.api.bean.IMRoom;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMFrame;
import iqq.app.ui.frame.panel.chat.ChatPane;
import iqq.app.ui.frame.panel.chat.EntityPanel;
import iqq.app.ui.frame.panel.chat.RoomPanel;
import iqq.app.ui.frame.panel.chat.UserPanel;
import iqq.app.util.UIUtil;
import org.sexydock.tabs.ITabCloseButtonListener;
import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-10
 * License  : Apache License 2.0
 */
public class ChatFrame extends IMFrame {
    private static final Logger LOG = LoggerFactory.getLogger(ChatFrame.class);

    private ChatPane contentPane;
    private JTabbedPane tabbedPane;
    private Map<IMEntity, EntityPanel> entityMap = new HashMap<IMEntity, EntityPanel>();

    public ChatFrame(IMContext context) {
        super(context);
        initUI();
        initTabListener();
        installSkin(getSkinService());
    }

    private void initUI() {
        contentPane = new ChatPane(this);
        tabbedPane = contentPane.getTabbedPane();
        setIMContentPane(contentPane);

        setTitle("与 承∮诺 的对话");
        setDefaultCloseOperation(WebFrame.HIDE_ON_CLOSE);
        setPreferredSize(new Dimension(650, 550));        // 首选大小
        pack();
    }

    /**
     * 安装皮肤
     *
     * @param skinService
     */
    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);
        this.contentPane.installSkin(skinService);
        setIconImage(skinService.getIconByKey("window/titleWIcon").getImage());
    }


    private void initTabListener() {
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(isVisible()) {
                    if( tabbedPane.getTabCount( ) == 0)
                    {
                        setVisible(false);
                    } else {
                        String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
                        setTitle(getI18nService().getMessage("conversationTitle", title));
                    }
                }
            }
        });
        tabbedPane.putClientProperty(JhromeTabbedPaneUI.TAB_CLOSE_BUTTON_LISTENER
                , new ITabCloseButtonListener() {

            @Override
            public void tabCloseButtonPressed(JTabbedPane tabbedPane, int tabIndex) {
                EntityPanel entityPanel = (EntityPanel) tabbedPane.getComponentAt(tabIndex);

                entityMap.remove(entityPanel.getEntity());
                tabbedPane.removeTabAt( tabIndex );
            }
        });
    }

    public void addChat(IMEntity entity) {
        if(!entityMap.containsKey(entity)) {
            if(entity instanceof IMBuddy) {
                addBuddyPane((IMBuddy) entity);
            } else if(entity instanceof IMRoom) {
                addRoomPane((IMRoom) entity);
            } else {
                LOG.warn("未知的聊天实体类，无法打开聊天对话");
            }
        } else {
            EntityPanel entityPanel = entityMap.get(entity);
            tabbedPane.setSelectedComponent(entityPanel);
        }
    }

    private void addBuddyPane(IMBuddy buddy) {
        EntityPanel entityPanel = new UserPanel(buddy);
        entityMap.put(buddy, entityPanel);

        ImageIcon avatar = UIUtil.Bean.byteToIcon(buddy.getAvatar(), 16, 16);
        tabbedPane.addTab(buddy.getNick(), avatar, entityPanel);
        tabbedPane.setSelectedComponent(entityPanel);
        setTitle(getI18nService().getMessage("conversationTitle", buddy.getNick()));
    }

    private void addRoomPane(IMRoom room) {
        EntityPanel entityPanel = new RoomPanel(room);
        entityMap.put(room, entityPanel);

        ImageIcon avatar = UIUtil.Bean.byteToIcon(room.getAvatar(), 16, 16);
        tabbedPane.addTab(room.getNick(), avatar, entityPanel);
        tabbedPane.setSelectedComponent(entityPanel);
        setTitle(getI18nService().getMessage("conversationTitle", room.getNick()));
    }

    @Override
    @Deprecated
    public void hide() {
        super.hide();
        // 如是关闭窗口，就清除所有对话
        clearChats();
    }

    private void clearChats() {
        for (int i = 0; i < entityMap.size(); i++) {
            tabbedPane.removeTabAt(0);
        }
        entityMap.clear();
    }
}
