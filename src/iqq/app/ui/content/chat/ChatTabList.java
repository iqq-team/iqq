package iqq.app.ui.content.chat;

import iqq.app.service.IMSkinService.Type;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMFrameView;
import iqq.app.ui.bean.IMChatListElement;
import iqq.app.util.I18NUtil;
import iqq.app.util.SkinUtils;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import com.alee.utils.CollectionUtils;
import com.alee.utils.TextUtils;

/**
 * 聊天标签窗口管理类 关于聊天窗口这几个类不知道怎么命名，回头再想想 ChatTabList -ChatListPanel 左边标签列表
 * --IMChatListElement 一行元素 -IChat 右边聊天窗口 --ChatPanel 个人聊天 --ChatRoomPanel 群聊天
 * 
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-21
 */
public class ChatTabList extends BackgroundPanel {
	private static final long serialVersionUID = 328012209650845820L;
	public static final String ID_PREFIX = "VTP";
	public static ImageIcon REMOVE_ICON = null;
	public static ImageIcon REMOVE_ACTIVE_ICON = null;
	public static ImageIcon REMOVE_FOCUSED_ICON = null;

	private IMFrameView view;
	private CardLayout cardLayout;
	private WebPanel contentPl;
	private ChatListPanel chatList;
	private WebSplitPane splitPane;
	private int leftMaxWidth = 0;

	private static List<ViewListener> viewListeners = new ArrayList<ViewListener>();
	private static List<String> ids = new ArrayList<String>();
	private static List<IChat> chats = new ArrayList<IChat>();
	private static List<IMChatListElement> listElt = new ArrayList<IMChatListElement>();

	private static ChatTabList me;

	public static ChatTabList create(IMFrameView view) {
		if (me == null) {
			me = new ChatTabList(view);
		}
		return me;
	}

	public static ChatTabList me() {
		return me;
	}

