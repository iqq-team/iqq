package iqq.api.event;

import iqq.api.event.IMEvent;

/**
 * Created with IntelliJ IDEA.
 * User: solosky
 * Date: 4/19/14
 * Time: 8:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IMEventBridge {
    public void broadcastEvent(IMEvent imEvent);
    public void fireEvent(IMEvent imEvent);
}
