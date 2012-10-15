package com.zbf.iceseal.lyric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

import com.zbf.iceseal.bean.SongBean;

public class LyricsTools {

	public static int oftenPlayCount = 30;
	public static boolean LRC_isCutBlankChars = false;
	
	public static String getLrcPath(SongBean song) {
		String lrcName;
		if(song.getArtist() != null && !"".equals(song.getArtist()) && !"<空>".equals(song.getArtist())) {
			lrcName = song.getArtist() + " - " + song.getName() + ".lrc";
		} else {
			lrcName = song.getName().substring(0, song.getName().lastIndexOf('.') == -1 ? song.getName().length() : song.getName().lastIndexOf('.')) + ".lrc";
		}
		return song.getPath().substring(0, song.getPath().lastIndexOf("/") + 1) + lrcName;
	}
	
	public static long getDuring(String songPath) {
		return 0;
	}

    /**
     * 根据一个比例得到两种颜色之间的渐变色
     * @param c1 第一种颜色
     * @param c2 第二种颜色
     * @param f 比例
     * @return 新的颜色
     */
    public static int getGradientColor(int c1, int c2, float f) {
        int deltaR = Color.red(c2) - Color.red(c1);
        int deltaG = Color.green(c2) - Color.green(c1);
        int deltaB = Color.blue(c2) - Color.blue(c1);
        int r1 = (int) (Color.red(c1) + f * deltaR);
        int g1 = (int) (Color.green(c1) + f * deltaG);
        int b1 = (int) (Color.blue(c1) + f * deltaB);
        int c = Color.rgb(r1, g1, b1);       
        return c;
    }
    

    /**
     * 一个简单的方法,得到传进去的歌手和标题的
     * 歌词搜索结果,以一个列表形式返回
     * @param artist 歌手名,可能为空
     * @param title 歌名,不能为空
     * @return
     */
    public static List<SearchResult> getSearchResults(String artist, String title) {
        List<SearchResult> list = new ArrayList<SearchResult>();
//        try {
//            HttpClient http = new HttpClient();
//            Config config = Config.getConfig();
//            if (config.isUseProxy()) {
//                if (config.getProxyUserName() != null && config.getProxyPwd() != null) {
//                    http.getState().setProxyCredentials(
//                            new AuthScope(config.getProxyHost(), Integer.parseInt(config.getProxyPort())),
//                            new UsernamePasswordCredentials(config.getProxyUserName(), config.getProxyPwd()));
//                }
//                http.getHostConfiguration().setProxy(config.getProxyHost(),
//                        Integer.parseInt(config.getProxyPort()));
//            }
//            http.getParams().setContentCharset("GBK");
//            GetMethod get = new GetMethod("http://www.baidu.com/s?wd=" + URLEncoder.encode("filetype:lrc " + title + "-" + artist, "GBK"));
//            get.addRequestHeader("Host", "www.baidu.com");
//            get.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11");
//            get.addRequestHeader("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
//            get.addRequestHeader("Accept-Language", "zh-cn,zh;q=0.5");
//            get.addRequestHeader("Keep-Alive", "300");
//            get.addRequestHeader("Referer", "http://www.baidu.com/");
//            get.addRequestHeader("Connection", "keep-alive");
//            @SuppressWarnings("unused")
//			int i = http.executeMethod(get);
//            String temp = getString(get.getResponseBodyAsStream());
//            get.releaseConnection();
//            System.out.println("TEMP="+temp);
//            list.addAll(parseSearchResult(temp));
//        } catch (IOException ex) {
//        }
        return list;
    }
    

    /**
     * 从网页内容里面分析出歌手和歌曲,还有下载的地址
     * @param input 给定的网页源码
     * @return 分析出来的列表
     */
//    private static List<SearchResult> parseSearchResult(String input) {
//        List<SearchResult> list = new ArrayList<SearchResult>();
//        Matcher m = Pattern.compile("(?<=<b>【LRC】</b>).*?(?=文件格式)").matcher(input);
//        Pattern p = Pattern.compile("(?<='\\)\" href=\").*?(?=\" target=\"_blank\"><font size=\"3\">)");
//        while (m.find()) {
//            String str = m.group();
//            String trimed = htmlTrim(str);
//            int index = trimed.indexOf("-");
//            String title = "",artist  = "",url  = null;
//            if (index != -1) {
//                title = trimed.substring(0, index);
//                artist = trimed.substring(index + 1);
//            }
//            Matcher m1 = p.matcher(str);
//            if (m1.find()) {
//                url = m1.group();
//            }
//            if (url != null) {
//                final String tUrl = url;
//                SearchResult sr = new SearchResult(artist, title, new SearchResult.Task() {
//
//                    public String getLyricContent() throws IOException {
//                        return LyricsTools.getURLContent(tUrl);
//                    }
//                });
//                list.add(sr);
//            }
//        }
//        return list;
//    }

    /**
     * 去除HTML标记
     * @param str1 含有HTML标记的字符串
     * @return 去除掉相关字符串
     */
    public static String htmlTrim(String str1) {
        String str = "";
        str = str1;
        //剔出了<html>的标签
        str = str.replaceAll("</?[^>]+>", "");
        //去除空格
        str = str.replaceAll("\\s", "");
        str = str.replaceAll("&nbsp;", "");
        str=str.replaceAll("&amp;", "&");
        str = str.replace(".", "");
        str = str.replace("\"", "‘");
        str = str.replace("'", "‘");
        return str;
    }


