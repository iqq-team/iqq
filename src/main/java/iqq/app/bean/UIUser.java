package iqq.app.bean;

import iqq.app.IMApp;
import iqq.app.util.UIUtils;
import iqq.im.bean.QQBuddy;
import iqq.im.bean.QQGroupMember;
import iqq.im.bean.QQStatus;
import iqq.im.bean.QQUser;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class UIUser implements UINamedObject {

	private QQUser user;
	private BufferedImage face;		//上一次调用getIcon的Face，用于判断头像是否有更改
	private Map<QQStatus, BufferedImage> faces; 	//画出的头像缓存
	
	public UIUser(QQUser user) {
		this.user = user;
		this.face = user.getFace();
		this.faces = new HashMap<QQStatus, BufferedImage>();
	}
	
	@Override
	public String getName() {
		return user.getNickname();
	}
	
	@Override
	public String getSubname() {
		//这里偷个懒，懒得实现UIBuddy和UIGroupMember,减少下类数量
		if(user instanceof QQBuddy){
			return ((QQBuddy) user).getMarkname();
		}else if(user instanceof QQGroupMember){
			return ((QQGroupMember) user).getCard();
		}else{
			return "";
		}
	}

	@Override
	public BufferedImage getIcon() {
		//return UIUtils.Bean.drawStatusFace(user, IMApp.me());
		BufferedImage f = null;
		if(user.getFace() == face){
			 f = faces.get(user.getStatus());
			if(f == null){
				f = UIUtils.Bean.drawStatusFace(user, IMApp.me());
				faces.put(user.getStatus(), f);
			}
			return f;
		}
		
		//头像有变化，需要重画
		face = user.getFace();
		faces.clear();
		f = UIUtils.Bean.drawStatusFace(user, IMApp.me());
		faces.put(user.getStatus(), f);
		return f;
	}
	
	@Override
	public Object getEntity() {
		return user;
	}

	public QQUser getUser() {
		return user;
	}

	public void setUser(QQUser user) {
		this.user = user;
	}

	@Override
	public String getExtra() {
		return user.getSign();
	}

}
