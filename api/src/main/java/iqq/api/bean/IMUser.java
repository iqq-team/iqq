package iqq.api.bean;

/**
 * Created with IntelliJ IDEA.
 * User: solosky
 * Date: 4/19/14
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class IMUser extends IMEntity {
    private IMStatus status;

    public IMStatus getStatus() {
        return status;
    }

    public void setStatus(IMStatus status) {
        this.status = status;
    }
}
