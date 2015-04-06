package iqq.bridge;

import iqq.api.bridge.IMBridge;
import iqq.bridge.test.TestBridge;

/**
 * Created by Tony on 4/6/15.
 */
public class IMBridgeFactory {

    /**
     * TestBridge
     * WebQQBridge
     */
    private static Class<? extends IMBridge> mIMBridge = TestBridge.class;

    public static IMBridge getIMBridge() {
        try {
            return mIMBridge.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
