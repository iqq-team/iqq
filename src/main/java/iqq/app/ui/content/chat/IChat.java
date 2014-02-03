package iqq.app.ui.content.chat;

import iqq.app.bean.UIMsg;
import iqq.app.ui.content.chat.conversation.RichTextPane;
import iqq.im.bean.QQUser;

import java.awt.Component;
import java.util.List;

import javax.swing.Icon;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-21
 */
public interface IChat {
	public Icon getIcon();

	public String getTitle();

	public Component getViewContent();
	
	public Object getEntity();
	
	public RichTextPane getInputText();
	
	public void showMsg(UIMsg msg);

	public void updateMsg(UIMsg msg); 
	
	public List<UIMsg> getMsgList();
	
	public void updateUser(QQUser user);

	public void showInput();
	
	public void setSelected(boolean selected);
}
