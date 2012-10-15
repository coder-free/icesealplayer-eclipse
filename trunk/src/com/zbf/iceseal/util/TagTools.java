package com.zbf.iceseal.util;

import java.io.IOException;

import com.zbf.iceseal.bean.SongBean;
import com.zbf.iceseal.tag.ape.APEv2Tag;
import com.zbf.iceseal.tag.id3.ID3v1;
import com.zbf.iceseal.tag.id3.ID3v2;
import com.zbf.iceseal.tag.id3.InvalidDataException;
import com.zbf.iceseal.tag.id3.Mp3File;
import com.zbf.iceseal.tag.id3.UnsupportedTagException;

public class TagTools {

	public static SongBean getApeV2Tag(String songPath) throws IOException {
		SongBean songBean = null;
		APEv2Tag ape = new APEv2Tag(songPath);
		if(ape.hasTag()) {
			songBean = new SongBean();
			String name = ape.getFirstTitle();
			if(name == null || "null".equals(name) || "".equals(name)) {
				name = songPath.substring(songPath.lastIndexOf('/') + 1, songPath.lastIndexOf('.'));
			}
			songBean.setName(name);
			String artist = ape.getFirstArtist();
			if(artist == null || "null".equals(name) || "".equals(artist)) {
				artist = "<未知>";
			}
			songBean.setArtist(artist);
			String genre = ape.getFirstGenre();
			if(genre == null || "null".equals(name) || "".equals(genre)) {
				genre = "<未知>";
			}
			songBean.setGenre(genre);
			String album = ape.getFirstAlbum();
			if(album == null || "null".equals(name)|| "".equals(album)) {
				album = "<未知>";
			}
			songBean.setAlbum(album);
			songBean.setPath(songPath);
		}
		return songBean;
	}
	
	public static SongBean getId3V2Tag(String songPath) throws UnsupportedTagException, InvalidDataException, IOException {
		SongBean songBean = null;
		ID3v2 id3V2 = new Mp3File(songPath).getId3v2Tag();
		if(id3V2 != null) {
			songBean = new SongBean();
			String name = id3V2.getTitle();
			if(name == null || "null".equals(name) || "".equals(name)) {
				name = songPath.substring(songPath.lastIndexOf('/') + 1, songPath.lastIndexOf('.'));
			}
			songBean.setName(name);
			String artist = id3V2.getArtist();
			if(artist == null || "null".equals(name) || "".equals(artist)) {
				artist = "<未知>";
			}
			songBean.setArtist(artist);
			String genre = id3V2.getGenre() + "";
			if(genre == null || "null".equals(name) || "".equals(genre)) {
				genre = "<未知>";
			}
			songBean.setGenre(genre);
			String album = id3V2.getAlbum();
			if(album == null || "null".equals(name) || "".equals(album)) {
				album = "<未知>";
			}
			songBean.setAlbum(album);
			songBean.setPath(songPath);
		}
		return songBean;
	}
	
	public static SongBean getId3V1Tag(String songPath) throws UnsupportedTagException, InvalidDataException, IOException {
		SongBean songBean = null;
		ID3v1 id3V1 = new Mp3File(songPath).getId3v1Tag();
		if(id3V1 != null) {
			songBean = new SongBean();
			String name = id3V1.getTitle();
			if(name == null || "null".equals(name) || "".equals(name)) {
				name = songPath.substring(songPath.lastIndexOf('/') + 1, songPath.lastIndexOf('.'));
			}
			songBean.setName(name);
			String artist = id3V1.getArtist();
			if(artist == null || "null".equals(name) || "".equals(artist)) {
				artist = "<未知>";
			}
			songBean.setArtist(artist);
			String genre = id3V1.getGenreDescription();
			if(genre == null || "null".equals(name) || "".equals(genre)) {
				genre = "<未知>";
			}
			songBean.setGenre(genre);
			String album = id3V1.getAlbum();
			if(album == null || "null".equals(name) || "".equals(album)) {
				album = "<未知>";
			}
			songBean.setAlbum(album);
			songBean.setPath(songPath);
		}
		return songBean;
	}
	
	public static SongBean getDefaultTag(String songPath) {
		SongBean songBean = new SongBean();
		songBean.setName(songPath.substring(songPath.lastIndexOf('/') + 1, songPath.lastIndexOf('.')));
		songBean.setArtist("<未知>");
		songBean.setGenre("<未知>");
		songBean.setAlbum("<未知>");
		songBean.setPath(songPath);
		return songBean;
	}
	
}
