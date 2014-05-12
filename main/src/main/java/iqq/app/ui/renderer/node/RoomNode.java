package iqq.app.ui.renderer.node;

import com.alee.laf.label.WebLabel;
import iqq.api.bean.IMRoom;
import iqq.app.util.UIUtil;

import javax.swing.*;

/**
 * 聊天室显示节点
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-10
 * License  : Apache License 2.0
 */
public class RoomNode extends EntityNode {
    private IMRoom room;
    private byte[] avatar = {0};
    private WebLabel view = new WebLabel();

    public RoomNode(IMRoom room) {
        super(room);
        this.room = room;

        view.setMargin(2, 5, 2, 2);
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
        if(!view.getText().equals(room.getNick())) {
            view.setText(room.getNick());
        }
        if(!avatar.equals(room.getAvatar())) {
            avatar = room.getAvatar();
            ImageIcon icon = UIUtil.Bean.byteToIcon(avatar, 30, 30);
            view.setIcon(icon);

        }
        return view;
    }
}
