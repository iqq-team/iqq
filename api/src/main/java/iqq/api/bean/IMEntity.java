package iqq.api.bean;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: solosky
 * Date: 4/19/14
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class IMEntity implements Serializable{
    private long id;
    private String nick;
    private String sign;
    private BufferedImage avatar;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public BufferedImage getAvatar() {
        return avatar;
    }

    public void setAvatar(BufferedImage avatar) {
        this.avatar = avatar;
    }
}
