package iqq.app.ui.widget.screencapture;

import iqq.app.util.SkinUtils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * 系统参数设置对话框
 * 
 * @author Johnson Lee
 * 
 */
public class PreferencesDialog extends JDialog {
	private static final long serialVersionUID = -6144025341989211530L;

	private JTable table = new JTable(new Object[][] {
			{ "color", "Alt + C", "set the forecolor of the canvas" },
			{ "copy", "Ctrl + C", "copy the cut zone to the clipboard" },
			{ "cut", "[Shift +]C", "cut canvas" },
			{ "line", "L", "draw line" },
			{ "move", "V", "move the cut zone" },
			{ "paste", "Ctrl + V",
					"paste the content into screenshot from clipboard" },
			{ "pencil", "P", "draw with pencil" },
			{ "print", "Ctrl + P", "print the screenshot" },
			{ "rectangle", "[Shift +]R", "draw rectangle" },
			{ "save", "Ctrl + S", "save the screenshot" },
			{ "stroke", "Alt + S", "set the stroke of canvas" },
			{ "undo", "Ctrl + Z", "undo the options" },
			{ "zoomout", "Z", "zoom in the canvas" },
			{ "zoomin", "Alt + Z", "zoom in the canvas" } }, new String[] {
			"action", "binding", "description" });
	private JTableHeader header = table.getTableHeader();
	private JScrollPane pnlContent = new JScrollPane(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	public PreferencesDialog(String title)
			throws HeadlessException {
		setAlwaysOnTop(true);
		JPanel pnlRoot = (JPanel) this.getContentPane();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.pnlContent.getViewport().add(table);
		this.table.setRowHeight(22);
		this.table.setEnabled(false);
		this.header.getColumnModel().getColumn(0).setWidth(50);
		TableColumn tc = this.table.getColumnModel().getColumn(0);
		tc.setCellRenderer(new IconableTableCellRenderer());
		pnlRoot.add(header, BorderLayout.NORTH);
		pnlRoot.add(this.pnlContent, BorderLayout.CENTER);
	}

	public void setVisible(boolean b) {
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		this.pack();
		this.setLocation((scr.width - this.getWidth()) / 2,
				(scr.height - this.getHeight()) / 2);
		super.setVisible(b);
	}

	private static class IconableTableCellRenderer extends
			DefaultTableCellRenderer {
		private static final long serialVersionUID = -2179677502574303278L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			this.setIcon(SkinUtils.getImageIcon("chat/screenshot", value.toString()));
			return super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);

		}
	}
}
