package com.zbf.iceseal.base;

public class Config {
	private static Config config;
	public int oftenPlayCount = 30;
	public boolean LRC_isCutBlankChars = false;
	public boolean isUseProxy = false;
	public int V_SPACE;
	private Config(){
		
	}
	public static Config getConfig() {
		if(config == null) {
			config = new Config();
		}
		return config;
	}
	public boolean isCutBlankChars() {
		return LRC_isCutBlankChars;
	}
	public boolean isUseProxy() {
		return isUseProxy;
	}
	public int getV_SPACE() {
		return V_SPACE;
	}
}
