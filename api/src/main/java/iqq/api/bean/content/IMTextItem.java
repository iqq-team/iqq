package iqq.api.bean.content;

/**
 * 聊天文本信息
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-15
 * License  : Apache License 2.0
 */
public class IMTextItem implements IMContentItem {
    private String text;

    public IMTextItem(String text) {
        this.text = text;
    }

    @Override
    public IMContentType getType() {
        return IMContentType.TEXT;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
