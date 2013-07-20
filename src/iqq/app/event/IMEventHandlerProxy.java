 /*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 /**
 * Project  : IQQ_V2.1
 * Package  : iqq.app.event
 * File     : IMEventHandlerProxy.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-16
 * License  : Apache License 2.0 
 */
package iqq.app.event;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.service.IMEventService;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 *
 * 使用这个类简化事件的注册，分发
 * 只需在被代理的类使用@IMEventHandler注解需要处理的事件类型即可
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class IMEventHandlerProxy implements IMEventListener{
	private static final Logger LOG = Logger.getLogger(IMEventHandlerProxy.class);
	private static Map<Object, IMEventHandlerProxy> REGISTRY = new HashMap<Object, IMEventHandlerProxy>();
	private Object proxyObject;
	private Map<IMEventType, Method> methodMap;
	private IMContext imContext;
	private IMEventHandlerProxy(Object proxyObject, IMContext imContext){
		this.proxyObject = proxyObject;
		this.imContext = imContext;
		this.methodMap = new HashMap<IMEventType, Method>();
		 for (Method m : proxyObject.getClass().getDeclaredMethods()) {
			 if(m.isAnnotationPresent(IMEventHandler.class)){
				 IMEventHandler handler = m.getAnnotation(IMEventHandler.class);
				 this.methodMap.put(handler.value(), m);
				 if(!m.isAccessible()){
					 m.setAccessible(true);
				 }
			 }
		 }
	}
	@Override
	public void onIMEvent(IMEvent imEvent) throws IMException {
		Method m =  methodMap.get(imEvent.getType());
		if(m != null){
			try {
				m.invoke(proxyObject, imEvent);
			} catch (Throwable e) {
				LOG.warn("invoke IMEventHandler Error!! proxyObject=" + proxyObject +", method=" + m.getName(), e);
			}
		}else{
			LOG.warn("IMEventHandler Not Found!! imEvent = " + imEvent);
		}
	}
	public void register(){
		IMEventService eventHub = imContext.getSerivce(IMService.Type.EVENT);
		 eventHub.register(this.methodMap.keySet().toArray(new IMEventType[0]), this);
	}
	
	public void unregister(){
		 IMEventService eventHub = imContext.getSerivce(IMService.Type.EVENT);
		 eventHub.unregister(this);
	}
	
	public static void register(Object proxyObject, IMContext imContext) {
		if(!REGISTRY.containsKey(proxyObject)){
			IMEventHandlerProxy proxy = new IMEventHandlerProxy(proxyObject, imContext);
			proxy.register();
			REGISTRY.put(proxyObject, proxy);
		}
	}
	
	public static void unregister(Object proxyObject){
		IMEventHandlerProxy proxy = REGISTRY.get(proxyObject);
		if(proxy != null){
			proxy.unregister();
		}else{
			LOG.warn("Not found proxyObject from local registry!!!" + proxyObject);
		}
	}
}
