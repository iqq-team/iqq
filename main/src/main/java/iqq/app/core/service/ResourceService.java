package iqq.app.core.service;
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

import javax.swing.*;
import java.io.File;

/**
 * 资源文件获取服务
 *
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 4/13/14
 * License  : Apache License 2.0
 */
public interface ResourceService {
    /**
     * 获取绝对资源目录
     *
     * @return
     */
    public String getResourcePath();

    /**
     * 获取资源文件
     *
     * @param filename
     * @return
     */
    public File getFile(String filename);


    /**
     * 获取资源文件图片
     *
     * @param filename
     * @return
     */
    public ImageIcon getIcon(String filename);


    /**
     * 获取资源文件图片，调整为固定大小
     *
     * @param filename
     * @param width
     * @param height
     * @return
     */
    public ImageIcon getIcon(String filename, int width, int height);


    /**
     * 获取用户目录
     *
     * @return
     */
    public String getUserPath();

    /**
     * 获取用户目录下面的文件
     *
     * @param filename
     * @return
     */
    public File getUserFile(String filename);


    /**
     * 获取资源文件图片
     *
     * @param filename
     * @return
     */
    public ImageIcon getUserIcon(String filename);


    /**
     * 获取资源文件图片，调整为固定大小
     *
     * @param filename
     * @param width
     * @param height
     * @return
     */
    public ImageIcon getUserIcon(String filename, int width, int height);


}
