/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zbf.iceseal.lyric;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zbf.iceseal.bean.SongBean;

/**
 * 表示一首歌的歌词对象,它可以以某种方式来画自己
 * @author hadeslee
 */
@SuppressWarnings("unused")
public class Lyric {

	private int width;//表示歌词的显示区域的宽度
    private int height;//表示歌词的显示区域的高度
    private long time;//表示当前的时间是多少了。以毫秒为单位
    private long tempTime;//表示一个暂时的时间,用于拖动的时候,确定应该到哪了
    private List<Sentence> list = new ArrayList<Sentence>();//里面装的是所有的句子
    private int currentIndex;//当前正在显示的歌词的下标
    private boolean initDone;//是否初始化完毕了
    private transient SongBean info;//有关于这首歌的信息
    private transient File file;//该歌词所存在文件
    private long during = Integer.MAX_VALUE;//这首歌的长度
    /**
     * 用歌曲bean来初始化歌词
     */
    public Lyric(final SongBean info) {
        this.info = info;
        this.during = info.getDuration();
        this.file = new File(LyricsTools.getLrcPath(info));
        //只要有关联好了的，就不用搜索了直接用就是了
        if (file.exists()) {
            init(file);
            initDone = true;
            return;
        } else {
        	init(info);
            initDone = true;
        	return;
//        	System.out.println("另起线程找歌词");
//            //否则就起一个线程去找了，先是本地找，然后再是网络上找
//            new Thread() {
//                public void run() {
//                    doInit(info);
//                    initDone = true;
//                }
//            }.start();
        }

    }

    /**
     * 读取某个指定的歌词文件,这个构造函数一般用于
     * 用户指定某个歌词文件
     * @param file 歌词文件
     * @param info 歌曲信息
     */
    public Lyric(File file, SongBean info) {
        this.file = file;
        this.info = info;
        init(file);
        initDone = true;
    }

    /**
     * 根据歌词内容和播放项构造一个
     * 歌词对象
     * @param lyric 歌词内容
     * @param info 播放项
     */
    public Lyric(String lyric, SongBean info) {
        this.info = info;
        this.init(lyric);
        initDone = true;
    }

    private void doInit(SongBean info) {
        init(info);
        Sentence temp = null;
        //这个时候就要去网络上找了
        if (list.size() == 1) {
            System.out.println("本地文件夹没找到");
            temp = list.remove(0);
            try {
            	System.out.println("去网络上找");
//                String lyric = LyricsTools.getLyric(info);
            	String lyric = null;
                if (lyric != null) {
                	System.out.println("网络上找到了");
                    init(lyric);
                    saveLyric(lyric, info);
                } else {//如果网络也没有找到,就要加回去了
                	System.out.println("网络上没找到");
                    list.add(temp);
                }
            } catch (Exception e) {
                System.out.println(e);
                list.add(temp);
            }
        }
    }

