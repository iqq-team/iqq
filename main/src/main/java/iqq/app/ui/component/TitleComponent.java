package iqq.app.ui.component;

import com.alee.extended.painter.ColorPainter;
import com.alee.extended.panel.WebButtonGroup;
import com.alee.extended.window.ComponentMoveAdapter;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebRootPaneStyle;
import com.alee.utils.ImageUtils;
import com.alee.utils.SwingUtils;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.SkinService;
import iqq.app.core.service.impl.SkinServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by zhihui_chen on 14-4-15.
 */
public class TitleComponent extends WebPanel {
    /**
     * Root pane styling icons.
     */
    public static ImageIcon skinIcon = null;
    public static ImageIcon skinActiveIcon = null;
    public static ImageIcon settingIcon = null;
    public static ImageIcon settingActiveIcon = null;
    public static ImageIcon minimizeIcon = null;
    public static ImageIcon minimizeActiveIcon = null;
    public static ImageIcon maximizeIcon = null;
    public static ImageIcon maximizeActiveIcon = null;
    public static ImageIcon restoreIcon = null;
    public static ImageIcon restoreActiveIcon = null;
    public static ImageIcon closeIcon = null;
    public static ImageIcon closeActiveIcon = null;

    /**
     * Style settings.
     */
    protected int round = WebRootPaneStyle.round;

    /**
     * Displayed window elements.
     */
    protected boolean showWindowButtons = WebRootPaneStyle.showWindowButtons;
    protected boolean showMinimizeButton = WebRootPaneStyle.showMinimizeButton;
    protected boolean showMaximizeButton = WebRootPaneStyle.showMaximizeButton;
    protected boolean showCloseButton = WebRootPaneStyle.showCloseButton;
    protected boolean groupButtons = WebRootPaneStyle.groupButtons;
    protected boolean showSkinButton = true;
    protected boolean showSettingButton = true;
    protected boolean showTitle = true;

    protected WebButton skinButton;
    protected WebButton settingButton;

    /**
     * Runtime variables
     */
    protected JComponent titleComponent;
    protected WebButtonGroup windowButtons;
    protected Window window;
    protected Frame frame;
    protected Dialog dialog;
    protected int state;
    protected int maxTitleWidth = 300;
    protected int shadeWidth = 20;
    protected String emptyTitleText = "   ";

    public TitleComponent(Window root) {
        window = root != null ? SwingUtils.getWindowAncestor(root) : null;
        frame = window instanceof Frame ? ( Frame ) window : null;
        dialog = window instanceof Dialog ? ( Dialog ) window : null;

        loadIcons();
        setOpaque(false);
        updateButtons();

        // Title
        if(showTitle) {
            titleComponent = createDefaultTitleComponent();
            this.add(titleComponent, BorderLayout.WEST);
        }
    }

    private void loadIcons() {
        SkinService skinService = IMContext.getBean(SkinServiceImpl.class);
        if(minimizeIcon == null) {
            skinIcon = skinService.getIconByKey("window/skin");
            skinActiveIcon = skinService.getIconByKey("window/skinActive");
            settingIcon = skinService.getIconByKey("window/setting");
            settingActiveIcon = skinService.getIconByKey("window/settingActive");
            minimizeIcon = skinService.getIconByKey("window/minimize");
            minimizeActiveIcon = skinService.getIconByKey("window/minimizeActive");
            maximizeIcon = skinService.getIconByKey("window/maximize");
            maximizeActiveIcon = skinService.getIconByKey("window/maximizeActive");
            restoreIcon = skinService.getIconByKey("window/restore");
            restoreActiveIcon = skinService.getIconByKey("window/restoreActive");
            closeIcon = skinService.getIconByKey("window/close");
            closeActiveIcon = skinService.getIconByKey("window/closeActive");
        }
    }

