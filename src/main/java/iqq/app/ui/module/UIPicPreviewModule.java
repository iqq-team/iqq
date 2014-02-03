/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Project  : IQQ_V2.1
 * Package  : iqq.app.ui.module
 * File     : UIPicPreviewModule.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2013-4-8
 * License  : Apache License 2.0 
 */
package iqq.app.ui.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventHandler;
import iqq.app.event.IMEventHandlerProxy;
import iqq.app.event.IMEventType;
import iqq.app.service.IMSkinService;
import iqq.app.ui.BackgroundPanel;
import iqq.app.ui.IMDialogView;
import iqq.app.util.SkinUtils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import com.alee.extended.image.WebImage;
import com.alee.extended.panel.CenterPanel;

/**
 * 
 * 图片预览模块
 * 
 * @author solosky <solosky772@qq.com>
 * 
 */
public class UIPicPreviewModule extends IMDialogView {
	private static final long serialVersionUID = -8068212248010652768L;
	
	private PreviewPanel previewPanel;
	private Image closeImg;
	private Image closeHover;
	private Point closePt;
	private boolean closeEnable = false;

	@Override
	public void init(IMContext context) throws IMException {
		super.init(context);

		// 初始化窗口
		initFrame();

		// 初始化显示内容，登录面板
		previewPanel = new PreviewPanel(this);
		setContentPanel(previewPanel);
		changeSkin(SkinUtils.getPainter(IMSkinService.Type.NPICON, "black"));

		// 注册感兴趣的事件
		IMEventHandlerProxy.register(this, context);
	}

	@Override
	public void destroy() throws IMException {
		IMEventHandlerProxy.unregister(this);
		super.destroy();

	}

	/**
	 * 初始化窗口
	 */
	private void initFrame() {
		// 设置标题
		setIconImage(SkinUtils.getImageIcon("appLogo").getImage());

		// 设置程序宽度 和高度
		setMinimumSize(new Dimension(280, 280));
		setLocationRelativeTo(null);
		setShowTitleComponent(false);
		setShowWindowButtons(false);
		setShadeWidth(50);
		setInactiveShadeWidth(30);
		closeImg = SkinUtils.getImageIcon("picPreview/closeNormal").getImage();
		closeHover = SkinUtils.getImageIcon("picPreview/closePress").getImage();
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Rectangle rect = new Rectangle(closePt.x, closePt.y, 50, 50);
				if (isPtInRect(rect, e.getPoint())) {
					closeEnable = true;
					repaint();
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (closeEnable) {
					UIPicPreviewModule.this.hide();
				}
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		closePt = new Point(this.getWidth() - closeImg.getWidth(this) - 30, 30);
		g.drawImage(closeImg, closePt.x, closePt.y, this);
		g.dispose();
	}

	@IMEventHandler(IMEventType.SHOW_PIC_PREVIEW)
	protected void processIMShowPicPreview(IMEvent event) {
		if (event.getTarget() instanceof BufferedImage) {
			BufferedImage pic = (BufferedImage) event.getTarget();
			previewPanel.background.setImage(pic);
			Dimension dim = new Dimension(pic.getWidth() + 100,
					pic.getHeight() + 110);
			this.setSize(dim);
			this.setLocationRelativeTo(null);
			this.show();
			// TODO ..屏幕居中显示
		}
	}

	/**
	 * 检测是否在矩形框内
	 * 
	 * @param rect
	 * @param point
	 * @return
	 */
	public boolean isPtInRect(Rectangle rect, Point point) {
		if (rect != null && point != null) {
			int x0 = rect.x;
			int y0 = rect.y;
			int x1 = rect.width + x0;
			int y1 = rect.height + y0;
			int x = point.x;
			int y = point.y;

			return x >= x0 && x < x1 && y >= y0 && y < y1;
		}
		return false;
	}

	public class CloseObserver implements ImageObserver {

		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y,
				int width, int height) {
			return false;
		}

	}

	public class PreviewPanel extends BackgroundPanel {
		public WebImage background;

		private boolean dragging = false;
		private int draggedAtX;
		private int draggedAtY;

		public PreviewPanel(final Window view) {
			super(view);
			setMinimumSize(new Dimension(200, 200));
			IMSkinService skins = getContext().getSerivce(IMService.Type.SKIN);
			background = new WebImage(skins.getBufferedImage("defaultPic"));
			add(new CenterPanel(background));
			setAutoscrolls(true);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					draggedAtX = e.getX();
					draggedAtY = e.getY();
					dragging = true;
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					dragging = false;
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2) {
						view.setVisible(false);
					}
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					if (dragging) {
						int x = e.getX() - draggedAtX + view.getLocation().x;
						int y = e.getY() - draggedAtY + view.getLocation().y;
						view.setLocation(x, y);
					}
				}
			});
		}

	}

}
