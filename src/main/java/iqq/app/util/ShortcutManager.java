/**
 * 
 */
package iqq.app.util;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author ZhiHui_Chen<6208317@qq.com>
 * @create date 2013-4-7
 */
public class ShortcutManager implements AWTEventListener {
	// 使用单态模式
	private static ShortcutManager instance = new ShortcutManager();

	// 快捷键与事件处理对象键值对
	private Map<String, ShortcutListener> listeners;

	// 某组件上发生了快捷键列表中的快捷键事件, 如果他的父组件在被忽略组件列表中, 则放弃这个事件.
	private Set<Component> ignoredComponents;

	// 保存键盘上键与它的ascii码对应
	// 如果以某键的ascii码为下标, 数组中此下标的值为true, 说明此键被按下了.
	// 当此键被释放开后, 数组中对应的值修改为false
	private boolean[] keys;

	private ShortcutManager() {
		keys = new boolean[256];
		ignoredComponents = new HashSet<Component>();
		listeners = new HashMap<String, ShortcutListener>();

		// 只关心键盘事件
		Toolkit.getDefaultToolkit().addAWTEventListener(this,
				AWTEvent.KEY_EVENT_MASK);
	}

	/**
	 * 有键盘事件发生
	 */
	public void eventDispatched(AWTEvent event) {
		if (event.getClass() == KeyEvent.class) {
			KeyEvent ke = (KeyEvent) event;

			if (ke.getID() == KeyEvent.KEY_PRESSED) {
				keys[ke.getKeyCode()] = true;

				// 查找快捷键对应的处理对象, 然后调用事件处理函数
				String shortcut = searchShortcut();
				ShortcutListener l = listeners.get(shortcut);
				if (l != null && !isIgnored(event)) {
					l.handle();
				}
			} else if (ke.getID() == KeyEvent.KEY_RELEASED) {
				keys[ke.getKeyCode()] = false;
			}
		}
	}

	protected String searchShortcut() {
		// 每个键之间用一个"."来隔开.
		// 例如ctr + x的对应值为"17.88."
		StringBuilder shortcut = new StringBuilder();
		for (int i = 0; i < keys.length; ++i) {
			if (keys[i]) {
				shortcut.append(i).append(".");
			}
		}
		// System.out.println(shortcut.toString());
		return shortcut.toString();
	}

	/**
	 * 查找此快捷键事件是否要被抛弃
	 * 
	 * @param event
	 * @return
	 */
	protected boolean isIgnored(AWTEvent event) {
		if (!(event.getSource() instanceof Component)) {
			return false;
		}

		boolean ignored = false;
		for (Component com = (Component) event.getSource(); com != null; com = com
				.getParent()) {
			if (ignoredComponents.contains(com)) {
				ignored = true;
				break;
			}
		}

		return ignored;
	}

	public static ShortcutManager getInstance() {
		return instance;
	}

	public void removeShortcutListener(ShortcutListener l) {
		String tempKey = null;
		for (Map.Entry<String, ShortcutListener> e : listeners.entrySet()) {
			if (e.getValue().equals(l)) {
				tempKey = e.getKey();
			}
		}

		listeners.remove(tempKey);
	}

	public void addShortcutListener(ShortcutListener l, int... keys) {
	    // 快捷键的对应值按它们的键顺序大小来进行创建
	    StringBuilder sb = new StringBuilder();
	    Arrays.sort(keys);
	     
	    for (int i = 0; i < keys.length; ++i) {
	    	if (0 < keys[i] && keys[i] < this.keys.length) {
	            sb.append(keys[i]).append(".");
	        } else {
	            System.out.println("Key is not valid");
	            return;
	        }
	    }
	 
	    String shortcut = sb.toString();
	 
	    // 如果还不存在, 则加入
	    if (listeners.containsKey(shortcut)) {
	        System.out.println("The shourt cut is already used.");
	    } else {
	        listeners.put(shortcut, l);
	    }
	}

	public void addIgnoredComponent(Component com) {
		ignoredComponents.add(com);
	}

	public void removeDiscardComponent(Component com) {
		ignoredComponents.remove(com);
	}

	public static interface ShortcutListener {
		void handle();
	}
}
