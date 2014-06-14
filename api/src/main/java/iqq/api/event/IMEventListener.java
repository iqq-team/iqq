package iqq.api.event;

/**
 * IM事件监听器
 */
public interface IMEventListener {
    /**
     * 触发事件回掉
     * @param imEvent
     */
    public void onIMEvent(IMEvent imEvent);
}
