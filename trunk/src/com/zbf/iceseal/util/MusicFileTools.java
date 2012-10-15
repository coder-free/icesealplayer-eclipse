package com.zbf.iceseal.util;

import java.io.File;

public class MusicFileTools {
	
	public static String[] suffixs= new String[]{".mp3"};
	
	public static boolean isMusicFile(File file) {
		if(file.isFile()) {
			for(String suffix : suffixs)
			if(file.getName().toLowerCase().endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}
}
