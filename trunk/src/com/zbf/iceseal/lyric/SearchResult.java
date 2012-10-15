/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zbf.iceseal.lyric;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.zbf.iceseal.bean.SongBean;

/**
 * 搜索到的结果的对象表示法
 * @author hadeslee
 */
public class SearchResult {

    private String artist;//表示结果的歌手名
    private String title;//表示结果的标题 
    private Task task;//下载歌词的任务
    private String content;

    public static interface Task {

        public String getLyricContent() throws IOException;
    }

    public SearchResult(String artist, String title, Task task) {
        this.artist = artist;
        this.title = title;
        this.task = task;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void save(SongBean song) throws IOException {
        content = task.getLyricContent();
        BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                new FileOutputStream(new File(LyricsTools.getLrcPath(song)))));
        bw.write(content);
        bw.close();
    }

    public String toString() {
        return artist + ":" + title;
    }
}
