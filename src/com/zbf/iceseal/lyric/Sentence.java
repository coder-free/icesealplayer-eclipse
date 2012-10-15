/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zbf.iceseal.lyric;


/**
 * 一个用来表示每一句歌词的类
 * 它封装了歌词的内容以及这句歌词的起始时间
 * 和结束时间，还有一些实用的方法
 * @author hadeslee
 */
public class Sentence {

    private long fromTime;//这句的起始时间,时间是以毫秒为单位
    private long toTime;//这一句的结束时间
    private String content;//这一句的内容
    public Sentence(String content, long fromTime, long toTime) {
        this.content = content;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public Sentence(String content, long fromTime) {
        this(content, fromTime, 0);
    }

    public Sentence(String content) {
        this(content, 0, 0);
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    /**
     * 检查某个时间是否包含在某句中间
     * @param time 时间
     * @return 是否包含了
     */
    public boolean isInTime(long time) {
        return time >= fromTime && time <= toTime;
    }

    /**
     * 得到这一句的内容
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 得到这个句子的时间长度,毫秒为单位
     * @return 长度
     */
    public long getDuring() {
        return toTime - fromTime;
    }

    /**
     * 根据当前指定的时候,得到这个时候应该
     * 取渐变色的哪个阶段了,目前的算法是从
     * 快到结束的五分之一处开始渐变,这样平缓一些
     * @param color1 高亮色
     * @param color2 普通色
     * @param time 时间
     * @return 新的颜色
     */
    public int getBestInColor(int color1, int color2, long time) {
        float f = (time - fromTime) * 1.0f / getDuring();
        if (f > 0.1f) {//如果已经过了十分之一的地方,就直接返高亮色
            return color1;
        } else {
            long dur = getDuring();
            f = (time - fromTime) * 1.0f / (dur * 0.1f);
            if (f > 1 || f < 0) {
                return color1;
            }
            return LyricsTools.getGradientColor(color2, color1, f);
        }
    }

    public String toString() {
        return "{" + fromTime + "(" + content + ")" + toTime + "}";
    }
}
