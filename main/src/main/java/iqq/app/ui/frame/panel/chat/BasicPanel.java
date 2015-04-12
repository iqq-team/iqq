package iqq.app.ui.frame.panel.chat;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.painter.ColorPainter;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.toolbar.ToolbarStyle;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.utils.ImageUtils;
import iqq.api.bean.IMEntity;
import iqq.api.bean.IMMsg;
import iqq.app.core.context.IMContext;
import iqq.app.core.module.LogicModule;
import iqq.app.core.service.EventService;
import iqq.app.core.service.SkinService;
import iqq.app.ui.IMPanel;
import iqq.app.ui.event.UIEvent;
import iqq.app.ui.event.UIEventDispatcher;
import iqq.app.ui.event.UIEventHandler;
import iqq.app.ui.event.UIEventType;
import iqq.app.ui.frame.panel.chat.msg.MsgGroupPanel;
import iqq.app.ui.frame.panel.chat.msg.MsgPane;
import iqq.app.ui.frame.panel.chat.rich.RichTextPane;
import iqq.app.ui.frame.panel.chat.rich.UIRichItem;
import iqq.app.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Date;
import java.util.List;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-11
 * License  : Apache License 2.0
 */
public abstract class BasicPanel extends IMPanel {
    /**
     * 面板聊天对象
     */
    protected IMEntity entity;

    /**
     * 输入面板
     */
    protected RichTextPane contentInput = new RichTextPane();
    /**
     * 输入面板工具条
     */
    protected WebToolBar inputToolbar = new WebToolBar();


    /**
     * 昵称
     */
    protected WebLabel nickLabel = new WebLabel();
    /**
     * 签名
     */
    protected WebLabel signLabel = new WebLabel();

    /**
     * 头像
     */
    protected WebDecoratedImage avatarImage = new WebDecoratedImage();

    protected WebButton textBtn = new WebButton();
    protected WebButton emoticonBtn = new WebButton();
    protected WebButton screenCaptureBtn = new WebButton();
    protected WebButton picturesBtn = new WebButton();
    protected WebButton historyBtn = new WebButton();
    protected WebButton sendBtn = new WebButton();

    protected IMPanel headerPanel = new IMPanel();
    protected IMPanel inputPanel = new IMPanel();
    protected MsgGroupPanel msgGroupPanel = new MsgGroupPanel();
    protected boolean isAppendMsg = false;

    protected UIEventDispatcher uiEventDispatcher = new UIEventDispatcher(this);

    public BasicPanel(IMEntity entity) {
        this.entity = entity;

        createHeader();
        createContent();
        createInput();

        update();

        getEventService().register(uiEventDispatcher.getEventTypes(), uiEventDispatcher);
    }

    public IMEntity getEntity() {
        return entity;
    }

    public void setEntity(IMEntity entity) {
        this.entity = entity;
    }

    private void createHeader() {
        avatarImage.setShadeWidth(1);
        avatarImage.setRound(4);
        avatarImage.setDrawGlassLayer(false);

        GroupPanel textGroup = new GroupPanel(0, false, nickLabel, signLabel);
        textGroup.setMargin(0, 10, 0, 5);

        headerPanel.add(avatarImage, BorderLayout.WEST);
        headerPanel.add(new CenterPanel(textGroup, false, true), BorderLayout.CENTER);
        headerPanel.setMargin(8);
        add(headerPanel, BorderLayout.NORTH);
    }

