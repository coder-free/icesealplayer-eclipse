package com.zbf.iceseal.util;

import java.io.File;

public class FileTools {
	public static boolean filterFile(File file, String[] suffixs) {
		for(String suffix : suffixs) {
			if(file.isDirectory()) {
				File[] files = file.listFiles();
				if(files == null) {
					return false;
				}
				for(File subFile : files) {
					return filterFile(subFile, suffixs);
				}
			} else if(file.isFile() && file.getName().toLowerCase().endsWith(suffix)){
				return true;
			}
		}
		return false;
	}
}
