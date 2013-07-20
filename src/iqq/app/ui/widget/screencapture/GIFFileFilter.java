package iqq.app.ui.widget.screencapture;


import java.io.File;

import javax.swing.filechooser.FileFilter;

public class GIFFileFilter extends FileFilter {

	public boolean accept(File f) {
		return f.isDirectory() || f.getName().matches("^.+?\\.[gG][iI][fF]$");
	}

	public String getDescription() {
		return "(*.gif) GIF Image";
	}
}