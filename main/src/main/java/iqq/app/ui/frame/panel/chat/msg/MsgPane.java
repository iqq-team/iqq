/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/**
 * Project  : IQQ_V2.1
 * Package  : iqq.app.ui.content.chat.conversation
 * File     : UIMsgPane.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-9
 * License  : Apache License 2.0
 */
package iqq.app.ui.frame.panel.chat.msg;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.layout.WrapFlowLayout;
import com.alee.extended.panel.FlowPanel;
import com.alee.extended.panel.WrapPanel;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import iqq.api.bean.IMAccount;
import iqq.api.bean.IMMsg;
import iqq.app.core.context.IMContext;
import iqq.app.core.module.LogicModule;
import iqq.app.core.query.AccountQuery;
import iqq.app.core.service.I18nService;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMPanel;
import iqq.app.ui.frame.panel.chat.rich.RichTextPane;
import iqq.app.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * 消息框，用于在MsgListPane里面显示一条消息
 *
 * @author solosky <solosky772@qq.com>
 */
public class MsgPane extends IMPanel {
    private static final long serialVersionUID = 1587863328325375740L;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd");
    private WebDecoratedImage avatar;
    private WebLabel nameLabl;
    private WebLabel infoLabl;
    private WebPanel msgBg;
    private IMMsg msg;
    private boolean isSelfMsg = false;

    public MsgPane(IMMsg msg) {
        setOpaque(false);
        this.msg = msg;
        if (msg.getDirection() == IMMsg.Direction.SEND) {
            isSelfMsg = true;
        } else {
            isSelfMsg = false;
        }
        if (msg.getSender().getAvatar() != null) {
            this.avatar = new WebDecoratedImage(new ImageIcon(msg.getSender().getAvatar()));
        } else {
            this.avatar = new WebDecoratedImage(UIUtils.Bean.getDefaultAvatar());
        }
        initComponent();
        addComponent();
        invalidate();
    }

    private void addComponent() {
        WrapPanel msgWrap = new WrapPanel(msgBg);
        WrapFlowLayout wf = new WrapFlowLayout();
        if (isSelfMsg) {
            msgWrap.setMargin(10, 50, 5, 5);
            wf.setHalign(SwingConstants.RIGHT);
        } else {
            msgWrap.setMargin(10, 5, 5, 50);
            wf.setHalign(SwingConstants.LEFT);
        }
        msgWrap.setLayout(wf);
        add(msgWrap, BorderLayout.CENTER);

        FlowPanel faceFp = new FlowPanel(SwingConstants.TOP, avatar);
        faceFp.setOpaque(false);
        if (isSelfMsg) {
            faceFp.setMargin(10, 5, 5, 10);
            add(faceFp, BorderLayout.LINE_END);
        } else {
            faceFp.setMargin(10, 10, 5, 5);
            add(faceFp, BorderLayout.LINE_START);
        }
    }

    private void initComponent() {
        msgBg = new WebPanel();
        msgBg.setMargin(8, 18, 8, 8);

        Insets margin = new Insets(5, 10, 5, 10);
        nameLabl = new WebLabel(msg.getSender().getNick());
        Font font = WebLookAndFeel.globalTitleFont;
        font = font.deriveFont(Font.BOLD);
        nameLabl.setFont(font);
        nameLabl.setForeground(Color.darkGray);
        nameLabl.setMargin(margin);

        Font infoFont = WebLookAndFeel.globalTitleFont.deriveFont(12f);
        infoLabl = new WebLabel("..");
        infoLabl.setFont(infoFont);
        infoLabl.setForeground(Color.darkGray);
        infoLabl.setMargin(margin);

        // 根据输入框内容，新建一个富文本框放到消息气泡中
        RichTextPane msgTP = new RichTextPane();
        msgTP.setRichItems(UIUtils.Bean.toRichItem(msg.getContents()));
        msgTP.setEditable(false);
        msgTP.setOpaque(false);
        msgTP.setMargin(margin);
        SkinService skinService = IMContext.getBean(SkinService.class);
        if (isSelfMsg) {
            msgBg.setPainter(skinService.getPainterByKey("chat/balloon/right/blue"));
        } else {
            msgBg.setPainter(skinService.getPainterByKey("chat/balloon/left/white"));
        }
        msgBg.add(nameLabl, BorderLayout.PAGE_START);
        msgBg.add(infoLabl, BorderLayout.PAGE_END);
        msgBg.add(msgTP, BorderLayout.CENTER);

        avatar.setShadeWidth(1);
        avatar.setRound(3);
        avatar.setBorderColor(Color.WHITE);
    }

    @Override
    public void invalidate() {
        if (msg.getSender().getAvatar() != null) {
            this.avatar.setIcon(new ImageIcon(msg.getSender().getAvatar()));
        } else {
            this.avatar.setIcon(UIUtils.Bean.getDefaultAvatar());
        }
        //小于一天就显示时间，否则就显示日期
        String time = DATE_FORMAT.format(msg.getDate());
        if (System.currentTimeMillis() - msg.getDate().getTime() < 24 * 3600 * 1000) {
            time = TIME_FORMAT.format(msg.getDate());
        }
        I18nService i18nService = IMContext.getBean(I18nService.class);
        String stat = i18nService.getMessage("chat.msg." + msg.getState().name().toLowerCase());
        infoLabl.setText("[ " + time + ", " + stat + " ]");

        super.invalidate();
    }

    /**
     * @return the msg
     */
    public IMMsg getMsg() {
        return msg;
    }
}
