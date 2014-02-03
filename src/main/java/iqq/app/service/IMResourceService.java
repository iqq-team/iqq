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
 * Package  : iqq.app.service
 * File     : ResourceService.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-13
 * License  : Apache License 2.0 
 */
package iqq.app.service;

import iqq.app.core.IMService;
import iqq.im.bean.QQAccount;

import java.io.File;
import java.net.URL;

import org.dom4j.Document;

/**
 *
 * 提供资源的读取
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public interface IMResourceService extends IMService{
	public URL getFileURL(String url_name);
	public String getFilePath(String filePath);
	public String getFileDir(String path);
	public URL getClassLoaderResouce(String url_name);
	public URL getResource(String url_name);
	public String getResourceDir();
	public Document readXml(File xmlFile);
	public void writeXml(Document doc, File xmlFile);
	public File getFile(String path);
	public File getUserFile(String path);
	public void setActiveUser(QQAccount account);
}
