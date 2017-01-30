package dk.heick.io.monitoring.filter;

import java.io.File;
import java.io.FileFilter;

public abstract class ChildFileFilter implements FileFilter {
	
	private FileFilter childFilter;
	
	public ChildFileFilter() {
		this(null);
	}
		
	public ChildFileFilter(FileFilter childFilter) {
		super();
		this.childFilter=childFilter; 
				
	}
	public FileFilter getChildFilter() {
		return childFilter;
	}
	public boolean hasChildFilter() {
		return getChildFilter()!=null;
	}
	public boolean acceptChildFilter(File pathname) {
		if (hasChildFilter()) {
			return getChildFilter().accept(pathname);
		} else {
			return true;
		}
	}

}
