package iqq.api.bean;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-8
 * License  : Apache License 2.0
 */
public class IMBuddyCategory extends IMCategory implements Serializable {
    private List<IMBuddy> buddyList = new LinkedList<IMBuddy>();

    public List<IMBuddy> getBuddyList() {
        return buddyList;
    }

    public void setBuddyList(List<IMBuddy> buddyList) {
        this.buddyList = buddyList;
    }
}