	public static boolean isOpenOfEntity(Object obj) {
		for (IChat c : chats) {
			if (c.getEntity() == obj) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param view
	 */
	private ChatTabList(IMFrameView view) {
		super(view);
		super.setOpaque(false);
		this.view = view;
		REMOVE_ICON = SkinUtils.getImageIcon("removeNormal");
		REMOVE_ACTIVE_ICON = SkinUtils.getImageIcon("removeActive");
		REMOVE_FOCUSED_ICON = SkinUtils.getImageIcon("removeFocused");
		cardLayout = new CardLayout();
		contentPl = new WebPanel(cardLayout);
		chatList = new ChatListPanel();
		contentPl.setOpaque(false);
		chatList.setOpaque(false);

		WebScrollPane listSp = new WebScrollPane(chatList, false);
		listSp.setOpaque(false);
		listSp.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		listSp.setBorder(null);
		listSp.setMargin(0);
		listSp.setShadeWidth(0);
		listSp.setRound(0);

		splitPane = new WebSplitPane(WebSplitPane.HORIZONTAL_SPLIT, listSp,
				contentPl);
		splitPane.setOneTouchExpandable(false);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerSize(3);
		add(splitPane);

	}

	/**
	 * 消除聊天窗口
	 */
	public void clearChat() {
		while (chats.size() > 0) {
			removeTabAt(0);
		}
	}

	/**
	 * 判断是否已经打开这个窗口
	 * 
	 * @param chat
	 * @return
	 */
	public boolean isOpened(IChat chat) {
		return chats.contains(chat);
	}

	public void select(IChat chat) {
		if (isOpened(chat)) {
			// 取消之前已经选择的背景
			cancelFontSelectPainter();

			// 获取已经打开的窗口索引
			int index = chats.indexOf(chat);
			// 设置先把索引
			chatList.setSelectedIndex(index);
			// 根据索引，获得左边的一个窗口列表行
			IMChatListElement elt = listElt.get(index);
			// 设置为现选择行
			elt.setSelected(true);
			elt.setRollover(false);
			// 更新已经选择的背景
			updatePainter(elt);
			// 显示右边的聊天窗口内容
			showContent(ids.get(chatList.getSelectedIndex()));

			view.setTitle(I18NUtil.getMessage("conversationTitle",
					chat.getTitle()));
			chat.getInputText().requestFocus();
			
			for(IChat c : chats) {
				if(c == chat) {
					c.setSelected(true);
				} else {
					c.setSelected(false);
				}
			}
		}
	}

	/**
	 * 显示聊天 窗口
	 * 
	 * @param chat
	 */
	public void showChat(IChat chat) {
		// 判断是否已经打开
		if (isOpened(chat)) {
			// 如果打开就选择，不再进行插入
			select(chat);
			return;
		}
		// 获得现在已经选择的索引，+1 插入到现选择的后面
		int index = chatList.getSelectedIndex() + 1;

		// 自动生成一个 ID
		String id = TextUtils.generateId(ID_PREFIX);
		// 创建一个聊天标题
		IMChatListElement elt = new IMChatListElement(chat.getIcon(),
				chat.getTitle(), createRemove());
		// 设置为现选择行
		elt.setSelected(true);
		elt.setRollover(false);
		// 更新已经选择的背景
		updatePainter(elt);

		// 正式插入标题到列表中
		chatList.add(index, elt);
		// 为右边聊天窗口，以id为窗口标记
		contentPl.add(id, chat.getViewContent());

		// 添加ID
		ids.add(index, id);
		// 添加聊天窗口
		chats.add(index, chat);
		// 添加左边标题行
		listElt.add(index, elt);

		// 选择该窗口
		select(chat);

		// 重新验证窗口数据
		revalidation();

		// 通知已经打开了一个新的窗口，以聊天窗口传送
		fireViewOpened(chats.get(index));

		// 添加鼠标监听
		elt.addMouseListener(new MouseAdapter() {

			/**
			 * 当鼠标按下，设置标题的背景，并且显示右边窗口内容
			 */
			@Override
			public void mousePressed(MouseEvent e) {
				IMChatListElement elt = (IMChatListElement) e.getSource();
				select(chats.get(listElt.indexOf(elt)));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				IMChatListElement elt = (IMChatListElement) e.getSource();
				elt.getRemoveBtn().setIcon(REMOVE_FOCUSED_ICON);
				if (listElt.indexOf(elt) != chatList.getSelectedIndex()) {
					elt.setSelected(false);
					elt.setRollover(true);
					updatePainter(elt);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				IMChatListElement elt = (IMChatListElement) e.getSource();
				elt.getRemoveBtn().setIcon(REMOVE_ICON);
				if (listElt.indexOf(elt) != chatList.getSelectedIndex()) {
					elt.setSelected(false);
					elt.setRollover(false);
					updatePainter(elt);
				}
			}
		});

		if (elt.getPreferredSize().getWidth() > leftMaxWidth) {
			leftMaxWidth = (int) elt.getPreferredSize().getWidth();
		}
		if (chats.size() == 1) {
			splitPane.setDividerLocation(0);
		} else {
			splitPane.setDividerLocation(leftMaxWidth);
		}
	}

	/**
	 * 根据ID 显示右边窗口内容
	 * 
	 * @param name
	 */
	private void showContent(String id) {
		cardLayout.show(contentPl, id);
		// 重新验证窗口数据
		revalidation();

	}

	private void cancelFontSelectPainter() {
		int idx = chatList.getSelectedIndex();
		if (idx != -1) {
			IMChatListElement frontSelectedElt = listElt.get(idx);
			frontSelectedElt.setSelected(false);
			frontSelectedElt.setRollover(false);
			updatePainter(frontSelectedElt);
		}
	}

	/**
	 * 更新已经选择标题的背景
	 * 
	 * @param e
	 */
	private void updatePainter(IMChatListElement e) {
		e.updatePainter();

	}

	/**
	 * 创建一个关闭图标按钮
	 * 
	 * @return
	 */
	private WebButton createRemove() {
		// 关闭按钮
		WebButton remove = new WebButton(REMOVE_ICON);
		remove.setPainter(SkinUtils.getPainter(Type.NPICON, "transparent"));
		remove.setMargin(2);
		remove.addMouseListener(new MouseAdapter() {
			/**
			 * 鼠标进入按钮范围显示关闭图标
			 */
			@Override
			public void mouseEntered(MouseEvent e) {
				WebButton rm = (WebButton) e.getSource();
				rm.setEnabled(true);
				rm.setIcon(REMOVE_ACTIVE_ICON);
				setRemoveIcon(rm, REMOVE_ACTIVE_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				WebButton rm = (WebButton) e.getSource();
				rm.setEnabled(true);
				rm.setIcon(REMOVE_FOCUSED_ICON);
				setRemoveIcon(rm, REMOVE_FOCUSED_ICON);
			}
		});
		remove.addActionListener(new ActionListener() {

			/**
			 * 关闭一个聊天窗口
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				WebButton rm = (WebButton) e.getSource();
				// 关闭按钮上层为 窗口标题行包含 IMChatListElement
				IMChatListElement elt = (IMChatListElement) rm.getParent();
				int index = listElt.indexOf(elt);
				close(chats.get(index));
			}
		});

		TooltipManager.setTooltip(remove, I18NUtil.getMessage("close"),
				TooltipWay.right, 300);

		return remove;
	}

	public void setRemoveIcon(WebButton rm, ImageIcon icon) {
		for (IMChatListElement e : listElt) {
			if (e.getRemoveBtn() == rm) {
				rm.setIcon(icon);
			} else {
				e.getRemoveBtn().setIcon(REMOVE_ICON);
			}
		}
	}

	/**
	 * 关闭窗口
	 * 
	 * @param chat
	 */
	public void close(IChat chat) {
		if (chats.contains(chat)) {
			removeTabAt(chats.indexOf(chat));
		}
	}

	/**
	 * 获取已经先把窗口
	 * 
	 * @return
	 */
	public IChat getSelected() {
		int index = chatList.getSelectedIndex();
		return index != -1 ? chats.get(index) : null;
	}

	/**
	 * 清除删除按钮
	 * 
	 * @param index
	 */
	public void removeTabAt(int index) {
		chatList.remove(index);

		IChat removed = chats.get(index);
		ids.remove(index);
		chats.remove(index);
		listElt.remove(index);

		// 如果删除按钮是已经选择按钮，减下索引待选择上一个窗口
		if (index == chatList.getSelectedIndex()) {
			chatList.setSelectedIndex(chats.size() - 1);
			// 如果删除按钮是已经选择按钮，就选择上一个窗口
			if (chats.size() > 0) {
				select(getSelected());
			}
		} else {
			chatList.setSelectedIndex(chatList.getSelectedIndex() - 1);
		}

		// 如果删除按钮是已经选择按钮，就选择上一个窗口
		if (chats.size() == 0) {
			// 已经没有打开任何窗口
			chatList.setSelectedIndex(-1);

			// 隐藏聊天窗口
			view.setVisible(false);
		} else if (chats.size() == 1) {
			splitPane.setDividerLocation(0);
		} else {
			leftMaxWidth = 0;
			for (IMChatListElement e : listElt) {
				if (e.getPreferredSize().getWidth() > leftMaxWidth) {
					leftMaxWidth = (int) e.getPreferredSize().getWidth();
				}
			}
			splitPane.setDividerLocation(leftMaxWidth);
		}

		// 重新验证窗口数据
		revalidation();
		fireViewClosed(removed);
	}

	/**
	 * 重新验证窗口数据
	 */
	private void revalidation() {
		// 重新验证窗口数据
		chatList.revalidate();
		chatList.repaint();
		contentPl.revalidate();
	}

	/**
	 * @return the viewListeners
	 */
	public List<ViewListener> getViewListeners() {
		return viewListeners;
	}

	/**
	 * @param viewListeners
	 *            the viewListeners to set
	 */
	public void setViewListeners(List<ViewListener> viewListeners) {
		this.viewListeners = viewListeners;
	}

	public void addViewListener(ViewListener listener) {
		this.viewListeners.add(listener);
	}

	public void removeViewListener(ViewListener listener) {
		this.viewListeners.remove(listener);
	}

	private void fireViewOpened(IChat chat) {
		for (ViewListener listener : CollectionUtils.clone(viewListeners)) {
			listener.viewOpened(chat);
		}
	}

	private void fireViewClosed(IChat chat) {
		for (ViewListener listener : CollectionUtils.clone(viewListeners)) {
			listener.viewClosed(chat);
		}
	}

	/**
	 * @return the ids
	 */
	public List<String> getIds() {
		return ids;
	}

	/**
	 * @param ids
	 *            the ids to set
	 */
	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	/**
	 * @return the data
	 */
	public List<IChat> getData() {
		return chats;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(List<IChat> data) {
		this.chats = data;
	}

}
