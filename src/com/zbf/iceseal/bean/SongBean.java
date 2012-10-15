package com.zbf.iceseal.bean;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.zbf.iceseal.base.BaseBean;

public class SongBean extends BaseBean implements Parcelable {
	public SongBean() {
	}
	public SongBean(JSONObject jsonObject) throws Exception {
		super(jsonObject);
	}
	private Integer id;
	private String name;
	private String genre;
	private String album;
	private String artist;
	private String path;
	private Long duration;
	private Integer islike;
	private Integer listid;
	private Integer folderid;
	private String createtime;
	private String lastplaytime;
	private Long size;
	private String namespell;
	private Integer playcount;
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
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public Integer getIslike() {
		return islike;
	}
	public void setIslike(Integer islike) {
		this.islike = islike;
	}
	public Integer getListid() {
		return listid;
	}
	public void setListid(Integer listid) {
		this.listid = listid;
	}
	public Integer getFolderid() {
		return folderid;
	}
	public void setFolderid(Integer folderid) {
		this.folderid = folderid;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getLastplaytime() {
		return lastplaytime;
	}
	public void setLastplaytime(String lastplaytime) {
		this.lastplaytime = lastplaytime;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public Integer getPlaycount() {
		return playcount;
	}
	public void setPlaycount(Integer playcount) {
		this.playcount = playcount;
	}
	public String getNamespell() {
		return namespell;
	}
	public void setNamespell(String namespell) {
		this.namespell = namespell;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(genre);
		dest.writeString(album);
		dest.writeString(artist);
		dest.writeString(path);
		dest.writeString(duration + "");
		dest.writeString(islike + "");
		dest.writeString(listid + "");
		dest.writeString(folderid + "");
		dest.writeString(createtime);
		dest.writeString(lastplaytime);
		dest.writeString(size + "");
		dest.writeString(namespell);
	}
	public static final Creator<SongBean> CREATOR = new Creator<SongBean>() {

		@Override
		public SongBean createFromParcel(Parcel source) {
			SongBean parcelSongBean = new SongBean();
			parcelSongBean.id = source.readInt();
			parcelSongBean.name = source.readString();
			parcelSongBean.genre = source.readString();
			parcelSongBean.album = source.readString();
			parcelSongBean.artist = source.readString();
			parcelSongBean.path = source.readString();
			try {
				parcelSongBean.duration = Long.parseLong(source.readString());
			} catch (NumberFormatException e) {
				parcelSongBean.duration = null;
			}
			try {
				parcelSongBean.islike = Integer.parseInt(source.readString());
			} catch (NumberFormatException e) {
				parcelSongBean.islike = null;
			}
			try {
				parcelSongBean.listid = Integer.parseInt(source.readString());
			} catch (NumberFormatException e) {
				parcelSongBean.listid = null;
			}
			try {
				parcelSongBean.folderid = Integer.parseInt(source.readString());
			} catch (NumberFormatException e) {
				parcelSongBean.folderid = null;
			}
			parcelSongBean.createtime = source.readString();
			parcelSongBean.lastplaytime = source.readString();
			try {
				parcelSongBean.size = Long.parseLong(source.readString());
			} catch (NumberFormatException e) {
				parcelSongBean.size = null;
			}
			parcelSongBean.namespell = source.readString();
			return parcelSongBean;
		}

		@Override
		public SongBean[] newArray(int size) {
			return new SongBean[size];
		}
	};
}
