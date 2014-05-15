package iqq.api.bean.content;

import java.io.File;

/**
 * 聊天图片信息
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-15
 * License  : Apache License 2.0
 */
public class IMPictureItem implements IMContentItem {
    private File file;

    @Override
    public IMContentType getType() {
        return IMContentType.PIC;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
