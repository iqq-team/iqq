package iqq.app.ui.widget.screencapture;


import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PNGFileFilter extends FileFilter {

	public boolean accept(File f) {
		return f.isDirectory() || f.getName().matches("^.+?\\.[pP][nN][gG]$");
	}

	public String getDescription() {
		return "(*.png) PNG Images";
	}
}