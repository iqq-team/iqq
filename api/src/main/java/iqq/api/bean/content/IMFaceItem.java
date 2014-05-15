package iqq.api.bean.content;

/**
 * 聊天表情信息
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-15
 * License  : Apache License 2.0
 */
public class IMFaceItem implements IMContentItem {
    private String key;

    @Override
    public IMContentType getType() {
        return IMContentType.FACE;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
