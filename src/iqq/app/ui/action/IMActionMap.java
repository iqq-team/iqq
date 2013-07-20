package iqq.app.ui.action;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-4-1
 */
public class IMActionMap {
	private Map<String, IMActionHandlerProxy> actionMap;
	private Object actionsObject;

	public IMActionMap(Object actionsObject) {
		this.actionsObject = actionsObject;
		actionMap = new HashMap<String, IMActionHandlerProxy>();
	}

	public Action get(String key) {
		Action action = actionMap.get(key);
		if (action == null) {
			action = new IMActionHandlerProxy(actionsObject, key);
		}

		return action;
	}
}
