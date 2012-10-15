package com.zbf.iceseal.bean;

import java.util.List;


public class SonglistBean {

	private String titleName;
	private String titleType;
	private List<SongBean> songList;
	private Integer position;
	public String getTitleName() {
		return titleName;
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public List<SongBean> getSongList() {
		return songList;
	}
	public void setSongList(List<SongBean> songList) {
		this.songList = songList;
	}
	public String getTitleType() {
		return titleType;
	}
	public void setTitleType(String titleType) {
		this.titleType = titleType;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
}
