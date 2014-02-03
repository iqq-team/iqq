package iqq.app.ui.widget.screencapture;


import java.io.File;

import javax.swing.filechooser.FileFilter;

public class JPEGFileFilter extends FileFilter {

	public boolean accept(File f) {
		return f.isDirectory() || f.getName().matches("^.+?\\.[jJ][pP][gG]$");
	}

	public String getDescription() {
		return "(*.jpg) JPEG Image";
	}
}