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
 * File     : IMTaskServiceImpl.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-25
 * License  : Apache License 2.0
 */
package iqq.app.core.service.impl;


import iqq.app.core.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author solosky <solosky772@qq.com>
 */
@Service
public class TaskServiceImpl implements TaskService {
    private final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private ExecutorService threads = Executors.newFixedThreadPool(2);

    @Override
    public void submit(Runnable runnable) {
        threads.submit(runnable);
    }

    @Override
    public void submit(final Object target, final String method, final Object... args) {
        threads.submit(new Runnable() {
            public void run() {
                for (Method m : target.getClass().getDeclaredMethods()) {
                    if (m.getName().equals(method)) {
                        try {
                            if (!m.isAccessible()) {
                                m.setAccessible(true);
                            }
                            m.invoke(target, args);
                            return;
                        } catch (Throwable e) {
                            logger.warn("invoke method error!!", e);
                        }
                    }
                }
            }
        });
    }

}
