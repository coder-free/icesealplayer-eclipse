package com.zbf.iceseal.bean;

import org.json.JSONObject;

import com.zbf.iceseal.base.BaseBean;

public class PlaylistBean extends BaseBean {
	public PlaylistBean() {
	}
	public PlaylistBean(JSONObject jsonObject) throws Exception {
		super(jsonObject);
	}
	private Integer id;
	private String name;
	private Integer sort;
	private Integer count;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
