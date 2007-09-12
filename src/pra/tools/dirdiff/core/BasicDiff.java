/**
 * @(#)BasicDiff.java
 */
package pra.tools.dirdiff.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Compute the basic diff between two given directories.
 *
 * First directory is treated as fresh copy
 * Second directory is treated as old copy
 *
 * If there are changes in files that exist in both the copies,
 * the file from first directory is copied to output directory.
 *
 * If there is a new file in first directory it is added to output directory.
 *
 * If there is a file in second directory but not in first directory,
 * the file copy is ignored.
 *
 * You can use this basically to create patch for your installation and 
 * update server.
 *
 * @author prasad
 *
 */
public class BasicDiff {
	public static void main(String[] args) throws IOException {
		
		if(args == null || args.length < 2) {
			System.out.println("Usage: java " + BasicDiff.class.getName() + " fresh_copy old_copy [output_dir]");
			return;
		}
		
		File dir1 = new File(args[0]);
		File dir2 = new File(args[1]);
		
		File dir3 = null;
		if(args.length > 2) {
			dir3 = new File(args[2]);
			if(!dir3.exists()) dir3.mkdirs();
		} else {
			dir3 = new File(dir2.getParentFile(), "output-" + System.currentTimeMillis());
		}
		
		File[] dir1files = dir1.listFiles();
		File[] dir2files = dir2.listFiles();
		
		List sourcepaths = FileUtils.getFilePaths(dir1, dir1files);
		List targetpaths = FileUtils.getFilePaths(dir2, dir2files);
		
		for(int index = 0; index < targetpaths.size(); ++index) {
			String targetpath = (String) targetpaths.get(index);
			if(sourcepaths.contains(targetpath)) {
				if(!FileUtils.hasFileChanges(dir2, targetpath, dir1, targetpath)) {
					sourcepaths.remove(targetpath);
				}
			}
		}
		
		// Copy new version of old files.
		for(int index = 0; index < sourcepaths.size(); ++index) {
			String sourcepath = (String) sourcepaths.get(index);
			FileUtils.copyFile(dir1, sourcepath, dir3);
		}
	}	
}
