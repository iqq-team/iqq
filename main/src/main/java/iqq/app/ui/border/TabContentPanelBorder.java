package iqq.app.ui.border;

import org.sexydock.tabs.jhrome.JhromeContentPanelBorder;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-13
 * License  : Apache License 2.0
 */
public class TabContentPanelBorder extends JhromeContentPanelBorder {
    int		roundness		= 3;
    int		thickness		= 0;
    Color   outlineColor	= new Color(55, 55, 55, 120);
    Color	backgroundColor	= new Color(255, 255, 255, 0);

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder( Component c , Graphics g , int x , int y , int width , int height )
    {
        Graphics2D g2 = ( Graphics2D ) g;

        int inset = Math.max( thickness - 1 , 0 );

        Path2D path = new Path2D.Double( );
        path.moveTo( x + inset , y + height - inset - inset );
        path.lineTo( x + inset , y + inset + roundness );
        path.curveTo( x + inset , y + inset , x + inset , y + inset , x + inset + roundness , y + inset );
        path.lineTo( x + width - roundness - inset - inset , y + inset );
        path.curveTo( x + width - inset - inset , y + inset , x + width - inset - inset , y + inset , x + width - inset - inset , y + inset + roundness );
        path.lineTo( x + width - inset - inset , y + height - inset - inset );
        path.closePath( );

        Stroke prevStroke = g2.getStroke( );
        Paint prevPaint = g2.getPaint( );
        Object prevAntialias = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON );

        g2.setStroke( new BasicStroke( thickness ) );
        g2.setColor( outlineColor );
        g2.draw( path );

        g2.setColor( backgroundColor );
        g2.fill( path );

        g2.setStroke( prevStroke );
        g2.setPaint( prevPaint );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING , prevAntialias );
    }

    @Override
    public Insets getBorderInsets( Component c )
    {
        return new Insets( roundness + thickness , roundness + thickness , roundness + thickness , roundness + thickness );
    }

    @Override
    public boolean isBorderOpaque( )
    {
        // TODO Auto-generated method stub
        return false;
    }
}