    /**
     * 根据歌曲的信息去下载歌词内容
     * @param fileName 文件本名
     * @param info 歌曲信息
     * @return 歌词内容
     */
    public static String getLyric(SongBean info) throws IOException {
        String ly = getLyricBaidu(info);
        return ly;
    }

    /**
     * 从百度去搜索歌词
     * @param info 播放项
     * @return 歌词内容，可能为NULL
     */
    private static String getLyricBaidu(SongBean info) {
        try {
            return getBaidu_Lyric(info.getName());
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * 得到在百度上搜索到的歌词的内容
     * @param key 关键内容
     * @return 内容
     * @throws java.lang.Exception
     */
    private static String getBaidu_Lyric(String key) throws Exception {
//    	System.out.println("去百度上找了...");
//    	String result = null;
//    	HttpGet get = new HttpGet("http://www.baidu.com/s?wd=" + URLEncoder.encode("filetype:lrc " + key, "GBK"));
//    	HttpResponse response= new DefaultHttpClient().execute(get);
//    	if(response.getStatusLine().getStatusCode() == 200) {
//    		result = EntityUtils.toString(response.getEntity(), "GBK");
//    		System.out.println(result);
//    		result = parseSearchResult(result).toString();
//    	}
//    	System.out.println(result);
//    	return result;
//    	HttpClient http = new HttpClient();
//        Config config = Config.getConfig();
//        if (config.isUseProxy()) {
////            if (config.getProxyUserName() != null && config.getProxyPwd() != null) {
////                http.getState().setProxyCredentials(
////                        new AuthScope(config.getProxyHost(), Integer.parseInt(config.getProxyPort())),
////                        new UsernamePasswordCredentials(config.getProxyUserName(), config.getProxyPwd()));
////            }
////            http.getHostConfiguration().setProxy(config.getProxyHost(),
////                    Integer.parseInt(config.getProxyPort()));
//        }
//        http.getParams().setContentCharset("GBK");
//        GetMethod get = new GetMethod("http://www.baidu.com/s?wd=" + URLEncoder.encode("filetype:lrc " + key, "GBK"));
//        get.addRequestHeader("Host", "www.baidu.com");
//        get.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11");
//        get.addRequestHeader("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
//        get.addRequestHeader("Accept-Language", "zh-cn,zh;q=0.5");
//        get.addRequestHeader("Keep-Alive", "300");
//        get.addRequestHeader("Referer", "http://www.baidu.com/");
//        get.addRequestHeader("Connection", "keep-alive");
//        System.out.println("执行那句话");
//        @SuppressWarnings("unused")
//		int i = http.executeMethod(get);
//        System.out.println("那句话执行完了");
//        String temp = getString(get.getResponseBodyAsStream());
//        get.releaseConnection();
////        System.out.println("TEMP="+temp);
////        Matcher m = Pattern.compile("(?<=<b>【LRC】</b>).*?(?=文件格式)").matcher(temp);
//        Matcher m = Pattern.compile("(?<='\\)\" href=\").*?(?=\" target=\"_blank\"><font size=\"3\">)").matcher(temp);
//        if (m.find()) {
//            String str = m.group();
//            String content = getURLContent(str);
//            System.out.println("百度上找到了，content = " + content);
//            return content;
//        } else {
//        	System.out.println("百度上没找到");
//            return null;
//        }
        return null;
    }
    /**
     * 从一个流里面得到这个流的字符串
     * 表现形式
     * @param is 流
     * @return 字符串
     */
//    private static String getString(InputStream is) {
//        InputStreamReader r = null;
//        try {
//            StringBuilder sb = new StringBuilder();
//            // 这里是固定把网页内容的编码写在GBK,应该是可设置的
//            r = new InputStreamReader(is, "GBK");
//            char[] buffer = new char[128];
//            int length = -1;
//            while ((length = r.read(buffer)) != -1) {
//                sb.append(new String(buffer, 0, length));
//            }
//            return sb.toString();
//        } catch (Exception e) {
//            System.out.println(e);
//            return "";
//        } finally {
//            try {
//                r.close();
//            } catch (Exception e) {
//            	System.out.println(e);
//            }
//        }
//    }
    /**
     * 得到URL的内容,最好是只限百度使用
     * @param url URL
     * @return 内容,可能是NULL
     * @throws java.lang.Exception
     */
//    private static String getURLContent(String url) throws IOException {
//        HttpClient http = new HttpClient();
//        Config config = Config.getConfig();
//        if (config.isUseProxy()) {
////            if (config.getProxyUserName() != null && config.getProxyPwd() != null) {
////                http.getState().setProxyCredentials(
////                        new AuthScope(config.getProxyHost(), Integer.parseInt(config.getProxyPort())),
////                        new UsernamePasswordCredentials(config.getProxyUserName(), config.getProxyPwd()));
////            }
////            http.getHostConfiguration().setProxy(config.getProxyHost(),
////                    Integer.parseInt(config.getProxyPort()));
//        }
//        http.getParams().setContentCharset("GBK");
//        GetMethod get = new GetMethod();
//        URI uri = new URI(url, false, "GBK");
//        get.setURI(uri);
//        http.executeMethod(get);
//        System.out.println(get.getResponseCharSet());
//        Header[] hs = get.getResponseHeaders();
//        for (Header h : hs) {
//            System.out.print(h);
//        }
//        return getString(get.getResponseBodyAsStream());
//    }

}
