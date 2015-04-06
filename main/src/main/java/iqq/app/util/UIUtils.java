package iqq.app.util;

import iqq.api.bean.content.IMContentItem;
import iqq.api.bean.content.IMTextItem;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.impl.ResourceServiceImpl;
import iqq.app.ui.frame.panel.chat.rich.UILinkItem;
import iqq.app.ui.frame.panel.chat.rich.UIRichItem;
import iqq.app.ui.frame.panel.chat.rich.UITextItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-11
 * License  : Apache License 2.0
 */
public class UIUtils {
    public static class Bean {
        private static final String LINK_REGXP = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

        public static ImageIcon byteToIcon(byte[] imageData) {
            return byteToIcon(imageData, 40, 40);
        }

        public static ImageIcon byteToIcon(byte[] imageData, int w, int h) {
            Image image = Toolkit.getDefaultToolkit().createImage(imageData);
            return new ImageIcon(image.getScaledInstance(w, h, 100));
        }

        public static ImageIcon getDefaultAvatar() {
            return IMContext.getBean(ResourceServiceImpl.class).getIcon("icons/default/qq_icon.png");
        }

        public static List<UIRichItem> toRichItem(List<IMContentItem> items) {
            List<UIRichItem> contents = new ArrayList<UIRichItem>();
            for (IMContentItem item : items) {
                switch (item.getType()) {
                    case TEXT:
                        contents.add(new UITextItem(((IMTextItem) item).getText()));
                        break;
                    case FACE:
                        break;
                    case PIC:
                        break;
                }
            }
            return contents;
        }

        public static List<IMContentItem> toIMItem(List<UIRichItem> items) {
            List<IMContentItem> contents = new ArrayList<IMContentItem>();
            for (UIRichItem item : items) {
                if (item instanceof UITextItem) {
                    UITextItem it = (UITextItem) item;
                    contents.add(new IMTextItem(it.getText()));
                }
            }
            return contents;
        }

        public static List<UIRichItem> parseLink(String text) {
            text = text.replaceAll("\r", "\n");
            List<UIRichItem> items = new ArrayList<UIRichItem>();
            Pattern pt = Pattern.compile(LINK_REGXP);
            Matcher mc = pt.matcher(text);
            int current = 0;
            while (mc.find()) {
                if (mc.start() > current) {
                    items.add(new UITextItem(text.substring(current, mc.start())));
                }
                current = mc.end();
                items.add(new UILinkItem(mc.group(0)));
            }
            if (current < text.length()) {
                items.add(new UITextItem(text.substring(current)));
            }
            return items;
        }
    }

    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();   // 获取屏幕的尺寸
    }
}
