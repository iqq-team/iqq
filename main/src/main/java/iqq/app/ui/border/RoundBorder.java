package iqq.app.ui.border;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-11
 * License  : Apache License 2.0
 */
public class RoundBorder implements Border {
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        final Graphics2D g2d = ( Graphics2D ) g;
        g2d.setPaint ( new Color(160, 160, 160) );
        g2d.drawRoundRect(x, y, width, height, 10, 10);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(5, 5, 5, 5);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
