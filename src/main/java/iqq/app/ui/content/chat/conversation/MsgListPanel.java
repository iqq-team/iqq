package iqq.app.ui.content.chat.conversation;

import iqq.app.bean.UIMsg;
import iqq.app.ui.IMFrameView;
import iqq.im.bean.QQUser;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import com.alee.extended.panel.GroupPanel;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-24
 */
public class MsgListPanel extends GroupPanel {
	private static final long serialVersionUID = -3757572461881253014L;

	/**
	 * @param view
	 */
	public MsgListPanel(IMFrameView view) {
		super(false);
		setOpaque(true);
		setBackground(new Color(245, 245, 245));
	}
	
	public void updateMsg(UIMsg msg){
		for(Component comp: getComponents()){
			if(comp instanceof UIMsgPane){
				UIMsgPane pane = (UIMsgPane) comp;
				UIMsg cur = pane.getMsg();
				String msgId = cur.getMsgId();
				if(msgId != null && msgId.endsWith(msg.getMsgId())){
					pane.invalidate();
					return;
				}
			}
		}
	}
	
	public void updateUser(QQUser user){
		for(Component comp: getComponents()){
			if(comp instanceof UIMsgPane){
				UIMsgPane pane = (UIMsgPane) comp;
				UIMsg cur = pane.getMsg();
				QQUser sender = cur.getSender();
				if( user.equals(sender)){
					pane.invalidate();
				}
			}
		}
	}
	
	public List<UIMsg> getMsgList(){
		List<UIMsg> list = new ArrayList<UIMsg>();
		for(Component comp: getComponents()){
			if(comp instanceof UIMsgPane){
				UIMsgPane pane = (UIMsgPane) comp;
				list.add(pane.getMsg());
			}
		}
		return list;
	}

}
