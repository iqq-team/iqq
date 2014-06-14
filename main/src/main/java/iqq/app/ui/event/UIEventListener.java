package iqq.app.ui.event;

/**
 * IM事件监听器
 */
public interface UIEventListener {
    /**
     * 触发事件回掉
     * @param imEvent
     */
    public void onUIEvent(UIEvent imEvent);
}
