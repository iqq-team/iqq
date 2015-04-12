package iqq.app.ui.renderer.node;

import com.alee.laf.label.WebLabel;
import com.alee.utils.ImageUtils;
import iqq.api.bean.IMRoom;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * 聊天室显示节点
 * <p>
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-10
 * License  : Apache License 2.0
 */
public class RoomNode extends EntityNode {
    private IMRoom room;
    private BufferedImage avatar;
    private WebLabel view = new WebLabel();

    public RoomNode(IMRoom room) {
        super(room);
        this.room = room;

        view.setMargin(5);
    }

    public IMRoom getRoom() {
        return room;
    }

    public void setRoom(IMRoom room) {
        this.room = room;
    }

    public WebLabel getView() {
        return getView(30, 30);
    }

    /**
     * 这个方法特别频繁，一定要处理好
     *
     * @return
     */
    public WebLabel getView(int iconWidth, int iconHeight) {
        if (!view.getText().equals(room.getNick())) {
            view.setText(room.getNick());
        }
        if (avatar == null || !avatar.equals(room.getAvatar())) {
            avatar = room.getAvatar();
            view.setIcon(ImageUtils.createPreviewIcon(avatar, 43));
        }
        return view;
    }
}
