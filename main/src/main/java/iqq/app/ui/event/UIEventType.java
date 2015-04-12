package iqq.app.ui.event;
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
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 5/3/14
 * License  : Apache License 2.0
 */
public enum UIEventType {
    /****启动和退出部分******/
    APP_EXIT_READY,	//准备退出程序
    APP_EXIT,		//退出程序
    CLIENT_OFFLINE,	//客户端离线
    CLIENT_ONLINE,	//客户端在线
    /********登录部分*******/
    LOGIN_READY,	//准备登录
    LOGIN_REQUEST,	//登录请求
    LOGIN_PROGRESS, //登录进度
    LOGIN_ERROR, 	//登录失败
    LOGIN_SUCCESS,	//登录成功
    LOGIN_CANCEL_REQUEST,	//取消登录请求
    LOGIN_CANCELED,	//用户取消了登录
    RELOGIN_REQEUST,//重新登录
    RELOGIN_SUCCESS,//重新登录成功
    RELOGIN_ERROR,	//重新登录失败

    /********改变状态部分*******/
    CHANGE_STATUS_REQUEST,	//状态请求
    CHANGE_STATUS_SUCCESS,	//改变状态成功

    /********验证码部分*******/
    IMAGE_VERIFY_NEED, 		//需要验证
    SUBMIT_VERIFY_REQUEST, //附加验证码继续登录
    VERIFY_CANCEL,			//用户取消验证码
    FRESH_VERIFY_REQUEST,	//刷新验证码
    FRESH_VERIFY_SUCCESS,	//刷新验证码成功
    FRESH_VERIFY_FAILED,	//刷新验证码失败


    /*******退出登录部分****/
    LOGOUT_REQUEST,	//请求退出登录
    LOGOUT_SUCCESS,	//退出登录成功

    /*******数据更新通知*****/
    BUDDY_LIST_UPDATE,	//好友列表有更新
    ONLINE_LIST_UPDATE, //在线好友列表更新
    GROUP_LIST_UPDATE,	//群列表更新
    DISCUZ_LIST_UPDATE, // 讨论组列表更新

    /*******好友（普通用户,自己）信息更新******/
    USER_FACE_UPDATE,	//好友头像更新
    BUDDY_UIN_UPDATE,	//好友的QQ号更新
    BUDDY_SIGN_UPDATE,//好友签名更新（貌似改了签名之后webqq不会更新）
    GROUP_FACE_UPDATE, //群头像更新
    USER_FACE_REQUEST,	//从网络下载头像
    USER_LEVEL_REQUEST,	//用户等级请求
    USER_LEVEL_UPDATE,	//用户等级更新
    USER_INFO_REQUEST,	//用户信息请求
    USER_INFO_UPDATE,	//用户信息更新
    USER_QQ_UPDATE,		//用户QQ号更新
    USER_QQ_REQUEST,	//请求获取QQ号
    USER_SIGN_UPDATE,	//用户签名更新
    USER_STATUS_UPDATE, //好友状态改变更新
    STRANGER_INFO_REQUEST,//陌生人信息请求
    STRANGER_INFO_UPDATE, //陌生人信息更新

    /********群部分**************/
    GROUP_INFO_REQUEST,	//群信息请求
    GROUP_INFO_UPDATE,	//群信息更新
    GROUP_GID_REQUEST, 	//查询群真实群号
    GROUP_GID_UPDATE,	//真实群号更新
    GROUP_MEMBER_UPDATE,//群成员更新

    GROUP_MSG_FILTER_REQUEST,	//群禁消息请求
    GROUP_MSG_FILTER_SUCCESS,	//群禁消息请求成功
    GROUP_MSG_FILTER_ERROR,		//群禁消息请求失败


    /*******讨论组**************/
    DISCUZ_INFO_REQUEST,	//讨论组信息请求
    DISCUZ_INFO_UPDATE,		//讨论组信息更新
    DISCUZ_MEMBER_UPDATE,	// 成员更新

    /********最近联系**********/
    RECENT_LIST_UPDATE,		//最近联系人更新
    RECENT_ACCOUNT_FIND,	//登录时查询最近登录过的账号，显示在账户选择框中
    RECENT_ACCOUNT_UPDATE,	//通知查询到最近登录的账号
    RECENT_ACCOUNT_DELETE,	//最近联系人删除


