package iqq.app.ui.content.main;

import iqq.app.core.IMContext;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.renderer.IMBuddySearchRenderer;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQBuddy;
import iqq.im.bean.QQUser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;

import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;

/**
 * @author auqtoss	<grasp_now@126.com>
 * @create-date 2013-12-18
 */
public class SearchBuddyPanel extends BackgroundPanel{

	private static final long serialVersionUID = 1L;

	private WebList findResultList;
	private DefaultListModel defaultListModel;
	public SearchBuddyPanel(Window view) {
		super(view);
		setRound(10);
		setOpaque(false);
		initComponent();
	}
	
	public void initComponent(){
		
		WebPanel middlePl = new WebPanel();
		middlePl.setOpaque(false);
		middlePl.setMargin(0, 10, 0, 10);
		middlePl.setBackground(Color.gray);
		add(middlePl,BorderLayout.CENTER);
	}
	
	
	public WebScrollPane addBuddyInfoPanel(final List<QQBuddy> buddys,final IMContext ic,String keyword){
		defaultListModel = new DefaultListModel();
		findResultList =  new WebList(defaultListModel);
		findResultList.setCellRenderer(new IMBuddySearchRenderer());
		keyword = keyword.toLowerCase();
		for(QQBuddy buddy : buddys){
			String nickName = buddy.getNickname();
			String[] pinyinResults = UIUtils.PinyinConvert.getPinyin(nickName);
			//¹ýÂËËÑË÷½á¹û
			if(nickName.contains(keyword) 
					|| ((pinyinResults[0].length()>0?pinyinResults[0].contains(keyword.charAt(0)+"".trim()):false) 
							|| (pinyinResults[1].length()>0?pinyinResults[1].contains(keyword.charAt(0)+"".trim()):false) ) 
					&& Arrays.toString(pinyinResults).replaceAll("\\[|]|,| ", "").contains(keyword))
				defaultListModel.addElement(new Object[]{buddy.getFace(),buddy.getNickname()});

		}
		findResultList.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getClickCount()==2){
					QQUser user = null;
					System.out.println();
					for (QQBuddy buddy : buddys) {
						if(((Object[])findResultList.getSelectedValues()[0])[1].equals(buddy.getNickname())){
							user = buddy;
						}
					}
					IMEvent imEvent = new IMEvent(IMEventType.SHOW_CHAT, user);
					IMEventService events = ic.getSerivce(IMService.Type.EVENT);
					events.broadcast(imEvent);
				}
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		return new WebScrollPane(findResultList){
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
	}
	

	

}
