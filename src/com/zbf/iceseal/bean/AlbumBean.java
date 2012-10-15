package com.zbf.iceseal.bean;

import org.json.JSONObject;

import com.zbf.iceseal.base.BaseBean;

public class AlbumBean extends BaseBean {
	public AlbumBean() {
	}
	public AlbumBean(JSONObject jsonObject) throws Exception {
		super(jsonObject);
	}
	private String album;
	private Integer count;
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
