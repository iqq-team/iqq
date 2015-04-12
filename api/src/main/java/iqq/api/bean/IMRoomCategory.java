package iqq.api.bean;

import java.util.LinkedList;
import java.util.List;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-9
 * License  : Apache License 2.0
 */
public class IMRoomCategory extends IMCategory {
    private List<IMRoom> roomList = new LinkedList<>();

    public List<IMRoom> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<IMRoom> roomList) {
        this.roomList = roomList;
    }
}
