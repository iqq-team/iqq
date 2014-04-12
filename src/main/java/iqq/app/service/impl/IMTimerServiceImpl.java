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
 * File     : IMTimerServiceImpl.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-10
 * License  : Apache License 2.0 
 */
package iqq.app.service.impl;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.service.IMTimerService;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 默认定时器实现
 * 
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class IMTimerServiceImpl implements IMTimerService{
	private static Logger LOG =LoggerFactory.getLogger(IMTimerServiceImpl.class);
	private Timer timer;
	private Map<Runnable, TimerAdapter> map;
	@Override
	public void init(IMContext context) throws IMException {
		timer = new Timer();
		map = new HashMap<Runnable, TimerAdapter>();
	}

	@Override
	public void destroy() throws IMException {
		timer.cancel();
	}

	@Override
	public void setInterval(Runnable runnable, long interval) {
		TimerAdapter adapter = new TimerAdapter(runnable);
		map.put(runnable, adapter);
		timer.schedule(adapter, interval, interval);
	}

	@Override
	public void setTimeout(Runnable runnable, long delay) {
		TimerAdapter adapter = new TimerAdapter(runnable);
		map.put(runnable, adapter);
		timer.schedule(adapter, delay);
	}

	@Override
	public void killTimer(Runnable runnable) {
		if(map.containsKey(runnable)){
			TimerAdapter adapter = map.get(runnable);
			adapter.cancel();
			timer.purge();
		}
	}
	
	private static class TimerAdapter extends TimerTask{
		private Runnable runnable;
		public TimerAdapter(Runnable runnable){
			this.runnable = runnable;
		}
		@Override
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						runnable.run();
					} catch (Throwable e) {
						LOG.warn("TimerAdapter run timer error!", e);
					}
				}
			});
		}
		
	}

}
