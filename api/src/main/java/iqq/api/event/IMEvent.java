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
 * Project  : IQQ_V2
 * Package  : iqq.action
 * File     : IMEvent.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-3-10
 * License  : Apache License 2.0 
 */
package iqq.api.event;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * IM事件，作为所有操作的数据的载体在多个模块中传递消息
 *
 * @author solosky <solosky772@qq.com>
 *
 */
public class IMEvent implements Serializable{
    private static final long serialVersionUID = 1L;

    /**事件ID计数器*/
    private static final AtomicInteger nextEventId = new AtomicInteger(0);;

    /**事件ID，定义在IMEventID*/
    private IMEventType eventType;
    /**内部事件ID，递增的*/
    private long eventId;
    /**关联事件，一个事件可能通常和其他事件相关联，比如 加好友结果事件必然和加好友请求事件联系在一起*/
    private IMEvent relatedEvent;
    /**是否停止冒泡，一个事件可能有多个事件监听器监听,分发器会按注册的先后顺序逐个的调用，如果设置为取消冒泡，后面的事件监听器不再调用*/
    private boolean cancelBubble;
    /**事件的对象，通常可以作为事件附加数据*/
    private Object target;
    /**事件数据MAP，也可以直接把事件数据以KEY的形式放入MAP中，处理事件时按KEY读取出来，编程时要确保KEY一致*/
    private Map<String, Object> data;
    /**事件的结果，如果是Query类型的事件，需要把结果放入这个对象，或者放入dataMAP中*/
    private Object result;
    /**如果是Query类型的事件，标志了这个事件是否被处理*/
    private boolean queryHandled;

    /***
     * 通过一个事件类型和事件对象来构造事件
     * @param eventType
     * @param target
     */
    public IMEvent(IMEventType eventType, Object target) {
        this(eventType, target, null);
    }

    public IMEvent(IMEventType eventType) {
        this(eventType, null, null);
    }

    /**
     *  通过一个事件类型来构造
     * @param eventType
     */
    public IMEvent(IMEventType eventType, Object target, IMEvent related){
        this.eventType = eventType;
        this.target = target;
        this.data = new HashMap<String, Object>();
        this.cancelBubble = false;
        this.eventId = nextEventId.incrementAndGet();
        this.relatedEvent = related;
    }

    /**
     * @return the relatedEvent
     */
    public IMEvent getRelatedEvent() {
        return relatedEvent;
    }

    /**
     * @param relatedEvent the relatedEvent to set
     */
    public void setRelatedEvent(IMEvent relatedEvent) {
        this.relatedEvent = relatedEvent;
    }

    /**
     * @return the cancelBubble
     */
    public boolean isCancelBubble() {
        return cancelBubble;
    }

    /**
     * @param cancelBubble the cancelBubble to set
     */
    public void setCancelBubble(boolean cancelBubble) {
        this.cancelBubble = cancelBubble;
    }

    /**
     * @return the target
     */
    public Object getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * @return the eventType
     */
    public IMEventType getType() {
        return eventType;
    }

    /**
     * @return the result
     */
    public Object getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * @return the queryHandled
     */
    public boolean isQueryHandled() {
        return queryHandled;
    }

    /**
     * @param queryHandled the queryHandled to set
     */
    public void setQueryHandled(boolean queryHandled) {
        this.queryHandled = queryHandled;
    }

    /**
     * @return the eventId
     */
    public long getId() {
        return eventId;
    }


    public void putData(String key, Object value){
        this.data.put(key, value);
    }

    public boolean hasData(String key){
        return this.data.containsKey(key);
    }

    public <T> T getData(String key){
        return (T) this.data.get(key);
    }

    public int getInt(String key){
        return getDataEx(key);
    }

    public long getLong(String key){
        return getDataEx(key);
    }

    public String getString(String key){
        return getDataEx(key);
    }

    private <T> T getDataEx(String key){
        if(hasData(key)){
            return (T) getData(key);
        }else{
            throw new IllegalArgumentException("key not found:" + key);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "IMEvent [eventType=" + eventType + ", eventId=" + eventId
                + ", cancelBubble=" + cancelBubble + ", queryHandled="
                + queryHandled + "]";
    }
}
