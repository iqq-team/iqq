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

/**
 * Interface for positioning the notification.
 * @author Heinrich Spreiter
 *
 */
public interface IPosition {
	public Rectangle getPosition(int sideWidth, int side);
	public Rectangle getPosition(int sideWidth, int side, int gap);
}
