package iqq.app.ui.renderer.node;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.label.WebLabel;
import iqq.api.bean.IMBuddy;
import iqq.app.ui.IMPanel;
import iqq.app.util.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * 好友显示节点
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-10
 * License  : Apache License 2.0
 */
public class BuddyNode extends EntityNode {
    private IMBuddy buddy;
    private IMPanel view = new IMPanel();
    // 头像
    private byte[] avatar = {0};
    private WebDecoratedImage avatarImage;
    // 昵称
    WebLabel nickLbl;
    WebLabel signLbl;

    public BuddyNode(IMBuddy buddy) {
        super(buddy);
        this.buddy = buddy;

        // 头像
        avatarImage = new WebDecoratedImage();
        avatarImage.setShadeWidth(1);
        avatarImage.setRound(4);
        avatarImage.setDrawGlassLayer(false);

        // 昵称
        nickLbl = new WebLabel();
        signLbl = new WebLabel();
        nickLbl.setFontSize(14);
        signLbl.setFontSize(13);
        signLbl.setForeground(Color.GRAY);
        GroupPanel textGroup = new GroupPanel(0, false, nickLbl, signLbl);
        textGroup.setMargin(0, 5, 0, 5);

        view.add(avatarImage, BorderLayout.WEST);
        view.add(new CenterPanel(textGroup, false , true), BorderLayout.CENTER);
        view.setMargin(5);
    }

    public IMBuddy getBuddy() {
        return buddy;
    }

    public void setBuddy(IMBuddy buddy) {
        this.buddy = buddy;
    }

    /**
     * 这个方法特别频繁，一定要处理好
     *
     * @return
     */
    public IMPanel getView() {
        if(!avatar.equals(buddy.getAvatar())) {
            avatar = buddy.getAvatar();
            ImageIcon icon = UIUtils.Bean.byteToIcon(avatar, 40, 40);
            avatarImage.setIcon(icon);
        }
        if(!nickLbl.getText().equals(buddy.getNick())) {
            nickLbl.setText(buddy.getNick());
        }
        if(!signLbl.getText().equals(buddy.getSign())) {
            signLbl.setText(buddy.getSign());
        }
        return view;
    }
}
