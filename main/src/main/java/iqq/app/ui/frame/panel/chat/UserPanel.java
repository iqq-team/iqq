package iqq.app.ui.frame.panel.chat;

import com.alee.laf.button.WebButton;
import com.alee.laf.toolbar.WebToolBar;
import iqq.api.bean.IMMsg;
import iqq.api.bean.IMUser;
import iqq.api.bean.content.IMContentItem;
import iqq.api.bean.content.IMTextItem;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.EventService;
import iqq.app.core.service.SkinService;
import iqq.app.ui.event.UIEventDispatcher;

import java.util.ArrayList;
import java.util.Date;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-11
 * License  : Apache License 2.0
 */
public class UserPanel extends BasicPanel {

    protected WebButton shakeBtn;

    public UserPanel(IMUser entity) {
        super(entity);

//        test();
    }

    private void test() {
        IMMsg msg = new IMMsg();
        msg.setSender((IMUser) entity);
        java.util.List<IMContentItem> contents = new ArrayList<IMContentItem>();
        IMTextItem text = new IMTextItem("test content...");
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

    @Override
    protected void initInputToolbar(WebToolBar inputToolbar) {
        super.initInputToolbar(inputToolbar);

        shakeBtn = new WebButton();

        setInputButtonStyle(shakeBtn);

        // 震动
        inputToolbar.add(3, shakeBtn);
    }

    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);

        shakeBtn.setIcon(skinService.getIconByKey("chat/toolbar/shake"));
    }

}
