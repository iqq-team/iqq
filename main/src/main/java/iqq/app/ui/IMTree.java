package iqq.app.ui;

import com.alee.extended.tree.WebAsyncTree;
import com.alee.laf.tree.WebTreeUI;
import iqq.app.core.service.SkinService;
import iqq.app.ui.skin.Skin;

import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 树组件，继承于weblaf的异步加载树组件
 *
 * Project  : iqq-projects
 * Author   : 承∮诺 < 6208317@qq.com >
 * Created  : 14-5-8
 * License  : Apache License 2.0
 */
public class IMTree extends WebAsyncTree implements Skin {

    public IMTree() {
        this.setPaintLines(false);
        this.setOpaque(false);
        this.setRootVisible(false);
        this.setShowsRootHandles(false);
        this.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.setUI(new IMTreeUI());
    }

    @Override
    public void installSkin(SkinService skinService) {

    }

    class IMTreeUI extends WebTreeUI {
        public Color selectedBg = new Color(240, 220 , 147);
        public Color rolloverBg = new Color(178, 212 , 243);

        /**
         * 选中时画背景
         *
         * @param g2d
         */
        @Override
        protected void paintSelection(Graphics2D g2d) {
            if ( tree.getSelectionCount () > 0 )
            {
                // Draw final selections
                final java.util.List<Rectangle> selections = getSelectionRects ();
                for ( final Rectangle rect : selections )
                {
                    g2d.setPaint ( selectedBg );
                    g2d.fill ( new RoundRectangle2D.Double ( rect.x + selectionShadeWidth, rect.y + selectionShadeWidth,
                            rect.width - selectionShadeWidth * 2 - 1, rect.height - selectionShadeWidth * 2 - 1, 0,
                            0 ) );
                }
            }
        }

        /**
         * 鼠标经过画背景
         *
         * @param g2d
         */
        @Override
        protected void paintRolloverNodeHighlight(Graphics2D g2d) {
            if ( tree.isEnabled () && highlightRolloverNode && rolloverRow != -1 && !tree.isRowSelected ( rolloverRow ) )
            {
                final Rectangle rect = isFullLineSelection () ? getFullRowBounds ( rolloverRow ) : tree.getRowBounds ( rolloverRow );
                if ( rect != null )
                {
                    //final Composite old = LafUtils.setupAlphaComposite ( g2d, 0.35f );
                    g2d.setPaint ( rolloverBg );
                    g2d.fill ( new RoundRectangle2D.Double ( rect.x + selectionShadeWidth, rect.y + selectionShadeWidth,
                            rect.width - selectionShadeWidth * 2 - 1, rect.height - selectionShadeWidth * 2 - 1, 0,
                            0 ) );
                    //LafUtils.restoreComposite ( g2d, old );
                }
            }
        }
    }
}
