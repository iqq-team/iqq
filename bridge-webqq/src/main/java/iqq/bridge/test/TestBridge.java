package iqq.bridge.test;

import iqq.api.annotation.IMEventHandler;
import iqq.api.bean.IMAccount;
import iqq.api.bean.IMBuddy;
import iqq.api.bean.IMBuddyCategory;
import iqq.api.bridge.IMApp;
import iqq.api.bridge.IMBridge;
import iqq.api.event.IMEvent;
import iqq.api.event.IMEventDispatcher;
import iqq.api.event.IMEventType;
import iqq.api.event.args.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tony on 4/6/15.
 */
public class TestBridge extends IMEventDispatcher implements IMBridge {
    private final Logger logger = LoggerFactory.getLogger(TestBridge.class);
    private IMApp mIMApp;

    @Override
    public void setApp(IMApp imApp) {
        mIMApp = imApp;
    }

    @IMEventHandler(IMEventType.LOGIN_REQUEST)
    private void processLoginRequestEvent(IMEvent imEvent) {
        LoginRequest loginRequest = (LoginRequest) imEvent.getTarget();

        IMAccount account = new IMAccount();
        account.setLoginName(loginRequest.getUsername());
        account.setPassword(loginRequest.getPassword());

        broadcastIMEvent(new IMEvent(IMEventType.LOGIN_SUCCESS, account));
        broadcastIMEvent(IMEventType.CLIENT_ONLINE, account);

        doGetBuddyList();
    }

    private void doGetBuddyList() {
        List<IMBuddyCategory> imCategories = new LinkedList<IMBuddyCategory>();
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
        IMEvent imEvent = new IMEvent(IMEventType.BUDDY_LIST_UPDATE);
        imEvent.setTarget(imCategories);
        broadcastIMEvent(imEvent);
    }

    protected void broadcastIMEvent(IMEvent event) {
        mIMApp.onIMEvent(event);
    }

    protected void broadcastIMEvent(IMEventType type, Object target) {
        mIMApp.onIMEvent(new IMEvent(type, target));
    }
}