    /********缓存部分**************/
    USER_CACHE_FIND,	//从缓存中查找用户
    USER_CACHE_BATCH_FIND, //从缓存中批量查找用户
    USER_CACHE_UPDATE,	//在缓存中找到数据更新通知
    USER_CACHE_BATCH_UPDATE,//缓存查找批量更新
    USER_CACHE_MISS,	//缓存中没有找到

    /*****用户自己数据更新**********/
    SELF_INFO_UPDATE,	//个人基本数据更新
    SELF_FACE_UPDATE,	//个人头像更新
    SELF_SIGN_UPDATE,	//个人签名更新

    /********聊天消息部分***********/
    SEND_MSG_REQUEST,	//请求发送消息
    SEND_MSG_SUCCESS,	//发送消息成功
    SEND_MSG_ERROR,		//发送消息失败
    SEND_CHAT_MSG,		//发送消息到消息管理器
    RECV_CHAT_MSG,		//请求保存消息到历史记录中
    SHOW_INFO_MSG,		//保存一些信息类的消息
    UPDATE_UIMSG,		//更新一条消息数据到历史记录中
    RECV_RAW_MSG,		//接受到原始的消息
    MSG_HISTORY_UPDATE,	//消息历史记录更新
    MSG_HISTORY_FIND,	//查找历史消息记录
    MSG_SHOWED,			//消息已经显示
    SEND_SHAKE_REQUEST,	//发送震动消息
    SHAKE_WINDOW,		//收到震动窗口提示
    USER_INPUTTING,		//用户正在输入
    SEND_INPUT_REQUEST,	//发送正在输入状态

    GET_SESSION_MSG_REQUEST,	//得到临时会话信道
    GET_SESSION_MSG_PROGRESS,
    GET_SESSION_MSG_ERROR,
    GET_SESSION_MSG_SUCCESS,

    FLASH_USER_START,	//开始闪烁用户
    FLASH_USER_STOP,	//停止闪烁用户

    GET_OFFPIC_REQUEST,	 //请求获取聊天图片
    GET_OFFPIC_PROGRESS, //上传或者下载图片进度
    GET_OFFPIC_ERROR,	//获取聊天图片失败
    GET_OFFPIC_SUCCESS, //获取聊天图片成功

    UPLOAD_OFFPIC_REQUEST,
    UPLOAD_OFFPIC_PROGRESS,
    UPLOAD_OFFPIC_ERROR,
    UPLOAD_OFFPIC_SUCCESS,

    GET_USERPIC_REQUEST,
    GET_USERPIC_PROGRESS,
    GET_USERPIC_ERROR,
    GET_USERPIC_SUCCESS,

    GET_GROUPPIC_REQUEST,
    GET_GROUPPIC_PROGRESS,
    GET_GROUPPIC_ERROR,
    GET_GROUPPIC_SUCCESS,

    /*********上传聊天图片部分*****/
    UPLOAD_CFACE_REQUEST, //请求上传图片
    UPLOAD_CFACE_SUCCESS,//请求上传图片失败
    UPLOAD_CFACE_ERROR,	//上传图片失败
    UPLOAD_CFACE_PROGRESS,//上传图片进度通知


    /********查询数据部分*******/
    QUERY_BUDDY_LIST,		//查询好友列表
    QUERY_GROUP_LIST,		//查询群列表
    QUERY_FRIEND_BY_UIN,	//通过UIN查找好友
    QUERY_ACCOUNT,			//查询登录帐号
    QUERY_SELF_IS_ONLINE,	//查询客户端是否在线


    /********窗口显示*******/
    SHOW_MAIN_FRAME, 		//显示主窗口
    SHOW_CHAT,				//显示聊天对话窗口
    SHOW_MINI_MSG_BOX,		//显示迷你消息窗口
    SHOW_PIC_PREVIEW,		//显示图片预览窗口
    SHOW_MEMBERS_WINDOW,	//显示群或者讨论组窗口
    SHOW_PROXY_WINDOW,		//显示代理窗口
    SHOW_MSG_HISTORY_WINDOW,//显示历史消息窗口
    SHOW_HOVER_INFOCARD_WINDOW,	//显示用户或者群信息
    HIDE_HOVER_INFOCARD_WINDOW,	//隐藏
    WINDOW_MAIN_LOST_FOCUS,		//主窗口失去焦点
    WINDOW_MAIN_GAINED_FOCUS,	//主窗口得到焦点
    SHOW_SETTING_WINDOW,		// 显示设置窗口
    SHOW_SEARCHBUDDY_WINDOW //显示查找好友信息窗口
}
