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
 * Project  : WebQQCore
 * Package  : iqq.im
 * File     : QQException.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2012-7-31
 * License  : Apache License 2.0 
 */
package iqq.app.core;

/**
 * 
 * IM异常，所有的接口都需要声明抛出IM异常
 * 
 * @author solosky <solosky772@qq.com>
 * 
 */
public class IMException extends Exception {
	private static final long serialVersionUID = 1L;
	private IMErrorCode errorCode;

	public IMException(IMErrorCode errorCode) {
		super(errorCode.toString());
		this.errorCode = errorCode;
	}
	
	public IMException(IMErrorCode errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
	}

	public IMException(IMErrorCode errorCode, Throwable e) {
		super(errorCode.toString(), e);
		this.errorCode = errorCode;
	}

	public IMErrorCode getError() {
		return errorCode;
	}
}
