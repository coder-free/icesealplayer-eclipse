/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zbf.iceseal.tag.ape;


/**
 * 一个工具类，主要负责分析歌词
 * 并找到歌词下载下来，然后保存成标准格式的文件
 * 还有一些常用的方法
 * @author hadeslee
 */
public final class Util {

    /**
     * 从一个int值得到它所代表的字节数组
     * @param i 值 
     * @return 字节数组
     */
    public static byte[] getBytesFromInt(int i) {
        byte[] data = new byte[4];
        data[0] = (byte) (i & 0xff);
        data[1] = (byte) ((i >> 8) & 0xff);
        data[2] = (byte) ((i >> 16) & 0xff);
        data[3] = (byte) ((i >> 24) & 0xff);
        return data;
    }

    /**
     * 从传进来的数得到这个数组
     * 组成的整型的大小
     * @param data 数组
     * @return 整型
     */
    public static int getInt(byte[] data) {
        if (data.length != 4) {
            throw new IllegalArgumentException("数组长度非法,要长度为4!");
        }
        return (data[0] & 0xff) | ((data[1] & 0xff) << 8) | ((data[2] & 0xff) << 16) | ((data[3] & 0xff) << 24);
    }

    /**
     * 从传进来的字节数组得到
     * 这个字节数组能组成的长整型的结果
     * @param data 字节数组
     * @return 长整型
     */
    public static long getLong(byte[] data) {
        if (data.length != 8) {
            throw new IllegalArgumentException("数组长度非法,要长度为4!");
        }
        return (data[0] & 0xff) |
                ((data[1] & 0xff) << 8) |
                ((data[2] & 0xff) << 16) |
                ((data[3] & 0xff) << 24) |
                ((data[4] & 0xff) << 32) |
                ((data[5] & 0xff) << 40) |
                ((data[6] & 0xff) << 48) |
                ((data[7] & 0xff) << 56);
    }
   
}

