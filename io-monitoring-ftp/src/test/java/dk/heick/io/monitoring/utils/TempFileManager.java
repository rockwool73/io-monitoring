package dk.heick.io.monitoring.utils;
 
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TempFileManager {
	
	private List<File> files = new ArrayList<File>();
		
	public TempFileManager() {
		super();
	}
	public List<File> getFiles() {
		return Collections.unmodifiableList(files);		
	}
	public void add(File fileOrDirectory) {
		files.add(fileOrDirectory);
	}
	public int size() {
		return files.size();
	}
	public void cleanup() throws IOException {
		for (File file : files) {
			if ((file!=null) && (file.exists()) && (file.isFile())) {
				FileUtils.deleteFile(file);				
			}
		}
		for (File file : files) {
			if ((file!=null) && (file.exists()) && (file.isDirectory())) {
				FileUtils.deleteDirectory(file, true);				
			}
		}
		files.clear();
	}
	
	
	public File createNullFile() {
		return null;
	}
	public Path createNullPath() {
		return null;
	}
	
	public File createTempRootDirectory(String name) throws IllegalArgumentException {
		try {
			File result = Files.createTempDirectory(name).toFile();
			exists("Directory", result);
			files.add(result);
			return result;
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
	}

	public File createDirectory(File inDirectory,String name) throws IllegalArgumentException {
		try {
			if (inDirectory!=null) {
				exists("Directory", inDirectory);
				if (inDirectory.isDirectory()) {
					File result = Files.createDirectory(new File(inDirectory,name).toPath()).toFile();
					exists("Directory", result);
					files.add(result);
					return result;
				} else {
					throw new IllegalArgumentException("InDirectory ["+inDirectory.getAbsolutePath()+"] is not a valid directory.");
				}
			} else {
				File result = Files.createDirectory(new File(name).toPath()).toFile();
				exists("Directory", result);
				files.add(result);
				return result;
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
	}
	
	
	public File createTempDirectory() throws IllegalArgumentException {
		return createTempDirectory(null,"tempdir");
	}
	public File createTempDirectory(String prefix) throws IllegalArgumentException {
		return createTempDirectory(null,prefix);
	}
	public File createTempDirectory(File inDirectory) throws IllegalArgumentException {
		return createTempDirectory(inDirectory,"subtempdir");
	}
	public File createTempDirectory(File inDirectory,String prefix) throws IllegalArgumentException {
		try {
			if (inDirectory!=null) {
				exists("Directory", inDirectory);
				if (inDirectory.isDirectory()) {
					File result = Files.createTempDirectory(inDirectory.toPath(), prefix).toFile();
					exists("Directory", result);
					files.add(result);
					return result;
				} else {
					throw new IllegalArgumentException("InDirectory ["+inDirectory.getAbsolutePath()+"] is not a valid directory.");
				}
			} else {
				File result = Files.createTempDirectory(prefix).toFile();
				exists("Directory", result);
				files.add(result);
				return result;
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
	}
	
	public File createTempFile() throws IllegalArgumentException {
		return createTempFile(null,"tmpfile",null);
	}	
	public File createTempFile(String extension) throws IllegalArgumentException {
		return createTempFile(null,"tmpfile",extension);
	}
	public File createTempFile(String prefix,String extension) throws IllegalArgumentException {
		return createTempFile(null,prefix,extension);
	}
	public File createTempFile(File inDirectory) throws IllegalArgumentException {
		return createTempFile(inDirectory,".tmp");
	}
	public File createTempFile(File inDirectory,String extension) throws IllegalArgumentException {
		return createTempFile(inDirectory,"tmpfile",extension);
	}	
	public File createTempFile(File inDirectory,String prefix,String extension) throws IllegalArgumentException {
		try {
			if (extension!=null) {
				if (!extension.startsWith(".")) {
					extension = "."+extension;
				}
			}
			if (inDirectory==null) {
				File result = File.createTempFile(prefix, extension);
				exists("File", result);
				files.add(result);
				return result;
			} else {
				exists("Directory", inDirectory);
				if (inDirectory.isDirectory()) {
					File result = File.createTempFile(prefix, extension,inDirectory);
					exists("File", result);
					files.add(result);
					return result;
				} else {
					throw new IllegalArgumentException("InDirectory ["+inDirectory.getAbsolutePath()+"] is not a valid directory.");
				}
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
	}
	public void writeContent(File target,File contentFile) throws IllegalArgumentException {
		exists("File", target);
		exists("File", contentFile);	
		try {
			byte[] content = Files.readAllBytes(contentFile.toPath());
			writeContent(target, content);
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
	}
	public void writeContent(File target,byte[] content) throws IllegalArgumentException {
		exists("File", target);
		try {
			Files.write(target.toPath(), content);
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
	}	
	public void writeContent(File target,String content) throws IllegalArgumentException {
		writeContent(target,content.getBytes());
	}
	public void writeContent(File target,String content,Charset charset) throws IllegalArgumentException {
		writeContent(target,content.getBytes(charset));
	}
	
	public String loadContent(File source) throws IllegalArgumentException {
		exists("File", source);
		try {
			return new String(Files.readAllBytes(source.toPath()));
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(),e);
		}
	}

	public File getFile(String filename) {
		for (File file : files) {
			if ((file.getName().equalsIgnoreCase(filename)) && (file.isFile())) {
				return file;
			}
		}
		return null;
	}
	public File getDirectory(String name) {
		for (File file : files) {		
			if ((file.getName().equalsIgnoreCase(name)) && (file.isDirectory())) {
				return file;
			}
		}
		return null;
	}
	public int getDirectorySize(String name) {
		File dir = getDirectory(name);
		if (dir==null) {
			return 0;
		} else if (dir.exists()) {
			if (dir.isFile()) {
				return 0;
			} else if (dir.isDirectory()) {
				return dir.list().length;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public boolean doTempFileExists(String fileNameStartWith) {
		File file = getTempFile(fileNameStartWith);
		if (file!=null) {
			return file.exists();
		} else {
			return false;
		}
	}
	public boolean doTempFileExists(String fileNameStartWith,String extension) {
		File file = getTempFile(fileNameStartWith,extension);
		if (file!=null) {
			return file.exists();
		} else {
			return false;
		}
	}
	public File getTempFile(String fileNameStartWith) {
		for (File file : files) {
			if ((file.getName().toLowerCase().trim().startsWith(fileNameStartWith.toLowerCase().trim())) && 
				(file.isFile())) {
				return file;
			}
		}
		return null;
	}
	
	public File getTempFile(String fileNameStartWith,String extension) {
		for (File file : files) {
			if ((file.getName().toLowerCase().trim().startsWith(fileNameStartWith.toLowerCase().trim())) && 
				(file.getName().toLowerCase().trim().endsWith(extension.toLowerCase().trim())) && 
				(file.isFile())) {
				return file;
			}
		}
		return null;
	}
	
	public File getTempDirectory(String name) {
		for (File file : files) {
			if ((file.getName().startsWith(name)) && (file.isDirectory())) {
				return file;
			}
		}
		return null;
	}
	
	public boolean existsFile(String filename) {
		return getFile(filename)!=null;
	}
	public boolean existsDirectory(String name) {		
		return getDirectory(name)!=null;
	}
	
	public boolean existsTempFile(String filename) {
		return getTempFile(filename)!=null;
	}
	public boolean existsTempDirectory(String name) {
		return getTempDirectory(name)!=null;
	}	
	
	
	public void setFileAgeInHours(File file,int hours) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, -(Math.abs(hours)));
		file.setLastModified(c.getTimeInMillis());
	}
	
	public void setFileAgeInDays(File file,int days) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -(Math.abs(days)));
		file.setLastModified(c.getTimeInMillis());
	}
	
	
	private void exists(String type,File file) throws IllegalArgumentException {
		if (file!=null) {
			if (!file.exists()) {
				throw new IllegalArgumentException(type+" ["+file.getAbsolutePath()+"] do not exist.");
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("TempFileManager size ["+size()+"] :\n");
		for (File f : files) {
			if (f.isDirectory()) {
				s.append(" - Dir ["+f.getAbsolutePath()+"] length ["+f.list().length+"].\n");
			} else {
				s.append(" - Fil ["+f.getAbsolutePath()+"] size ["+f.length()+"].\n");
			}
		}
		return s.toString();
	}
	
	public void toString(FileStructure fs) {
		
	}
	public void toString(FileStructure oldFs,FileStructure newFs) {
		
	}
	
	public FileStructure getFileStructure(File path) {
		FileStructure fs = new FileStructure(path);
		if ((fs.exists()) && (fs.isDirectory())) {
			for (File f : fs.getPath().listFiles()) {
				if (f.isFile()) {
					fs.add(f);
				} else {
					FileStructure child = fs.add(f);
					getFileStructure(f, child);
				}
			}
		}
		return fs;
	}
	public FileStructure getFileStructure(File path,FileStructure parent) {		
		if ((parent.exists()) && (parent.isDirectory())) {
			for (File f : parent.getPath().listFiles()) {
				if (f.isFile()) {
					parent.add(f);
				} else {
					FileStructure child = parent.add(f);
					getFileStructure(f, child);
				}
			}
		}
		return parent;
	}	
	
	class FileStructure {
		
		private File path;
		private List<FileStructure> files = new ArrayList<FileStructure>();
		
		public FileStructure(File path) {
			this.path = path;
		}
		public FileStructure add(File file) {
			FileStructure fs = new FileStructure(file);
			files.add(fs);
			return fs;
		}		
		public File getPath() {
			return path;
		}
		public List<FileStructure> getFiles() {
			return files;
		}
		public boolean isDirectory() {
			return exists() && getPath().isDirectory();
		}
		public boolean isFile() {
			return exists() && getPath().isFile(); 
		}
		public boolean exists() {
			return getPath()!=null && getPath().exists();
		}
		public int size() {
			return files.size();
		}
		
		
	}
}
