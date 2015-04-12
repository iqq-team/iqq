package iqq.bridge.test;

import iqq.api.annotation.IMEventHandler;
import iqq.api.bean.*;
import iqq.api.bean.content.IMContentItem;
import iqq.api.bean.content.IMTextItem;
import iqq.api.bridge.IMApp;
import iqq.api.bridge.IMBridge;
import iqq.api.event.IMEvent;
import iqq.api.event.IMEventDispatcher;
import iqq.api.event.IMEventType;
import iqq.api.event.args.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tony on 4/6/15.
 */
public class TestBridge extends IMEventDispatcher implements IMBridge {
    private final Logger logger = LoggerFactory.getLogger(TestBridge.class);
    private IMApp mIMApp;
    private IMAccount account;

    @Override
    public void setApp(IMApp imApp) {
        mIMApp = imApp;
    }

    @IMEventHandler(IMEventType.SEND_MSG_REQUEST)
    private void processSendMsgRequest(IMEvent imEvent) {
        logger.debug("processSendMsgRequest: " + imEvent.getTarget());

        IMBuddy buddy = new IMBuddy();
        buddy.setId(123);
        buddy.setNick("Test");

        IMMsg msg = new IMMsg();
        msg.setSender(buddy);
        msg.setDate(new Date());
        msg.setState(IMMsg.State.READ);
        msg.setOwner(account);

        List<IMContentItem> contents = new ArrayList<>();
        IMTextItem text = new IMTextItem("test content...");
        contents.add(text);
        msg.setContents(contents);

        broadcastIMEvent(IMEventType.RECV_RAW_MSG, msg);
    }

    @IMEventHandler(IMEventType.LOGIN_REQUEST)
    private void processLoginRequestEvent(IMEvent imEvent) {
        LoginRequest loginRequest = (LoginRequest) imEvent.getTarget();

        account = new IMAccount();
        account.setId(10);
        account.setLoginName(loginRequest.getUsername());
        account.setPassword(loginRequest.getPassword());

        broadcastIMEvent(IMEventType.LOGIN_SUCCESS, account);
        broadcastIMEvent(IMEventType.CLIENT_ONLINE, account);

        doGetBuddyList();
        doGetGroupList();
        doGetRecentList();
    }

    private void doGetBuddyList() {
        List<IMBuddyCategory> imCategories = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            IMBuddyCategory buddyCategory = new IMBuddyCategory();
            buddyCategory.setName("Category " + i);
            for (int j = 0; j < 10; j++) {
                IMBuddy buddy = new IMBuddy();
                buddy.setNick("Tony " + j);
                buddy.setSign("Hello World! " + j);
                buddyCategory.getBuddyList().add(buddy);
            }
            imCategories.add(buddyCategory);
        }

        broadcastIMEvent(IMEventType.BUDDY_LIST_UPDATE, imCategories);
    }

    private void doGetGroupList() {
        List<IMRoomCategory> roomCategories = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            IMRoomCategory roomCategory = new IMRoomCategory();
            roomCategory.setName("Category " + i);
            for (int j = 0; j < 10; j++) {
                IMRoom room = new IMRoom();
                room.setNick("Hello World! " + j);
                roomCategory.getRoomList().add(room);
            }
            roomCategories.add(roomCategory);
        }

        broadcastIMEvent(IMEventType.GROUP_LIST_UPDATE, roomCategories);
    }

    private void doGetRecentList() {
        List<IMBuddy> buddies = new LinkedList<>();
        for (int j = 0; j < 10; j++) {
            IMBuddy buddy = new IMBuddy();
            buddy.setNick("Tony " + j);
            buddy.setSign("Hello World! " + j);
            buddies.add(buddy);
        }

        broadcastIMEvent(IMEventType.RECENT_LIST_UPDATE, buddies);
    }


    protected void broadcastIMEvent(IMEvent event) {
        mIMApp.onIMEvent(event);
    }

    protected void broadcastIMEvent(IMEventType type, Object target) {
        mIMApp.onIMEvent(new IMEvent(type, target));
    }
}
