package iqq.app;

import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.service.IMI18nService;
import iqq.app.service.impl.IMI18nServiceImpl;
import iqq.app.service.impl.IMPropServiceImpl;

import org.junit.Test;

/**
 * @author ChenZhiHui <6208317@qq.com>
 * @create-time 2013-3-16
 */
public class TestLanguage {

	@Test
	public void test() throws IMException {
		IMApp app = IMApp.me();
		IMService srvProp = new IMPropServiceImpl();
		IMI18nService srvI18N = new IMI18nServiceImpl();
		srvProp.init(app);
		srvI18N.init(app);
		System.out.println(srvI18N.getAllMessages());
		
		new MyThread(srvI18N).startup();
		
	}
	class MyThread extends Thread {
		IMI18nService srv = null;
		public MyThread(IMI18nService srv) {
			this.srv = srv;
		 }
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println(srv.getAllMessages());
		}
		
		public void startup() {
			this.run();
		}
	}
}
