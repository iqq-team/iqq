package iqq.app.ui.widget;

import iqq.app.IMApp;
import iqq.app.service.IMSkinService.Type;
import iqq.app.util.SkinUtils;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.alee.laf.button.WebButton;
import com.alee.laf.rootpane.WebFrame;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-14
 */
public class TitleTest {
	
	public static void main(String[] args) throws InterruptedException {
		IMApp.me().startup();

		WebFrame f = new WebFrame();
		f.setSize(200, 300);
		f.setVisible(true);
		f.add(new IMTitleComponent(null), BorderLayout.PAGE_START);
		
		final WebButton b = new WebButton("test");
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				b.setPainter(SkinUtils.getPainter(Type.RESOURCE, "loginButton"));
			}
		});
		f.add(b, BorderLayout.CENTER);
		
	}

}
