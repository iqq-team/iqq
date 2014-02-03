package iqq.app.ui.widget.screencapture;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class BMPFileFilter extends FileFilter {

	public boolean accept(File f) {
		return f.isDirectory() || f.getName().matches("^.+?\\.[bB][mM][pP]$");
	}

	public String getDescription() {
		return "(*.bmp) BMP Image";
	}
}