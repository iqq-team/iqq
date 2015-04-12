package iqq.api.bean;

import iqq.api.bean.content.IMContentItem;

import java.awt.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 聊天消息
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-15
 * License  : Apache License 2.0
 */
public class IMMsg implements Serializable {
    private long id;        // 消息唯一ID
    /**消息发送者*/
    private IMUser sender;
    /**消息内容*/
    private List<IMContentItem> contents;
    /**消息接收或者发送时间*/
    private Date date;
    /**消息状态*/
    private State state;
    /**消息方向*/
    private Direction direction;
    /**消息组别*/
    private Category category;
    /**字体信息*/
    private Font font;
    /**消息拥有者，群还是讨论组*/
    private transient IMEntity owner;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public IMUser getSender() {
        return sender;
    }

    public void setSender(IMUser sender) {
        this.sender = sender;
    }

    public List<IMContentItem> getContents() {
        return contents;
    }

    public void setContents(List<IMContentItem> contents) {
        this.contents = contents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public IMEntity getOwner() {
        return owner;
    }

    public void setOwner(IMEntity owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "IMMsg{" +
                "id=" + id +
                ", sender=" + sender +
                ", contents=" + contents +
                ", date=" + date +
                ", state=" + state +
                ", direction=" + direction +
                ", category=" + category +
                ", font=" + font +
                ", owner=" + owner +
                '}';
    }

    /***
     *
     * 消息状态枚举
     *
     * @author solosky <solosky772@qq.com>
     *
     */
    public enum State{
        /**消息已经发送*/
        SENT,
        /**消息*/
        PENDING,
        /**发送失败*/
        ERROR,
        /**已读*/
        READ,
        /**未读*/
        UNREAD
    }

    /***
     *
     * 消息方向，接收还是发送
     *
     * @author solosky <solosky772@qq.com>
     *
     */
    public enum Direction{
        /**接收到的消息*/
        SEND,
        /**发送的消息*/
        RECV
    }

    /***
     *
     * 消息类型枚举
     *
     * @author solosky <solosky772@qq.com>
     *
     */
    public enum Category{
        /**普通的聊天消息*/
        CHAT,
        /**信息提示消息*/
        INFO,
    }
}
