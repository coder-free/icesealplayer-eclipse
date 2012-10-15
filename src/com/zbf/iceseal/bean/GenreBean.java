package com.zbf.iceseal.bean;

import org.json.JSONObject;

import com.zbf.iceseal.base.BaseBean;

public class GenreBean extends BaseBean {
	public GenreBean() {
	}
	public GenreBean(JSONObject jsonObject) throws Exception {
		super(jsonObject);
	}
	private String genre;
	private Integer count;
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
