package iqq.app.service;

import iqq.app.core.IMService;
import iqq.app.ui.action.IMActionMap;

/**
 * 
 * @author ZhiHui_Chen
 * 
 * @author 承∮诺 <6208317@qq.com>
 */
public interface IMActionService extends IMService {
	public IMActionMap getActionMap(Object actionsObject);

	public IMActionMap getActionMap(Class<? extends Object> actionsClass,
			Object actionsObject);
}
