package iqq.app.core.context;
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

import org.springframework.context.ApplicationContext;

/**
 * 客户端上下文，所有的创建的对象实例都由上下文管理，也可以从这个类得到任意对象
 * 通常来说，不需要直接使用这个类，因为所有的对象都是通过依赖注入的方式自动注入的
 * 只有在一些特定的场景，比如某些对象不是由容器管理的情况下才需要使用这个类获得容器中的对象或者创建对象
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 4/13/14
 * License  : Apache License 2.0
 */
public class IMContext {
//    /**ioc容器*/
//    private Ioc ioc;

    private ApplicationContext applicationContext;

    private static IMContext singleton = new IMContext();

    public static IMContext me() {
        return singleton;
    }

    public static void init(ApplicationContext ctx) {
        me().applicationContext = ctx;
    }

    private IMContext() {
//        applicationContext = new ClassPathXmlApplicationContext("classpath:spring-config.xml");
//        applicationContext = new AnnotationConfigApplicationContext("iqq.app");
    }

    public static <T> T getBean(Class<T> clazz) {
        return singleton.applicationContext.getBean(clazz);
    }
}