    protected void updateButtons()
    {
        // Removing old buttons
        if ( windowButtons != null )
        {
           this.remove(windowButtons);
        }

        // Creating new buttons
        final boolean isFrame = isFrame();
        final JComponent[] buttons = new JComponent[ 5 ];

        if ( showSkinButton )
        {
            final WebButton skin = new WebButton ( skinIcon );
            skin.setName("skin");
            skin.setRolloverIcon(skinActiveIcon);
            skin.setOpaque(false);
            skin.setVerticalAlignment(SwingConstants.TOP);
            skin.setPainter(new ColorPainter(new Color(0, 0, 0, 0)));
            buttons[ 0 ] = skinButton = skin;
        }

        if ( showSettingButton )
        {
            final WebButton setting = new WebButton ( settingIcon );
            setting.setName ( "setting" );
            setting.setRolloverIcon(settingActiveIcon);
            setting.setOpaque(false);
            setting.setVerticalAlignment(SwingConstants.TOP);
            setting.setPainter(new ColorPainter(new Color(0,0,0,0)));
            buttons[ 1 ] = settingButton = setting;
        }

        if ( showMinimizeButton && isFrame )
        {
            final WebButton minimize = new WebButton ( minimizeIcon );
            minimize.setName ( "minimize" );
            minimize.setRolloverIcon(minimizeActiveIcon);
            minimize.setOpaque(false);
            minimize.setVerticalAlignment(SwingConstants.TOP);
            minimize.setPainter(new ColorPainter(new Color(0,0,0,0)));
            minimize.addActionListener ( new ActionListener()
            {
                @Override
                public void actionPerformed ( final ActionEvent e )
                {
                    iconify();
                }
            } );
            buttons[ 2 ] = minimize;
        }
        if ( showMaximizeButton && isResizable() && isFrame )
        {
            final WebButton maximize = new WebButton ( maximizeIcon )
            {
                @Override
                public Icon getIcon ()
                {
                    return isFrameMaximized() ? restoreIcon : maximizeIcon;
                }

                @Override
                public Icon getRolloverIcon ()
                {
                    return isFrameMaximized() ? restoreActiveIcon : maximizeActiveIcon;
                }
            };
            maximize.setName ( "maximize" );
            maximize.setRolloverIcon(maximizeActiveIcon);
            maximize.setOpaque(false);
            maximize.setVerticalAlignment(SwingConstants.TOP);
            maximize.setPainter(new ColorPainter(new Color(0,0,0,0)));
            maximize.addActionListener ( new ActionListener ()
            {
                @Override
                public void actionPerformed ( final ActionEvent e )
                {
                    if ( isFrame () )
                    {
                        if ( isFrameMaximized () )
                        {
                            restore ();
                        }
                        else
                        {
                            maximize ();
                        }
                    }
                }
            } );
            buttons[ 3 ] = maximize;
        }
        if ( showCloseButton )
        {
            final WebButton close = new WebButton ( closeIcon );
            close.setName ( "close" );
            close.setRolloverIcon(closeActiveIcon);
            close.setOpaque(false);
            close.setVerticalAlignment(SwingConstants.TOP);
            close.setPainter(new ColorPainter(new Color(0,0,0,0)));
            close.addActionListener ( new ActionListener ()
            {
                @Override
                public void actionPerformed ( final ActionEvent e )
                {
                    close ();
                }
            } );
            buttons[ 4 ] = close;
        }

        windowButtons = new WebButtonGroup( buttons );
        windowButtons.setOpaque(false);
        windowButtons.setMargin(0, 0, 0, -2);
        updateWindowButtonsStyle();
        this.add(windowButtons, BorderLayout.EAST);
    }
    Point mouseDownCompCoords;
    protected JComponent createDefaultTitleComponent ()
    {
        final WebLabel titleIcon = new WebLabel ()
        {
            @Override
            public Icon getIcon ()
            {
                return getWindowIcon ();
            }
        };

        final TitleLabel titleLabel = new TitleLabel ();
        titleLabel.setDrawShade ( true );
        titleLabel.setHorizontalAlignment ( WebLabel.CENTER );
        titleLabel.addComponentListener ( new ComponentAdapter()
        {
            @Override
            public void componentResized ( final ComponentEvent e )
            {
                titleLabel.setHorizontalAlignment ( titleLabel.getRequiredSize ().width > titleLabel.getWidth () ? SwingConstants.LEADING : SwingConstants.CENTER );
            }
        } );
        SwingUtils.setFontSize ( titleLabel, 13 );

        // Window move and max/restore listener
        final ComponentMoveAdapter cma = new ComponentMoveAdapter ()
        {
            @Override
            public void mouseClicked ( final MouseEvent e )
            {
                if ( isFrame () && SwingUtils.isLeftMouseButton ( e ) && e.getClickCount () == 2 )
                {
                    if ( isFrameMaximized () )
                    {
                        restore ();
                    }
                    else
                    {
                        // maximize ();
                    }
                }
            }

            @Override
            public void mouseDragged ( final MouseEvent e )
            {
                if ( dragging && isFrameMaximized () )
                {
                    initialPoint = new Point ( initialPoint.x + shadeWidth, initialPoint.y + shadeWidth );
                    restore ();
                }
                super.mouseDragged ( e );
            }
        };
        this.addMouseListener(cma);
        this.addMouseMotionListener(cma);

        final WebPanel titlePanel = new WebPanel ( new BorderLayout ( 5, 0 ) );
        titlePanel.setOpaque ( false );
        titleLabel.setForeground(Color.white);
        titlePanel.setMargin(4, 5, 4, 10);
        titlePanel.add ( titleIcon, BorderLayout.LINE_START );
        titlePanel.add ( titleLabel, BorderLayout.CENTER );
        window.addPropertyChangeListener("title", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                titleLabel.setText(frame.getTitle());
            }
        });
        window.addPropertyChangeListener("iconImage", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                titleIcon.setIcon(getWindowIcon());
            }
        });
        return titlePanel;
    }

    /**
     * Returns window title
     */

    protected String getWindowTitle ()
    {
        if ( isDialog () )
        {
            return dialog.getTitle ();
        }
        else if ( isFrame () )
        {
            return frame.getTitle ();
        }
        else
        {
            return null;
        }
    }

    /**
     * Finds a frame image with most suitable size
     */

    protected ImageIcon getWindowIcon ()
    {
        final java.util.List<Image> images = window != null ? window.getIconImages () : null;
        if ( images != null && images.size () > 1 )
        {
            int bestIndex = 0;
            int bestDiff = Math.abs ( images.get ( bestIndex ).getWidth ( null ) - 16 );
            for ( int i = 1; i < images.size (); i++ )
            {
                if ( bestDiff == 0 )
                {
                    break;
                }
                final int diff = Math.abs ( images.get ( i ).getWidth ( null ) - 16 );
                if ( diff < bestDiff )
                {
                    bestIndex = i;
                    bestDiff = diff;
                }
            }
            return generateProperIcon ( images.get ( bestIndex ) );
        }
        else if ( images != null && images.size () == 1 )
        {
            return generateProperIcon ( images.get ( 0 ) );
        }
        else
        {
            return new ImageIcon ();
        }
    }

    protected ImageIcon generateProperIcon ( final Image image )
    {
        if ( image.getWidth ( null ) <= 16 )
        {
            return new ImageIcon ( image );
        }
        else
        {
            return ImageUtils.createPreviewIcon(image, 16);
        }
    }

    /**
     *
     */
    protected class TitleLabel extends WebLabel
    {
        /**
         * Returns window title text.
         * There is a small workaround to show window title even when it is empty.
         * That workaround allows window dragging even when title is not set.
         *
         * @return window title text
         */
        @Override
        public String getText ()
        {
            final String title = getWindowTitle ();
            return title != null && !title.equals ( "" ) ? title : emptyTitleText;
        }

        /**
         * Returns preferred title size.
         * There is also a predefined title width limit to force it shrink.
         *
         * @return preferred title size
         */
        @Override
        public Dimension getPreferredSize ()
        {
            final Dimension ps = super.getPreferredSize ();
            ps.width = Math.min ( ps.width, maxTitleWidth );
            return ps;
        }

        /**
         * Returns actual preferred size of the title label.
         *
         * @return actual preferred size of the title label
         */
        public Dimension getRequiredSize ()
        {
            return super.getPreferredSize ();
        }
    }

    protected void updateWindowButtonsStyle ()
    {
        if ( windowButtons != null )
        {
            windowButtons.setButtonsDrawFocus ( false );
            windowButtons.setButtonsShadeWidth (0);
            windowButtons.setButtonsRound ( 0 );
            windowButtons.setButtonsMargin(0);
        }
    }

    /**
     * Closes the Window.
     */

    protected void close ()
    {
        if ( window != null )
        {
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        }
    }

    /**
     * Iconifies the Frame.
     */

    protected void iconify ()
    {
        if ( frame != null )
        {
            frame.setExtendedState ( Frame.ICONIFIED );
        }
    }

    /**
     * Maximizes the Frame.
     */

    protected void maximize ()
    {
        if ( frame != null )
        {
            frame.setExtendedState ( Frame.MAXIMIZED_BOTH );
            state = Frame.MAXIMIZED_BOTH;
        }
    }

    /**
     * Restores the Frame size.
     */

    protected void restore ()
    {
        if ( frame != null )
        {
            frame.setExtendedState ( Frame.NORMAL );
            state = Frame.NORMAL;
        }
    }

    /**
     * Checks if root pane's window is resizable
     */

    protected boolean isResizable ()
    {
        return isDialog () ? dialog.isResizable () : isFrame () && frame.isResizable ();
    }

    /**
     * Checks if root pane is inside a frame
     */

    protected boolean isFrame ()
    {
        return frame != null;
    }

    /**
     * Checks if frame is maximized
     */

    protected boolean isFrameMaximized ()
    {
        return isFrame () && state == Frame.MAXIMIZED_BOTH;
    }

    /**
     * Checks if root pane is inside a dialog
     */

    protected boolean isDialog ()
    {
        return dialog != null;
    }

    public WebButtonGroup getWindowButtons ()
    {
        return windowButtons;
    }

    public boolean isShowWindowButtons ()
    {
        return showWindowButtons;
    }

    public void setShowWindowButtons ( final boolean showWindowButtons )
    {
        this.showWindowButtons = showWindowButtons;
        this.revalidate();
    }

    public boolean isShowMinimizeButton ()
    {
        return showMinimizeButton;
    }

    public void setShowMinimizeButton ( final boolean showMinimizeButton )
    {
        this.showMinimizeButton = showMinimizeButton;
        updateButtons ();
        this.revalidate();
    }

    public boolean isShowMaximizeButton ()
    {
        return showMaximizeButton;
    }

    public void setShowMaximizeButton ( final boolean showMaximizeButton )
    {
        this.showMaximizeButton = showMaximizeButton;
        updateButtons();
        this.revalidate();
    }

    public boolean isShowCloseButton ()
    {
        return showCloseButton;
    }

    public void setShowCloseButton ( final boolean showCloseButton )
    {
        this.showCloseButton = showCloseButton;
        this.updateButtons();
        this.revalidate();
    }

    public boolean isGroupButtons ()
    {
        return groupButtons;
    }

    public void setGroupButtons ( final boolean groupButtons )
    {
        this.groupButtons = groupButtons;
        this.updateButtons();
        this.revalidate();
        this.repaint();
    }

    public boolean isShowSkinButton() {
        return showSkinButton;
    }

    public void setShowSkinButton(boolean showSkinButton) {
        this.showSkinButton = showSkinButton;
        this.updateButtons();
        this.revalidate();
    }

    public boolean isShowSettingButton() {
        return showSettingButton;
    }

    public void setShowSettingButton(boolean showSettingButton) {
        this.showSettingButton = showSettingButton;
        this.updateButtons();
        this.revalidate();
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        if(showTitle) {
            this.add(titleComponent, BorderLayout.WEST);
        } else {
            this.remove(titleComponent);
        }
    }

    public WebButton getSkinButton() {
        return skinButton;
    }

    public WebButton getSettingButton() {
        return settingButton;
    }
}
