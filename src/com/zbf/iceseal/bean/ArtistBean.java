package com.zbf.iceseal.bean;

import org.json.JSONObject;

import com.zbf.iceseal.base.BaseBean;

public class ArtistBean extends BaseBean {
	public ArtistBean() {
	}
	public ArtistBean(JSONObject jsonObject) throws Exception {
		super(jsonObject);
	}
	private String artist;
	private Integer count;
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
