package iqq.app.ui.widget;

import java.util.Timer;
import java.util.TimerTask;

import com.alee.laf.rootpane.WebFrame;

public class WindowTest {

	public static void main(String[] args) {
		final WebFrame frame = new WebFrame();
		frame.setDefaultCloseOperation(WebFrame.EXIT_ON_CLOSE);
		frame.setSize(200, 300);
		frame.setVisible(true);
		
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				frame.setVisible(true);
			}
		}, 5000);
	}

}
