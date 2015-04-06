package iqq.app.ui.manager;

import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMEntity;
import iqq.api.bean.IMRoom;
import iqq.api.bean.IMUser;
import iqq.app.ui.frame.ChatFrame;
import iqq.app.ui.frame.panel.chat.BasicPanel;
import iqq.app.ui.frame.panel.chat.RoomPanel;
import iqq.app.ui.frame.panel.chat.UserPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 聊天窗口管理类，用于管理聊天对话
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-12
 * License  : Apache License 2.0
 */
@Component
public class ChatManager {
    private final Logger LOG = LoggerFactory.getLogger(ChatManager.class);
    private ChatFrame chatFrame;
    private Map<IMEntity, BasicPanel> entityMap;

    public void addChat(IMEntity entity) {
        if(chatFrame == null) {
            chatFrame = new ChatFrame();
            entityMap = new HashMap<IMEntity, BasicPanel>();
            LOG.debug("创建了一个聊天窗口界面");
        }

        if(!entityMap.containsKey(entity)) {
            if(entity instanceof IMUser) {
                // 用户对话
                UserPanel entityPanel = new UserPanel((IMUser) entity);
                entityMap.put(entity, entityPanel);
                chatFrame.addBuddyPane((IMBuddy) entity, entityPanel);
            } else if(entity instanceof IMRoom) {
                // 聊天室对话
                RoomPanel entityPanel = new RoomPanel((IMRoom) entity);
                entityMap.put(entity, entityPanel);
                chatFrame.addRoomPane((IMRoom) entity, entityPanel);
            } else {
                LOG.warn("未知的聊天实体类，无法打开聊天对话");
            }
        } else {
            // 已经打开了对话，直接进行选中状态
            chatFrame.setSelectedChat(entityMap.get(entity));
        }
        if(!chatFrame.isVisible()) {
            chatFrame.setVisible(true);
        }
    }

    /**
     * 删除了一个对话
     *
     * @param entity
     */
    public void removeChat(IMEntity entity) {
        entityMap.remove(entity);
    }

    /**
     * 对话窗口已经关闭，进行清除处理
     */
    public void clearChats() {
        entityMap.clear();
    }


}
