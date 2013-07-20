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
 * Package  : iqq.app.service.impl
 * File     : IMEventServiceImpl.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-13
 * License  : Apache License 2.0 
 */
package iqq.app.service.impl;

import iqq.app.core.IMErrorCode;
import iqq.app.core.IMException;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventListener;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 事件服务实现
 * TODO ..检查调用线程，如果不是在Swing线程就加入到事件线程中调用 
 * 
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class IMEventServiceImpl extends AbstractServiceImpl implements IMEventService {
	private static final Logger LOG = Logger.getLogger(IMEventServiceImpl.class);

	/**Type => list<listener> **/
	private Map<IMEventType, List<IMEventListener>> lookup;
	public IMEventServiceImpl(){
		lookup = new HashMap<IMEventType, List<IMEventListener>>();
	}
	@Override
	public void register(IMEventType[] intrestedEvents, IMEventListener listener) {
		for(IMEventType type: intrestedEvents){
			List<IMEventListener> list = lookup.get(type);
			if(list == null){
				list = new ArrayList<IMEventListener>();
				lookup.put(type, list);
			}
			list.add(listener);
		}
		
	}

	@Override
	public void unregister(IMEventType[] intrestedEvents,
			IMEventListener listener) {
		for(IMEventType type: intrestedEvents){
			List<IMEventListener> list = lookup.get(type);
			if(list != null){
				list.remove(listener);
			}
		}
	}

	@Override
	public void unregister(IMEventListener listener) {
		unregister(lookup.keySet().toArray(new IMEventType[0]), listener);
	}

	@Override
	public void broadcast(final IMEvent event){
		if(EventQueue.isDispatchThread()){
			doBroadcast(event);
		}else{
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					doBroadcast(event);
				}
			});
		}
	}

	@Override
	public Object query(IMEvent event) throws IMException {
		List<IMEventListener> list = lookup.get(event.getType());
		LOG.debug("Query IMEvent: " + event.getType()+", listeners: " + (list != null ? list.size(): 0));
		if(list != null && list.size() > 0){
			for(IMEventListener listener: list){
				listener.onIMEvent(event);
				if(event.isQueryHandled()){
					return event.getResult();
				}
			}
		}
		throw new IMException(IMErrorCode.QUERY_UNHANDLED);
	}
	
	private void doBroadcast(IMEvent event){
		List<IMEventListener> list = lookup.get(event.getType());
		LOG.debug("Broadcast IMEvent: " + event.getType()+", listeners: " + (list != null ? list.size(): 0));
		if(list != null && list.size() > 0){
			list = new ArrayList<IMEventListener>(list);
			for(IMEventListener listener: list){
				try {
					listener.onIMEvent(event);
					if(event.isCancelBubble()){
						return;
					}
				} catch (Throwable e) {
					LOG.warn("Broadcast IMEvent Error!", e);
				}
			}
		}
	}

}
