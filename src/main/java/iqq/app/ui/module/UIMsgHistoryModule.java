/**
 * 
 */
package iqq.app.ui.module;

import iqq.app.bean.UIMsg;
import iqq.app.bean.UINamedObject;
import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMDialogView;
import iqq.app.ui.content.chat.conversation.RichTextPane;
import iqq.app.ui.widget.IMTitleComponent;
import iqq.app.util.IMImageUtil;
import iqq.app.util.SkinUtils;
import iqq.im.bean.QQAccount;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.SwingConstants;

import com.alee.extended.layout.WrapFlowLayout;
import com.alee.extended.painter.ColorPainter;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.WrapPanel;
import com.alee.laf.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;

/**
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-17
 */
public class UIMsgHistoryModule extends IMDialogView {
	private static final long serialVersionUID = 5662186451061358867L;
	private MsgHistoryPanel content;

	private UINamedObject namedObject;
	private int limit = 10;
	private int currentPage = 0;
	private int totalPage = 0;
	

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);
		IMEventHandlerProxy.register(this, context);

		initView();
		intiContent();
	}

	@Override
	public void destroy() throws IMException {
		super.destroy();
		IMEventHandlerProxy.unregister(this);
	}

	/**
	 * initialization
	 */
	private void initView() {
		setSize(300, 480);
		setLocationRelativeTo(null);
		// setAlwaysOnTop(true);
	}

	/**
	 * content
	 */
	private void intiContent() {
		IMTitleComponent title = getIMTitleComponent();
		title.setShowMinimizeButton(false);
		title.setShowMaximizeButton(false);

		content = new MsgHistoryPanel(this);
		WebPanel rootContent = new WebPanel();
		rootContent.add(title, BorderLayout.PAGE_START);
		rootContent.add(content, BorderLayout.CENTER);
		setContentPanel(rootContent);
		changeSkin(new ColorPainter(Color.LIGHT_GRAY));
	}

	@IMEventHandler(IMEventType.SHOW_MSG_HISTORY_WINDOW)
	protected void processIMShowWindow(IMEvent event) {
		namedObject = (UINamedObject) event.getTarget();
		setTitle(namedObject.getName() + " - Message History.");

		Window win = event.getData("view");
		Rectangle rect = win.getBounds();
		setLocation(rect.x + rect.width - 50, rect.y);
		setSize(getWidth(), rect.height);
		validate();
		setVisible(true);

		currentPage = 0;
		totalPage = 0;
		
		// 显示消息
		IMEvent imEvent = new IMEvent(IMEventType.MSG_HISTORY_FIND, namedObject.getEntity());
		imEvent.putData("limit", limit);
		IMEventService events = getContext().getSerivce(IMService.Type.EVENT);
		events.broadcast(imEvent);
		
		win.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				int x = e.getComponent().getX() + e.getComponent().getWidth()
						- 50;
				int y = e.getComponent().getY();
				if (isPtInRect(getBounds(), new Point(x, y))) {
					setLocation(x, y);
					setSize(getWidth(), e.getComponent().getHeight());
				}
			}
		});
	}
	
	@IMEventHandler(IMEventType.MSG_HISTORY_UPDATE)
	public void processIMHistoryMsg(IMEvent event) {
		IMEvent related = event.getRelatedEvent();
		//如果发起查询的事件是查询未读的消息，就不响应
		if(related.getData("state") == UIMsg.State.UNREAD){
			return;
		}
		List<UIMsg> msgList = (List<UIMsg>) event.getTarget();
		content.clearLocalMsg();
		Integer total =  event.getData("total");
		content.setPageInfo(getTotalPage(total));
		if(!msgList.isEmpty()) {
			content.setFirstKey(msgList.get(0).getMsgId());
			content.setLastKey(msgList.get(msgList.size() - 1).getMsgId());
		}
		for(UIMsg msg : msgList) {
			WrapPanel msgWrap = new WrapPanel(new UIHistoryMsgPane(msg, null));
			WrapFlowLayout wf = new WrapFlowLayout();
			wf.setHalign(SwingConstants.LEFT);
			msgWrap.setLayout(wf);
			content.addLocalMsg(msgWrap);
		}
		
		content.revalidate();
		content.repaint();
	}
	
	public int getTotalPage(int total) {
		return (total % limit == 0) ? total / limit : total / limit + 1;
	} 

	/**
	 * 检测是否在矩形框内
	 * 
	 * @param rect
	 * @param point
	 * @return
	 */
	public boolean isPtInRect(Rectangle rect, Point point) {
		if (rect != null && point != null) {
			int x0 = rect.x;
			int y0 = rect.y;
			int x1 = rect.width + x0;
			int y1 = rect.height + y0;
			int x = point.x + 45;
			int y = point.y + 45;

			return x >= x0 && x < x1 && y >= y0 && y < y1;
		}
		return false;
	}
	
	public class UIHistoryMsgPane extends WebPanel {
		private static final long serialVersionUID = -6282047626247695106L;
		private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
		private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd");
		private UIMsg msg;
		private QQAccount account;
		private WebLabel infoLabl;
		private RichTextPane contnetPane;
		public UIHistoryMsgPane(UIMsg msg, QQAccount account){
			this.msg = msg;
			this.account = account;
			initComponent();
			setMargin(5, 10, 5, 10);
			setOpaque(false);
		}
		
		private void initComponent() {
			// 小于一天就显示时间，否则就显示日期
			String time = DATE_FORMAT.format(msg.getDate());
			if(System.currentTimeMillis() - msg.getDate().getTime() < 24*3600*1000){
				time = TIME_FORMAT.format(msg.getDate());
			}
			infoLabl = new WebLabel(msg.getSender().getNickname() + " " + time);
			infoLabl.setForeground(new Color(70, 160, 220));
			infoLabl.setMargin(5);
			contnetPane = new RichTextPane();
			contnetPane.setRichItems(msg.getContents());
			contnetPane.setEditable(false);
			
			add(infoLabl, BorderLayout.PAGE_START);
			add(contnetPane, BorderLayout.CENTER);
		}
		
	}

	public class MsgHistoryPanel extends BackgroundPanel {
		private static final long serialVersionUID = 8859545941569824057L;
		WebPanel headerPl;
		WebPanel contentPl;
		WebPanel footerPl;
		GroupPanel localMsgPl;
		GroupPanel onlineMsgPl;
		WebLabel pageTotal;
		WebTextField pageFld;
		
		String firstKey = null;
		String lastKey = null;
		
		/**
		 * @param view
		 */
		public MsgHistoryPanel(Window view) {
			super(view);
			
			initComponent();
			add(headerPl, BorderLayout.PAGE_START);
			add(contentPl, BorderLayout.CENTER);
			add(footerPl, BorderLayout.PAGE_END);
		}

		/**
		 * initialization
		 */
		private void initComponent() {
			headerPl = new WebPanel();
			contentPl = new WebPanel();
			footerPl = new WebPanel();
			localMsgPl = new GroupPanel(false);
			onlineMsgPl = new GroupPanel(false);
			
			WebScrollPane localMsgScroll = new WebScrollPane(localMsgPl) {
				private static final long serialVersionUID = 1L;

				{
					setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					setBorder(null);
					setMargin(0);
					setShadeWidth(0);
					setRound(0);
					setDrawBorder(false);
				}
			};
			
			/*
			WebScrollPane onlineMsgScroll = new WebScrollPane(onlineMsgPl) {
				private static final long serialVersionUID = 1L;

				{
					setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					setBorder(null);
					setMargin(0);
					setShadeWidth(0);
					setRound(0);
					setDrawBorder(false);
				}
			};
			WebTabbedPane msgTab = new WebTabbedPane();
			msgTab.add("Local", localMsgScroll);
			msgTab.add("Online", onlineMsgScroll);
			msgTab.setTabbedPaneStyle(TabbedPaneStyle.attached);
			msgTab.setTabStretchType(TabStretchType.always);
			msgTab.setOpaque(false);
			msgTab.setPainter(SkinUtils.getPainter(Type.NPICON, "transparent"));
			msgTab.setBackgroundAt(0, Color.WHITE);
			msgTab.setBackgroundAt(1, Color.WHITE);
			contentPl.add(msgTab);
			*/
			contentPl.add(localMsgScroll);
			
			WebButton searcherBtn = WebButton.createIconWebButton(
					IMImageUtil.getScaledInstance(
							SkinUtils.getImageIcon("searchNormal"), 18, 18),
					StyleConstants.smallRound, true);
			WebButton pagePrev = WebButton.createIconWebButton(
					IMImageUtil.getScaledInstance(
							SkinUtils.getImageIcon("chat/msghistory/arrow/left"), 18, 18),
							StyleConstants.smallRound, true);
			WebButton pageNext = WebButton.createIconWebButton(
					IMImageUtil.getScaledInstance(
							SkinUtils.getImageIcon("chat/msghistory/arrow/right"), 18, 18),
							StyleConstants.smallRound, true);
			WebTextField pageFld = new WebTextField();
			pageFld.setPreferredSize(new Dimension(30, 20));
			pageTotal = new WebLabel("/0 Page");
			pageTotal.setForeground(Color.GRAY);
			
			footerPl.add(searcherBtn, BorderLayout.LINE_START);
			footerPl.add(new GroupPanel(true, pagePrev, pageTotal, pageNext), BorderLayout.LINE_END);
			footerPl.setOpaque(true);
			footerPl.setBackground(Color.LIGHT_GRAY);
			
			pagePrev.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(currentPage == 1) {
						return ;
					} else {
						currentPage--;
					}
					
					// 显示消息
					IMEvent imEvent = new IMEvent(IMEventType.MSG_HISTORY_FIND, namedObject.getEntity());
					imEvent.putData("start", firstKey);
					imEvent.putData("limit", limit);
					imEvent.putData("direct", "older");
					IMEventService events = getContext().getSerivce(IMService.Type.EVENT);
					events.broadcast(imEvent);
				}
			});
			pageNext.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(currentPage == totalPage) {
						return ;
					} else {
						currentPage++;
					}
					// 显示消息
					IMEvent imEvent = new IMEvent(IMEventType.MSG_HISTORY_FIND, namedObject.getEntity());
					imEvent.putData("start", lastKey);
					imEvent.putData("limit", limit);
					imEvent.putData("direct", "newer");
					IMEventService events = getContext().getSerivce(IMService.Type.EVENT);
					events.broadcast(imEvent);
				}
			});
		}
		
		public void addLocalMsg(WebPanel msgPane) {
			this.localMsgPl.add(msgPane);
		}
		public void addOnlineMsg(WebPanel msgPane) {
			this.onlineMsgPl.add(msgPane);
		}
		public void clearLocalMsg() {
			localMsgPl.removeAll();
		}
		public void clearOnlineMsg() {
			localMsgPl.removeAll();
		}

		public int getLimit() {
			return limit;
		}

		public WebTextField getPageFld() {
			return pageFld;
		}

		public void setPageFld(WebTextField pageFld) {
			this.pageFld = pageFld;
		}

		public String getFirstKey() {
			return firstKey;
		}

		public void setFirstKey(String firstKey) {
			this.firstKey = firstKey;
		}

		public String getLastKey() {
			return lastKey;
		}

		public void setLastKey(String lastKey) {
			this.lastKey = lastKey;
		}

		public void setPageInfo(int count) {
			if(currentPage == 0) {
				currentPage = count;
			}
			totalPage = count;
			pageTotal.setText(currentPage + " / " + totalPage + " page");
		}
	}
}
