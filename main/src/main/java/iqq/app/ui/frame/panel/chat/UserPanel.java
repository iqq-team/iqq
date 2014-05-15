package iqq.app.ui.frame.panel.chat;

import iqq.api.bean.IMMsg;
import iqq.api.bean.IMUser;
import iqq.api.bean.content.IMContentItem;
import iqq.api.bean.content.IMTextItem;
import iqq.app.ui.frame.panel.chat.rich.UITextItem;

import java.util.ArrayList;
import java.util.Date;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-11
 * License  : Apache License 2.0
 */
public class UserPanel extends EntityPanel {

    public UserPanel(IMUser entity) {
        super(entity);

        test();
    }

    private void test() {
        IMMsg msg = new IMMsg();
        msg.setSender((IMUser) entity);
        java.util.List<IMContentItem> contents = new ArrayList<IMContentItem>();
        IMTextItem text = new IMTextItem();
        text.setText("test content...");
        contents.add(text);
        msg.setContents(contents);
        msg.setDate(new Date());
        msg.setState(IMMsg.State.READ);
        showMsg(msg);
        showMsg(msg);
        showMsg(msg);
        showMsg(msg);
        showMsg(msg);
    }

    public IMUser getUser() {
        return (IMUser) this.entity;
    }


}
