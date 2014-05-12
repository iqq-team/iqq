package iqq.app.util;

import javax.swing.*;
import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-11
 * License  : Apache License 2.0
 */
public class UIUtil {
    public static class Bean {

        public static ImageIcon byteToIcon(byte[] imageData) {
            return byteToIcon(imageData, 40, 40);
        }

        public static ImageIcon byteToIcon(byte[] imageData, int w, int h) {
            Image image = Toolkit.getDefaultToolkit().createImage(imageData);
            return new ImageIcon(image.getScaledInstance(w, h, 100));
        }
    }
}
