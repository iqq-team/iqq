package iqq.app.util;

import iqq.api.bean.content.IMContentItem;
import iqq.api.bean.content.IMContentType;
import iqq.api.bean.content.IMTextItem;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.impl.ResourceServiceImpl;
import iqq.app.ui.frame.panel.chat.rich.UIRichItem;
import iqq.app.ui.frame.panel.chat.rich.UITextItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static iqq.api.bean.content.IMContentType.TEXT;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-11
 * License  : Apache License 2.0
 */
public class UIUtils {
    public static class Bean {

        public static ImageIcon byteToIcon(byte[] imageData) {
            return byteToIcon(imageData, 40, 40);
        }

        public static ImageIcon byteToIcon(byte[] imageData, int w, int h) {
            Image image = Toolkit.getDefaultToolkit().createImage(imageData);
            return new ImageIcon(image.getScaledInstance(w, h, 100));
        }

        public static ImageIcon getDefaultAvatar() {
            return IMContext.me().getIoc()
                    .get(ResourceServiceImpl.class).getIcon("icons/default/qq_icon.png");
        }

        public static List<UIRichItem> toRichItem(List<IMContentItem> items) {
            List<UIRichItem> contents = new ArrayList<UIRichItem>();
            for(IMContentItem item : items) {
                switch (item.getType()) {
                    case TEXT:
                        contents.add(new UITextItem(((IMTextItem) item).getText()));
                        break;
                    case  FACE:
                        break;
                    case PIC:
                        break;
                }
            }
            return contents;
        }
    }
}
