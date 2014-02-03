package iqq.app.ui.action;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-4-1
 */
public class IMActionHandlerProxy extends AbstractAction {
	private static final long serialVersionUID = -2219334359197210013L;
	private static final Logger logger = Logger
			.getLogger(IMActionHandlerProxy.class.getName());
	private Object proxyObject;
	private String actionName;
	private List<Method> methods;

	public IMActionHandlerProxy(Object proxyObject, String actionName) {
		this.proxyObject = proxyObject;
		this.actionName = actionName;
		this.methods = new ArrayList<Method>();
		for (Method m : proxyObject.getClass().getDeclaredMethods()) {
			if (m.isAnnotationPresent(IMActionHandler.class)) {
				IMActionHandler handler = m
						.getAnnotation(IMActionHandler.class);
				if(!handler.name().isEmpty() && actionName.equals(handler.name())) {
					this.methods.add(m);
				} else if(handler.name().isEmpty() && actionName.equals(m.getName())) {
					this.methods.add(m);
				}
				if (!m.isAccessible()) {
					m.setAccessible(true);
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		if(methods.size() == 0) {
			throw new IllegalArgumentException("invoke IMActionHandlerProxy Error!! no method: " + actionName);
		}
		for (Method m : methods) {
			try {
				if (m.getParameterTypes().length == 0) {
					m.invoke(proxyObject);
				} else {
					m.invoke(proxyObject, evt);
				}
			} catch (Throwable e) {
				logger.warning("invoke IMActionHandlerProxy Error!!"
						+ e.getMessage());
			}
		}
	}

}
