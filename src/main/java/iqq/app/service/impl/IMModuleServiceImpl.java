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
 * File     : IMModuleServiceImpl.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-13
 * License  : Apache License 2.0 
 */
package iqq.app.service.impl;

import iqq.app.core.IMException;
import iqq.app.core.IMModule;
import iqq.app.service.IMModuleService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class IMModuleServiceImpl extends AbstractServiceImpl implements IMModuleService {
	private List<IMModule> modules;
	
	public IMModuleServiceImpl(){
		this.modules = new ArrayList<IMModule>();
	}
	
	@Override
	public void installModule(IMModule module) throws IMException {
		module.init(getContext());
		modules.add(module);
	}

	@Override
	public void removeModule(IMModule module) throws IMException {
		modules.remove(module);
		module.destroy();
	}

	@Override
	public List<IMModule> getModuleList() {
		return modules;
	}
}
