/**
 * 
 */
package iqq.app.service.impl;

import java.util.HashMap;
import java.util.Map;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.service.IMActionService;
import iqq.app.ui.action.IMActionMap;

/**
 * @author ZhiHui_Chen
 * 
 * @author 承∮诺 <6208317@qq.com>
 */
public class IMActionServiceImpl extends AbstractServiceImpl implements
		IMActionService {
	private Map<Object, IMActionMap> globalActionMap;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);
		globalActionMap = new HashMap<Object, IMActionMap>();
	}

	@Override
	public IMActionMap getActionMap(Object actionsObject) {
		return getActionMap(null, actionsObject);
	}

	@Override
	public IMActionMap getActionMap(Class<? extends Object> actionsClass,
			Object actionsObject) {
		if (actionsClass != null) {
			actionsObject = actionsClass.cast(actionsObject);
		}
		if (actionsObject == null) {
			throw new IllegalArgumentException("null actionsObject");
		}
		if (actionsClass != null
				&& !actionsClass.isAssignableFrom(actionsObject.getClass())) {
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
