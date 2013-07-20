package iqq.app.util;

import iqq.im.bean.QQStatus;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;

import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebMenuItem;

/**
 * @author ChenZhiHui
 * @create-time 2013-3-8
 */
public class StatusUtils {
	public final static String STATUS_ICON_PREFIX = "status_";
	public final static String STATUS_I18N_PREFIX = "status.";
	public static Map<QQStatus, WebMenuItem> statusMap;

	public static ImageIcon getStatusIcon(String name) {
		if (StringUtils.isNotEmpty(name)) {
			return SkinUtils.getImageIcon(STATUS_ICON_PREFIX + name);
		}
		return null;
	}

	public static ImageIcon getStatusIcon(QQStatus status) {
		if (status != null) {
			return getStatusIcon(status.getValue());
		}
		return null;
	}

	public static WebLabel statusToLabel(QQStatus status) {
		if (status != null) {
			return new WebLabel(status.getValue(), IMImageUtil.getScaledInstance(
					getStatusIcon(status), 12, 12));
		}
		return null;
	}

	public static WebMenuItem statusToMenuItem(QQStatus status) {
		if (statusMap == null) {
			getStatus();
		}
		if (status != null) {
			WebMenuItem item = statusMap.get(status);
			if (item == null) {
				item = new WebMenuItem(I18NUtil.getMessage(STATUS_I18N_PREFIX
						+ status.getValue()), IMImageUtil.getScaledInstance(
						getStatusIcon(status), 12, 12));
				statusMap.put(status, item);
			}
			return item;
		}

		new NullPointerException("QQUserStatus: " + status);
		return null;
	}

	public static Map<QQStatus, WebMenuItem> getStatus() {
		statusMap = new LinkedHashMap<QQStatus, WebMenuItem>();
		QQStatus[] status = QQStatus.values();
		for (QQStatus state : status) {
			statusMap.put(state, StatusUtils.statusToMenuItem(state));
			//System.out.println("status: " + status);
		}
		return statusMap;
	}

	public static String statusToI18N(QQStatus status) {
		return I18NUtil.getMessage(STATUS_I18N_PREFIX + status.getValue());
	}

}
