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
 * File     : IMResourceServiceImpl.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-13
 * License  : Apache License 2.0 
 */
package iqq.app.service.impl;

import iqq.app.core.IMConstants;
import iqq.app.service.IMResourceService;
import iqq.im.bean.QQAccount;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 
 * 
 * @author solosky <solosky772@qq.com>
 * 
 */
public class IMResourceServiceImpl extends AbstractServiceImpl implements
		IMResourceService {
	private QQAccount account;
	
	public URL getFileURL(String path) {
		// 先从当前目录取（打成jar包的情况下）
		URL url = Class.class.getClass().getResource(path);
		// 如果没取到，则从根目录取（打成jar包的情况下）
		if (url == null)
			url = Class.class.getClass().getResource("/" + path);
		// 从当前线程的地址取
		if (null == url)
			url = Thread.currentThread().getContextClassLoader()
					.getResource(path);
		// 以上代码都是针对swing的。下面代码针对eclipse中情况
		if (url == null) {
			if (path.startsWith("/") || path.startsWith(File.separator)) {
				path = IMConstants.APP_RESOURCES_DIR + path; // 加上资源目录
			} else {
				path = IMConstants.APP_RESOURCES_DIR + "/" + path; // 加上资源目录
			}
			try {
				String rootPath = System.getProperty("user.dir");
				// 针对在eclipse中，用Java Application运行的。
				File webFile = new File(rootPath + "/" + path);
				if (webFile.exists()) {
					url = webFile.toURI().toURL();
				}
				// 实在不行了，死马当活马医吧
				if (null == url)
					webFile = new File(new File("").getAbsoluteFile() + "/"
							+ path);
				if (webFile.exists()) {
					url = webFile.toURI().toURL();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		//if (null == url)
		//	throw new NullPointerException("没有找到资源: " + path);
		return url;
	}

	public String getFilePath(String path) {
		return getFileURL(path).getPath();
	}

	public String getFileDir(String path) {
		int n = path.lastIndexOf('/');
		if (n == -1) {
			return path;
		} else {
			if (n < path.length()) {
				return path.substring(0, n + 1);
			} else {
				return "";
			}
		}
	}

	/**
	 * @param url_name
	 * @return url
	 * */
	public URL getClassLoaderResouce(String url_name) {
		return ClassLoader.getSystemResource(url_name);
	}

	/**
	 * @param url_name
	 * @return url
	 * */
	public URL getResource(String url_name) {
		return getFileURL(url_name);
	}

	@Override
	public String getResourceDir() {
		return System.getProperty("user.dir") + File.separator + IMConstants.APP_RESOURCES_DIR;
	}
	
	@Override
	public Document readXml(File xmlFile) {
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(xmlFile);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return document;
	}

	@Override
	public void writeXml(Document document, File xmlFile) {
		try {
			OutputFormat outputFormat = OutputFormat.createPrettyPrint();// 设置XML文档输出格式
			outputFormat.setEncoding("UTF-8");// 设置XML文档的编码类型
			outputFormat.setIndent(true);// 设置是否缩进
			outputFormat.setIndent("	");// 以TAB方式实现缩进
			outputFormat.setNewlines(true);// 设置是否换行
			XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(xmlFile), outputFormat);
			xmlWriter.write(document);
			xmlWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public File getFile(String path) {
		return new File(path);
	}


	@Override
	public File getUserFile(String path) {
		return new File("user/" + this.account.getUin() +"/" + path);
	}

	@Override
	public void setActiveUser(QQAccount account) {
		this.account = account;
	}

}
