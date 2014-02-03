package iqq.app.ui.module;

import iqq.app.bean.UIDiscuz;
import iqq.app.bean.UIGroup;
import iqq.app.bean.UIMsg;
import iqq.app.bean.UIUser;
import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.ui.IMFrameView;
import iqq.app.ui.content.chat.ChatTabList;
import iqq.app.ui.content.chat.IChat;
import iqq.app.ui.content.chat.ViewListener;
import iqq.app.ui.content.chat.conversation.ChatPanel;
import iqq.app.ui.widget.IMTitleComponent;
import iqq.app.util.LocationUtil;
import iqq.app.util.SettingUtils;
import iqq.app.util.SkinUtils;
import iqq.im.bean.QQAccount;
import iqq.im.bean.QQDiscuz;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQUser;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alee.laf.panel.WebPanel;

/**
 * 聊天UI模式
 * 
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-21
 */
public class UIChatModule extends IMFrameView {
	private static final long serialVersionUID = -3177027375988433502L;
	private static final Logger LOG = Logger.getLogger(UIChatModule.class);
	private static final Map<Object, IChat> chatEntities = new HashMap<Object, IChat>();
	private static ChatTabList chatTabList;
	private QQAccount self;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		// 初始化窗口
		initFrame();
		// 初始化窗口列表
		initChatList();

		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);
	}

	/**
	 * 初始化窗口列表
	 */
	private void initChatList() {
		chatTabList = ChatTabList.create(this);

		WebPanel pl = new WebPanel();
		pl.add(new IMTitleComponent(this), BorderLayout.PAGE_START);
		pl.add(chatTabList, BorderLayout.CENTER);
		this.setContentPanel(pl);

		// 监听打开对话的监听
		chatTabList.addViewListener(new ViewListener() {
			@Override
			public void viewOpened(IChat chat) {}
			@Override
			public void viewClosed(IChat chat) {
				chatEntities.remove(chat.getEntity());
			}
		});
	}

	private void initFrame() {
		// 设置Logo
		setTitle("Conversations");
		setIconImage(SkinUtils.getImageIcon("conversations").getImage());
		setSize(SettingUtils.getInt("chatWidth"),
				SettingUtils.getInt("chatHeight"));

		// 设置在屏幕显示位置，左中
        setLocation(LocationUtil.getScreenLeft(getWidth(), getHeight()));
		setDefaultCloseOperation(HIDE_ON_CLOSE);
        setAlwaysOnTop(false);
        
		// 窗口关闭事件，消除聊天窗口
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				chatEntities.clear();
				chatTabList.clearChat();
			}
		});
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
	}

	@IMEventHandler(IMEventType.SELF_FACE_UPDATE)
	public void processSelfFaceUpdate(IMEvent event) {
		this.self = (QQAccount) event.getTarget();
	}

	@IMEventHandler(IMEventType.USER_FACE_UPDATE)
	public void processUserFaceUpdate(IMEvent event) {
		QQUser user = (QQUser) event.getTarget();
		for(IChat chat: chatEntities.values()){
			chat.updateUser(user);
		}
	}
	
	@IMEventHandler(IMEventType.CLIENT_OFFLINE)
	protected void processIMClientOffline(IMEvent event){
		for(IChat chat: chatEntities.values()){
			if(chat.getEntity() instanceof QQUser){
				chat.updateUser((QQUser) chat.getEntity());
			}
		}
	}

	@IMEventHandler(IMEventType.MSG_HISTORY_UPDATE)
	public void processMsgHistoryUpdate(IMEvent event) {
		IMEvent imEvt = event.getRelatedEvent();
		String isComing = imEvt.getData("coming");
		if(isComing == null || !isComing.equals("true")) return ;
		List<UIMsg> msgList = (List<UIMsg>) event.getTarget();
		IChat chat = chatEntities.get(event.getRelatedEvent().getTarget());
		if (chat != null) {
			for (UIMsg uiMsg : msgList) {
				chat.showMsg(uiMsg);
				QQUser fromUser = uiMsg.getSender();
				if(fromUser.getFace() == null){
					broadcastIMEvent(IMEventType.USER_CACHE_FIND, fromUser);
				}
				
				if(uiMsg.getState() == UIMsg.State.UNREAD){
					uiMsg.setState(UIMsg.State.READ);
					broadcastIMEvent(IMEventType.UPDATE_UIMSG, uiMsg);
				}
			}
		}
	}
	
	@IMEventHandler(IMEventType.USER_CACHE_UPDATE)
	public void processUserCacheUpdate(IMEvent event) {
		QQUser user = (QQUser) event.getTarget();
		for(IChat chat: chatEntities.values()){
			chat.updateUser(user);
		}
	}
	

	@IMEventHandler(IMEventType.SHOW_CHAT)
	public void processShowChat(IMEvent event) {
		Object entity = event.getTarget();
		IChat chat = chatEntities.get(entity);
		if (chat == null) {
			if (entity instanceof QQUser) {
				chat = new ChatPanel(this, new UIUser((QQUser) entity), self);
			} else if (entity instanceof QQGroup) {
				chat = new ChatPanel(this, new UIGroup((QQGroup) entity), self);
			} else if (entity instanceof QQDiscuz) {
				chat = new ChatPanel(this, new UIDiscuz((QQDiscuz) entity), self);
			}
			chatEntities.put(entity, chat);
			
			if(entity instanceof QQGroup) {
				IMEventService events = getContext().getSerivce(IMService.Type.EVENT);
				events.broadcast(new IMEvent(IMEventType.GROUP_INFO_REQUEST, entity));
			} else if(entity instanceof QQDiscuz) {
				IMEventService events = getContext().getSerivce(IMService.Type.EVENT);
				events.broadcast(new IMEvent(IMEventType.DISCUZ_INFO_REQUEST, entity));
			}

			// 显示所有消息
			IMEvent imEvent = new IMEvent(IMEventType.MSG_HISTORY_FIND, event.getTarget());
			imEvent.putData("state", UIMsg.State.UNREAD);
			imEvent.putData("coming", "true");
			IMEventService events = getContext().getSerivce(IMService.Type.EVENT);
			events.broadcast(imEvent);
		}
		chatTabList.showChat(chat);
		this.setVisible(true);
	}
	
	@IMEventHandler(IMEventType.UPDATE_UIMSG)
	protected void processIMUpdateUIMsg(IMEvent event){
		UIMsg uiMsg = (UIMsg) event.getTarget();
		for(IChat chat: chatEntities.values()){
			chat.updateMsg(uiMsg);
		}
	}
	
	@IMEventHandler(IMEventType.RECV_CHAT_MSG)
	protected void processIMRecvChatMsg(IMEvent event){
		UIMsg uiMsg = (UIMsg) event.getTarget();
		IChat chat = chatEntities.get(uiMsg.getOwner());
		if (chat != null) {
			chat.showMsg(uiMsg);
			uiMsg.setState(UIMsg.State.READ);
			broadcastIMEvent(IMEventType.UPDATE_UIMSG, uiMsg);
		}
		
		if(uiMsg.getSender().getFace() == null){
			broadcastIMEvent(IMEventType.USER_CACHE_FIND, uiMsg.getSender());
		}
	}
	
	@IMEventHandler(IMEventType.SHOW_INFO_MSG)
	protected void processIMShowInfoMsg(IMEvent event){
		processIMRecvChatMsg(event);
	}
	
	@IMEventHandler(IMEventType.USER_INPUTTING)
	protected void processIMUserInputing(IMEvent event){
		IChat chat = chatEntities.get(event.getTarget());
		if(chat != null){
			chat.showInput();
		}
	}
}
