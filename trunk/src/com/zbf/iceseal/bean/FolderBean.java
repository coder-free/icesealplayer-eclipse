package com.zbf.iceseal.bean;

import org.json.JSONObject;

import com.zbf.iceseal.base.BaseBean;

public class FolderBean extends BaseBean {
	public FolderBean() {
	}
	public FolderBean(JSONObject jsonObject) throws Exception {
		super(jsonObject);
	}
	private Integer id;
	private String name;
	private String path;
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
