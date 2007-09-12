/**
 * @(#)FileUtils.java
 */
package pra.tools.dirdiff.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jlibdiff.Diff;

/**
 * @author prasad
 *
 */
public class FileUtils {
	public static boolean hasFileChanges(File sourcebase, String source, 
			File targetbase, String target) throws IOException {
		
		File sfile = new File(sourcebase, source);
		File tfile = new File(targetbase, target);
		
		if(sfile.exists() && !tfile.exists()) return true;
		if(!sfile.exists() && tfile.exists()) return true;
		
		BufferedReader br1 = new BufferedReader(new FileReader(sfile));
		BufferedReader br2 = new BufferedReader(new FileReader(tfile));
		
		Diff diff = new Diff();
		diff.diff(br1, br2);
		
		return (diff.getHunks().size() > 0);
	}
	
	public static List getFilePaths(File base, File[] files) throws IOException {
		List flist = new ArrayList();
		for(int index = 0; files != null && index < files.length; ++index) {
			File file = files[index];
			
			if(file.isFile()) flist.add(getRelativePath(base, file));
			else if(file.isDirectory()) {
				//flist.add(getRelativePath(base, file));
				flist.addAll(getFilePaths(base, file.listFiles()));			
			}
		}		
		return flist;
	}
	
	public static String getRelativePath(File base, File file) throws IOException {
		String basePath = base.getCanonicalPath();
		String filePath = file.getCanonicalPath();
		
		return getRelativePath(basePath, filePath);
	}
	public static String getRelativePath(String basePath, String filePath) {
        if (basePath.equals(filePath))
            return "";

        // Lets consider one type of File Separator.
        basePath = normalizePath(basePath);
        filePath = normalizePath(filePath);

        int beginIndex = filePath.indexOf(basePath);
        if (beginIndex == -1)
            return filePath;

        beginIndex += basePath.length() + 1;

        return filePath.substring(beginIndex);
    }
	public static String normalizePath(String path) {
        String separator = "/";
        return path.replaceAll("\\\\", separator);
    }
	public static String getExtension(String filename) {
    	int lastDotIndex = filename.lastIndexOf('.');
    	if(lastDotIndex == -1) return "";
    	return filename.substring(lastDotIndex);
    }
	
	public static void copyFile(File base, String relfilepath, File targetdir) throws IOException {
		File sourcefile = new File(base, relfilepath);
		File targetfile = new File(targetdir, relfilepath);
		if(!targetfile.exists()) {
			File parentfile = sourcefile.getParentFile();
			String relpfilepath = getRelativePath(base, parentfile);
			File targetfiledir = new File(targetdir, relpfilepath);
			if(!targetfiledir.exists()) targetfiledir.mkdirs();
			targetfile = new File(targetdir, relfilepath);
		}
		copyFile(sourcefile, targetfile);
	}
	
	public static void copyFile(File src, File target) throws IOException {
		if(!target.exists()) target.createNewFile();
		
		if (!src.isFile() || !target.isFile())
            return;
		
        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(target);

        streamIO(fis, fos);

        fos.close();
        fis.close();
    }
	public static void streamIO(InputStream is, OutputStream os)
    throws IOException {
		byte[] bytes = new byte[2048];
		int readlen = -1;
		while ((readlen = is.read(bytes)) != -1) {
		    os.write(bytes, 0, readlen);
		}
	}
}
