package iqq.app.ui.bean;

import iqq.app.bean.DefaultUINamedObject;
import iqq.app.bean.UICategory;
import iqq.app.bean.UINamedObject;
import iqq.app.util.IMImageUtil;
import iqq.app.util.SkinUtils;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.alee.laf.panel.WebPanel;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-19
 */
public class IMNodeData extends WebPanel {
	private static final long serialVersionUID = 3532566298469715048L;
	public static final String KEY_ICON = "icon";
	public static final String KEY_LEAF_ICON = "leaf_icon";
	public static final String KEY_OPEN_ICON = "open_icon";
	public static final String KEY_CLOSED_ICON = "closed_icon";
	public static final String KEY_TEXT = "text";

	private UINamedObject namedObject;
	private Map<String, Object> values;

	public IMNodeData(UINamedObject namedObject) {
		super();
		values = new HashMap<String, Object>();
		this.namedObject = namedObject;

		if (namedObject instanceof UICategory
				|| namedObject instanceof DefaultUINamedObject) {
			addString(KEY_TEXT, namedObject.getName());
			addImageIcon(
					KEY_OPEN_ICON,
					IMImageUtil.getScaledInstance(
							SkinUtils.getImageIcon("arrowDown"), 8, 8));
			addImageIcon(
					KEY_CLOSED_ICON,
					IMImageUtil.getScaledInstance(
							SkinUtils.getImageIcon("arrowLeft"), 8, 8));
		}

	}

	public Object getEntity() {
		return namedObject.getEntity();
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> map) {
		this.values = map;
	}

	public String getString(String key) {
		return (String) values.get(key);
	}

	public void addString(String key, String value) {
		this.values.put(key, value);
	}

	public Object get(String key) {
		return values.get(key);
	}

	public void add(String key, Object obj) {
		this.values.put(key, obj);
	}

	public ImageIcon getImageIcon(String key) {
		return (ImageIcon) values.get(key);
	}

	public void addImageIcon(String key, ImageIcon icon) {
		this.values.put(key, icon);
	}

	public UINamedObject getNamedObject() {
		return namedObject;
	}

	public void setNamedObject(UINamedObject namedObject) {
		this.namedObject = namedObject;
	}

}
