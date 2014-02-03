package iqq.app.ui.content.chat;

import iqq.app.service.IMSkinService.Type;
import iqq.app.util.SkinUtils;

import com.alee.extended.panel.GroupPanel;

/**
 * 窗口左边的好友会话
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-22
 */
public class ChatListPanel extends GroupPanel {
	private static final long serialVersionUID = 1873186564256405800L;

	private int index = -1;

	public ChatListPanel() {
		super(false);
		setOpaque(false);
		setMargin(0);
		setShadeWidth(0);
		setRound(0);
		setPainter(SkinUtils.getPainter(Type.NPICON, "chatElt"));
	}

	/**
	 * @param indexOf
	 */
	public void setSelectedIndex(int indexOf) {
		this.index = indexOf;
	}

	/**
	 * @return
	 */
	public int getSelectedIndex() {

		return index;
	}

}