    protected void createContent() {
        msgGroupPanel.setOpaque(false);
        WebScrollPane msgScroll = new WebScrollPane(msgGroupPanel) {
            {
                setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                setBorder(null);
                setMargin(0);
                setShadeWidth(0);
                setRound(0);
                setDrawBorder(false);
                setOpaque(false);
            }
        };

        // 获取JScrollPane中的纵向JScrollBar
        JScrollBar bar = msgScroll.getVerticalScrollBar();
        bar.setUnitIncrement(30);
        bar.addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (isAppendMsg) {
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    isAppendMsg = false;
                }
            }
        });
        add(msgScroll, BorderLayout.CENTER);
    }

    protected void createInput() {
        initInputToolbar(inputToolbar);
        initInputToolbarListener();
        contentInput.setPreferredSize(new Dimension(-1, 80));
        WebScrollPane textPaneScroll = new WebScrollPane(contentInput) {
            private static final long serialVersionUID = 1L;

            {
                setOpaque(false);
                setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                setBorder(null);
                setMargin(5, 5, 5, 5);
                setDrawBorder(false);
            }
        };
        inputPanel.add(inputToolbar, BorderLayout.NORTH);
        inputPanel.add(textPaneScroll, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    protected void initInputToolbar(WebToolBar inputToolbar) {
        setInputButtonStyle(textBtn);
        setInputButtonStyle(emoticonBtn);
        setInputButtonStyle(screenCaptureBtn);
        setInputButtonStyle(picturesBtn);
        setInputButtonStyle(historyBtn);
        setInputButtonStyle(sendBtn);

        // add to Toolbar
        inputToolbar.addSpacing(10);
        // toolbar.add(text);
        inputToolbar.add(emoticonBtn);
        inputToolbar.add(screenCaptureBtn);
        inputToolbar.add(picturesBtn);
        inputToolbar.add(historyBtn);

        inputToolbar.addToEnd(sendBtn);
        inputToolbar.addSpacingToEnd(30);

        // 输入小工具栏
        inputToolbar.setFloatable(false);
        inputToolbar.setToolbarStyle(ToolbarStyle.attached);
        inputToolbar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    private void initInputToolbarListener() {
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg(contentInput.getRichItems());
                contentInput.setText("");
            }
        });
    }

    private void sendMsg(List<UIRichItem> richItems) {
        LogicModule logicModule = IMContext.getBean(LogicModule.class);
        IMMsg msg = new IMMsg();
        msg.setSender(logicModule.getOwner());
        msg.setContents(UIUtils.Bean.toIMItem(richItems));
        msg.setDate(new Date());
        msg.setState(IMMsg.State.PENDING);
        msg.setDirection(IMMsg.Direction.SEND);
        msg.setOwner(entity);
        showMsg(msg);

        getEventService().broadcast(new UIEvent(UIEventType.SEND_MSG_REQUEST, msg));
    }

    public void showMsg(IMMsg msg) {
        isAppendMsg = true;
        msgGroupPanel.add(new MsgPane(msg));
        msgGroupPanel.revalidate();
    }

    protected void setInputButtonStyle(WebButton webButton) {
        webButton.setRound(2);
        webButton.setRolloverDecoratedOnly(true);
    }

    public void update() {
        ImageIcon icon = ImageUtils.createPreviewIcon(entity.getAvatar(), 40);
        avatarImage.setIcon(icon);
        nickLabel.setText(entity.getNick());
        signLabel.setText(entity.getSign());
    }

    @Override
    public void installSkin(SkinService skinService) {
        super.installSkin(skinService);

        // input toolbar icons
        textBtn.setIcon(skinService.getIconByKey("chat/toolbar/text"));
        emoticonBtn.setIcon(skinService.getIconByKey("chat/toolbar/emoticon"));
        screenCaptureBtn.setIcon(skinService.getIconByKey("chat/toolbar/screenCapture"));
        picturesBtn.setIcon(skinService.getIconByKey("chat/toolbar/pictures"));
        historyBtn.setIcon(skinService.getIconByKey("chat/toolbar/history"));
        sendBtn.setIcon(skinService.getIconByKey("chat/toolbar/send"));

        inputToolbar.setPainter(new ColorPainter(new Color(230, 230, 230)));
        inputPanel.setPainter(new ColorPainter(new Color(250, 250, 250)));
        msgGroupPanel.setPainter(new ColorPainter(new Color(250, 250, 250)));
        contentInput.setBackground(new Color(250, 250, 250));
        headerPanel.setPainter(skinService.getPainterByKey("chat/navBg"));

    }

    @UIEventHandler(UIEventType.SEND_MSG_SUCCESS)
    public void onSentSuccessEvent(UIEvent uiEvent) {

    }

    @UIEventHandler(UIEventType.SEND_MSG_ERROR)
    public void onSentErrorEvent(UIEvent uiEvent) {

    }

    @UIEventHandler(UIEventType.RECV_RAW_MSG)
    public void onRecvMsgEvent(UIEvent uiEvent) {
        IMMsg msg = (IMMsg) uiEvent.getTarget();
        showMsg(msg);
    }

    protected EventService getEventService() {
        return IMContext.getBean(EventService.class);
    }

    public void closeChat() {
        getEventService().unregister(uiEventDispatcher);
    }
}
