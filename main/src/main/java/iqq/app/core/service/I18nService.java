package iqq.app.core.service; /*
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

import iqq.app.core.annotation.IMService;

import java.util.Locale;

/**
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 4/13/14
 * License  : Apache License 2.0
 */
@IMService
public interface I18nService {

    /**
     * 根据设置的语言获取国际化资源
     *
     * @param messageKey
     * @return
     */
    public String getMessage(String messageKey);

    /**
     * 根据设置的语言获取国际化资源，支持格式化串
     *
     * @param messageKey
     * @param params
     * @return
     */
    public String getMessage(String messageKey, Object... params);

    /**
     * 根据固定语言获取国际化资源
     *
     * @param messageKey
     * @param locale
     * @return
     */
    public String getMessage(String messageKey, Locale locale);

    /**
     * 根据固定语言获取国际化资源，支持格式化串
     *
     * @param messageKey
     * @param locale
     * @param params
     * @return
     */
    public String getMessage(String messageKey, Locale locale, Object... params);

    /**
     * 获取当前语言
     *
     * @return
     */
    public Locale getCurrentLocale();

    /**
     * 获取国际化资源文件目录
     *
     * @return
     */
    public String getI18nDirectory();
}
