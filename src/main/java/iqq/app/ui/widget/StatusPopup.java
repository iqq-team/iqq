package iqq.app.ui.widget;

import iqq.app.service.IMSkinService.Type;
import iqq.app.util.IMImageUtil;
import iqq.app.util.SkinUtils;
import iqq.app.util.StatusUtils;
import iqq.im.bean.QQStatus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.menu.WebMenuItem;
import com.alee.managers.popup.PopupStyle;
import com.alee.managers.popup.PopupWay;
import com.alee.managers.popup.WebButtonPopup;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-18
 */
public class StatusPopup extends WebButton implements ActionListener {

	private WebButtonPopup popup;
	private GroupPanel gp;
	private QQStatus status = QQStatus.OFFLINE;
	private Map<QQStatus, WebMenuItem> statusMap;
	private StatusChangeListner statusListener;

	public StatusPopup() {
		super();
		this.setPainter(SkinUtils.getPainter(Type.NPICON, "transparent"));

		popup = new WebButtonPopup(this, PopupWay.downRight);
		popup.setPopupStyle(PopupStyle.lightSmall);	// QQ状态样式
		gp = new GroupPanel(false);
		setCurrentStatus(QQStatus.ONLINE);
		initPopupList();
	}

	private static final long serialVersionUID = 651258159455991962L;

	private void initPopupList() {
		statusMap = StatusUtils.getStatus();
		for (QQStatus status : statusMap.keySet()) {
			WebMenuItem menuItem = statusMap.get(status);
			menuItem.addActionListener(this);
			gp.add(menuItem);
		}

		popup.setContent(gp);
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (popup.isShowing()) {
					popup.hidePopup();
				} else {
					popup.showPopup(StatusPopup.this);
				}
			}
		});
	}

	public void setCurrentStatus(QQStatus status) {
		this.status = status;
		this.setText(StatusUtils.statusToI18N(status));
		this.setIcon(IMImageUtil.getScaledInstance(
				StatusUtils.getStatusIcon(status), 15, 15));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (QQStatus status : statusMap.keySet()) {
			if (statusMap.get(status) == e.getSource()) {
				if(this.status != status){	//不相同才触发
					if(statusListener != null){
						statusListener.statusChanged(status, this.status);
					}
					this.status = status;
					setCurrentStatus(status);
				}
				break;
			}
		}
		popup.hidePopup();
	}

	/**
	 * @return the status
	 */
	public QQStatus getStatus() {
		return status;
	}

	/**
	 * @param statusListener the statusListener to set
	 */
	public void setStatusListener(StatusChangeListner statusListener) {
		this.statusListener = statusListener;
	}



	public interface StatusChangeListner{
		public void statusChanged(QQStatus newStatus, QQStatus oldStatus);
	}
}
