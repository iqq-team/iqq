/*
 * This library is dual-licensed: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation. For the terms of this
 * license, see licenses/gpl_v3.txt or <http://www.gnu.org/licenses/>.
 *
 * You are free to use this library under the terms of the GNU General
 * Public License, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * Alternatively, you can license this library under a commercial
 * license, as set out in licenses/commercial.txt.
 */

package iqq.app.ui.widget.hideframe;

import java.awt.Rectangle;
import java.awt.Toolkit;

import ch.swingfx.twinkle.NotificationBuilder;

/**
 * This enum has some default positions that can be used by
 * {@link NotificationBuilder}.withPosition()
 * 
 * @author Heinrich Spreiter
 * 
 */
public enum Positions implements IPosition {

	TOP {

		@Override
		public Rectangle getPosition(int sideWidth, int side) {
			return getPosition(sideWidth, side, 0);
		}

		@Override
		public Rectangle getPosition(int sideWidth, int side, int gap) {
			return new Rectangle(gap, 0, Toolkit.getDefaultToolkit()
					.getScreenSize().width - gap * 2, sideWidth + side);
		}
	},
	LEFT {

		@Override
		public Rectangle getPosition(int sideWidth, int side) {
			return getPosition(sideWidth, side, 0);
		}

		@Override
		public Rectangle getPosition(int sideWidth, int side, int gap) {
			return new Rectangle(0, gap, sideWidth, Toolkit.getDefaultToolkit()
					.getScreenSize().height - gap * 2);
		}

	},
	RIGHT {

		@Override
		public Rectangle getPosition(int sideWidth, int side) {
			return getPosition(sideWidth, side);
		}

		@Override
		public Rectangle getPosition(int sideWidth, int side, int gap) {
			return new Rectangle(
					Toolkit.getDefaultToolkit().getScreenSize().width - side,
					gap, sideWidth,
					Toolkit.getDefaultToolkit().getScreenSize().height - gap
							* 2);
		}

	}
}