    /**
     * 把下载到的歌词保存起来,免得下次再去找
     * @param lyric 歌词内容
     * @param info 歌的信息
     */
    private void saveLyric(String lyric, SongBean info) {
        try {
            //如果歌手不为空,则以歌手名+歌曲名为最好组合
            file = new File(LyricsTools.getLrcPath(info));
            if(file.exists()) {
            	file = new File(file.getPath().replace(".lrc", "2.lrc"));
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "GBK"));
            bw.write(lyric);
            bw.close();
        } catch (Exception e) {
        	System.out.println(e);
        }
    }

    /**
     * 得到此歌词保存的地方
     * @return 文件
     */
    public File getLyricFile() {
        return file;
    }

    /**
     * 调整整体的时间,比如歌词统一快多少
     * 或者歌词统一慢多少,为正说明要快,为负说明要慢
     * @param time 要调的时间,单位是毫秒
     */
    public void adjustTime(int time) {
        //如果是只有一个显示的,那就说明没有什么效对的意义了,直接返回
        if (list.size() == 1) {
            return;
        }
        for (Sentence s : list) {
            s.setFromTime(s.getFromTime() - time);
            s.setToTime(s.getToTime() - time);
        }
    }

    /**
     * 根据一个文件夹,和一个歌曲的信息
     * 从本地搜到最匹配的歌词
     * @param dir 目录
     * @param info 歌曲信息 
     * @return 歌词文件
     */
    private File getMathedLyricFile(SongBean info) {
        File matched = null;//已经匹配的文件
        File dir = new File(info.getPath().substring(0, info.getPath().lastIndexOf("/")));
        File[] fs = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".lrc");
            }
        });
        for (File f : fs) {
            //全部匹配或者部分匹配都行
            if (matchAll(info, f) || matchSongName(info, f)) {
                matched = f;
                break;
            }
        }
        return matched;
    }

    /**
     * 根据歌的信息去初始化,这个时候
     * 可能在本地找到歌词文件,也可能要去网络上搜索了
     * @param info 歌曲信息
     */
    private void init(SongBean info) {
        File matched = null;
        matched = getMathedLyricFile(info);
        if (matched != null && matched.exists()) {
//            info.setLyricFile(matched);
            file = matched;
            init(matched);
        } else {
            init("");
        }
    }

    /**
     * 根据文件来初始化
     * @param file 文件
     */
    private void init(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp).append("\n");
            }
            init(sb.toString());
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
            	System.out.println(e);
            }
        }
    }

    /**
     * 是否完全匹配,完全匹配是指直接对应到ID3V1的标签,
     * 如果一样,则完全匹配了,完全匹配的LRC的文件格式是:
     * 阿木 - 有一种爱叫放手.lrc
     * @param info 歌曲信息
     * @param file 侯选文件
     * @return 是否合格
     */
    private boolean matchAll(SongBean info, File file) {
        String name = info.getName();
        String fn = file.getName().substring(0, file.getName().lastIndexOf("."));
        if (name.equals(fn)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否匹配了歌曲名
     * @param info 歌曲信息
     * @param file 侯选文件
     * @return 是否合格
     */
    private boolean matchSongName(SongBean info, File file) {
        String name = info.getName();
        String rn = file.getName().substring(0, file.getName().lastIndexOf("."));
        if (name.equalsIgnoreCase(rn) || info.getName().equalsIgnoreCase(rn)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 最重要的一个方法，它根据读到的歌词内容
     * 进行初始化，比如把歌词一句一句分开并计算好时间
     * @param content 歌词内容
     */
    private void init(String content) {
        //如果歌词的内容为空,则后面就不用执行了
        //直接显示歌曲名就可以了
        if (content == null || content.trim().equals("")) {
            list.add(new Sentence(info.getName() + " (没有歌词)", Integer.MIN_VALUE, Integer.MAX_VALUE));
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new StringReader(content));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                parseLine(temp.trim());
            }
            br.close();
            //读进来以后就排序了
            Collections.sort(list, new Comparator<Sentence>() {
                public int compare(Sentence o1, Sentence o2) {
                    return (int) (o1.getFromTime() - o2.getFromTime());
                }
            });
            //处理第一句歌词的起始情况,无论怎么样,加上歌名做为第一句歌词,并把它的
            //结尾为真正第一句歌词的开始
            if (list.size() == 0) {
                list.add(new Sentence(info.getName(), 0, Integer.MAX_VALUE));
                return;
            } else {
                Sentence first = list.get(0);
                list.add(0, new Sentence(info.getName(), 0, first.getFromTime()));
            }

            int size = list.size();
            for (int i = 0; i < size; i++) {
                Sentence next = null;
                if (i + 1 < size) {
                    next = list.get(i + 1);
                }
                Sentence now = list.get(i);
                if (next != null) {
                    now.setToTime(next.getFromTime() - 1);
                }
            }
            //如果就是没有怎么办,那就只显示一句歌名了
            if (list.size() == 1) {
                list.get(0).setToTime(Integer.MAX_VALUE);
            } else {
                Sentence last = list.get(list.size() - 1);
                last.setToTime(info == null ? Integer.MAX_VALUE : LyricsTools.getDuring(info.getPath()) + 1000);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * 分析这一行的内容，根据这内容
     * 以及标签的数量生成若干个Sentence对象
     * @param line 这一行
     */
    private void parseLine(String line) {
        if (line.equals("")) {
            return;
        }
        Matcher m = Pattern.compile("(?<=\\[).*?(?=\\])").matcher(line);
        List<String> temp = new ArrayList<String>();
        int length = 0;
        while (m.find()) {
            String s = m.group();
            temp.add(s);
            length += (s.length() + 2);
        }
        try {
            String content = line.substring(length > line.length() ? line.length() : length);
            if (LyricsTools.LRC_isCutBlankChars) {
                content = content.trim();
            }
            if (content.equals("")) {
                return;
            }
            for (String s : temp) {
                long t = parseTime(s);
                if (t != -1) {
                    list.add(new Sentence(content, t));
                }
            }
        } catch (Exception exe) {
        }
    }

    /**
     * 把如00:00.00这样的字符串转化成
     * 毫秒数的时间，比如 
     * 01:10.34就是一分钟加上10秒再加上340毫秒
     * 也就是返回70340毫秒
     * @param time 字符串的时间
     * @return 此时间表示的毫秒
     */
    private long parseTime(String time) {
        String[] ss = time.split("\\:|\\.");
        //如果 是两位以后，就非法了
        if (ss.length < 2) {
            return -1;
        } else if (ss.length == 2) {//如果正好两位，就算分秒
            try {
                int min = Integer.parseInt(ss[0]);
                int sec = Integer.parseInt(ss[1]);
                if (min < 0 || sec < 0 || sec >= 60) {
                    throw new RuntimeException("数字不合法!");
                }
                return (min * 60 + sec) * 1000L;
            } catch (Exception exe) {
                return -1;
            }
        } else if (ss.length == 3) {//如果正好三位，就算分秒，十毫秒
            try {
                int min = Integer.parseInt(ss[0]);
                int sec = Integer.parseInt(ss[1]);
                int mm = Integer.parseInt(ss[2]);
                if (min < 0 || sec < 0 || sec >= 60 || mm < 0 || mm > 99) {
                    throw new RuntimeException("数字不合法!");
                }
                return (min * 60 + sec) * 1000L + mm * 10;
            } catch (Exception exe) {
                return -1;
            }
        } else {//否则也非法
            return -1;
        }
    }

    /**
     * 设置其显示区域的高度
     * @param height 高度
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 设置其显示区域的宽度
     * @param width 宽度
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 设置时间
     * @param time 时间
     */
    public void setTime(long time) {
    	tempTime = this.time = time;
    }

    /**
     * 得到是否初始化完成了
     * @return 是否完成
     */
    public boolean isInitDone() {
        return initDone;
    }
    
    /**
     * 得到当前正在播放的那一句的下标
     * 不可能找不到，因为最开头要加一句
     * 自己的句子 ，所以加了以后就不可能找不到了
     * @return 下标
     */
    public int getNowSentenceIndex(long t) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isInTime(t)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 得到当前正在播放的那一句
     * 不可能找不到，因为最开头要加一句
     * 自己的句子 ，所以加了以后就不可能找不到了
     * @return 当前那一句歌词
     */
    public Sentence getNowSentence(long t) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isInTime(t)) {
                return list.get(i);
            }
        }
        return null;
    }
    
    /**
     * 得到当前正在播放的那一句
     * 不可能找不到，因为最开头要加一句
     * 自己的句子 ，所以加了以后就不可能找不到了
     * @return 当前那一句歌词
     */
    public Sentence getNowSentence(int index) {
        return list.size() > index && index > -1 ? list.get(index) : null;
    }
}
