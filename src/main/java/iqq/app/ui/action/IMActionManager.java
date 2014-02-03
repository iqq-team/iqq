package iqq.app.ui.action;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-4-1
 */
public class IMActionManager {
	private Map<Object, IMActionMap> globalActionMap;

	public IMActionManager() {
		globalActionMap = new HashMap<Object, IMActionMap>();
	}

	public IMActionMap getActionMap(Object actionsObject) {
		return getActionMap(null, actionsObject);
	}

	public IMActionMap getActionMap(Class<? extends Object> actionsClass,
			Object actionsObject) {
		if (actionsClass != null) {
			actionsObject = actionsClass.cast(actionsObject);
		}
		if (actionsObject == null) {
			throw new IllegalArgumentException("null actionsObject");
		}
		if (actionsClass != null && !actionsClass.isAssignableFrom(actionsObject.getClass())) {
			throw new IllegalArgumentException(
					"actionsObject not instanceof actionsClass");
		}

		IMActionMap actionMap = globalActionMap.get(actionsObject);
		if (actionMap == null) {
			actionMap = new IMActionMap(actionsObject);
			globalActionMap.put(actionsObject, actionMap);
		}
		return actionMap;
	}

}
