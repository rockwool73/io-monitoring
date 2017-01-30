package dk.heick.io.monitoring.utils;

import java.io.File;
import java.util.Comparator;

/**
 * File comparator on the lastModified value.
 * @author Frederik Heick
 * @see File#lastModified()
 */
public class FileLastModifiedComparator implements Comparator<File> {

	@Override
	public int compare(File o1, File o2) {		
		return Long.compare(o1.lastModified(), o2.lastModified());
	}

}
