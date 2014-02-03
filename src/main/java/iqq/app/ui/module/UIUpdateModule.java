package iqq.app.ui.module;

import iqq.app.core.IMContext;
import iqq.app.core.IMException;
import iqq.app.core.IMService;
import iqq.app.event.IMEvent;
import iqq.app.event.IMEventType;
import iqq.app.service.IMEventService;
import iqq.app.ui.IMFrameView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UIUpdateModule extends IMFrameView {
	private static final long serialVersionUID = 8336887670577801018L;

	/**
	 * 初始化，判断更新
	 */
	@Override
	public void init(IMContext context) throws IMException {
		// TODO Auto-generated method stub
		super.init(context);
		/*
		IMTaskService taskService = getContext()
				.getSerivce(IMService.Type.TASK);
		taskService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					if (IMUpdater.checkUpdate()) {
						int ok = WebOptionPane.showConfirmDialog(null,
								"是否更新主程序", "IQQ更新", WebOptionPane.YES_NO_OPTION);
						if (ok == WebOptionPane.YES_OPTION) {
							executeUpdate();
						}
					}
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		*/
	}

	/**
	 * 执行更新
	 * 
	 * @param vcode
	 */
	private void executeUpdate() {
		final IMEventService events = getContext().getSerivce(
				IMService.Type.EVENT);
		try {
			// 复制更新文件到根目录
			copyFile(new File("lib/updater.jar"), new File("updater.jar"));
			Runtime.getRuntime().exec(
					new String[] { "java", "-jar", "updater.jar", new File("").getAbsolutePath() }, null,
					new File("lib").getParentFile());

		} catch (IOException e) {
			e.printStackTrace();
		}
		events.broadcast(new IMEvent(IMEventType.APP_EXIT_READY));
	}

	public void copyFile(File src, File dest) {
		if(!src.exists()) {
			return ;
		}
		if(dest.exists()) {
			dest.delete();
		}
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			byte[] buffer = new byte[4096];
			int read = 0;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (out != null) {
					out.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
