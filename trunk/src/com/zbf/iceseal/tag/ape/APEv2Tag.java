/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zbf.iceseal.tag.ape;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hadeslee
 */
public class APEv2Tag {

	private boolean hasTag;
//    private static Logger log = Logger.getLogger(APEv2Tag.class.getName());
    private File input;
    private TagHead head;
    private TagBody body;
    private String artist = "";
    private String album = "";
    private String title = "";
    private String year = "";
    private String comment = "";
    private String track = "";
    private String genre = "";
    private Map<String, String> map;

    public APEv2Tag(File file) throws IOException {
        this.input = file;
        map = new HashMap<String, String>();
        load();
    }
    
    public APEv2Tag(String path) throws IOException {
        this.input = new File(path);
        map = new HashMap<String, String>();
        load();
    }

    public APEv2Tag() {
        map = new HashMap<String, String>();
    }

    protected void load() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(input, "r");
        //先查看最后32个字节
        try {
            raf.seek((int) (input.length() - 32));
            byte[] buffer = new byte[32];
            raf.read(buffer);
            head = new TagHead(buffer);
            if (head.isValid()) {
//                log.log(Level.INFO, "读取:最后32个字节有标签!");
                int size = head.getTagSize();
                raf.seek((int) (input.length() - size));
                buffer = new byte[size - 32];
                int read = 0;
                while (read < buffer.length) {
                    read += raf.read(buffer, read, buffer.length - read);
                }
                body = new TagBody(buffer);
//                List<TagItem> list = body.getItems();
//                for (TagItem item : list) {
//                    log.log(Level.INFO, item.toString());
//                }
                hasTag = true;

            } else {//再查看128前面的32个字节
                raf.seek((int) (input.length() - 32 - 128));
                raf.read(buffer);
                head = new TagHead(buffer);
                if (head.isValid()) {
//                    log.log(Level.INFO, "读取:ID3v1前面的字节有标签!");
                    int size = head.getTagSize();
                    raf.seek((int) (input.length() - size - 128));
                    buffer = new byte[size - 32];
                    int read = 0;
                    while (read < buffer.length) {
                        read += raf.read(buffer, read, buffer.length - read);
                    }
                    body = new TagBody(buffer);
//                    List<TagItem> list = body.getItems();
//                    for (TagItem item : list) {
//                        log.log(Level.INFO, item.toString());
//                    }
                    hasTag = true;
                } else {
                	hasTag = false;
//                    System.out.println("读取:找不到APEv2格式的标签!");
                }
            }
        } finally {
            try {
                raf.close();
                readTag();
            } catch (Exception exe) {
            	hasTag = false;
//            	System.out.println("读取:找不到APEv2格式的标签!");
            }
        }
    }

    private void readTag() {
        for (TagItem item : body.getItems()) {
            map.put(item.getId(), item.getContent());
        }
        this.album = map.get(APEv2FieldKey.Album.name());
        this.artist = map.get(APEv2FieldKey.Artist.name());
        this.comment = map.get(APEv2FieldKey.Comment.name());
        this.genre = map.get(APEv2FieldKey.Genre.name());
        this.title = map.get(APEv2FieldKey.Title.name());
        this.track = map.get(APEv2FieldKey.Track.name());
        this.year = map.get(APEv2FieldKey.Year.name());
    }

    /**
     * 写出APE标签到文件里面去
     * @param raf 随机文件流
     * @param hasID3v1 是否有ID3v1标签
     * @throws java.io.IOException
     */
    public void write(RandomAccessFile raf, boolean hasID3v1) throws IOException {
        //如果有ID3标签,则先把它缓存起来,总共128个字节
        byte[] temp = null;
        int deleteLength = 0;
        if (hasID3v1) {
            temp = new byte[128];
            raf.seek(raf.length() - 128);
            raf.read(temp);
            deleteLength += 128;
        }
        TagHead header = checkTag(raf);
        //如果有标头,则说明有APE的标签,还要多删一些
        if (header != null) {
//            log.log(Level.INFO, "原来存在APEv2标签,先删除之...");
            int length = header.getTagSize();
            if (header.hasHeader()) {//如果有标头的话,长度还要加32个字节
                length += 32;
            }
            deleteLength += length;
        } else {
//            log.log(Level.INFO, "以前不存在APEv2标签,直接添加...");
        }
        raf.setLength(raf.length() - deleteLength);
        //把该截掉的都截了以后,就开始写标签了,先写APE的,再看
        //有没有ID3的,有就写,没有就不写了
        raf.seek(raf.length());
        byte[] data = getTagBytes();
        raf.write(data);
        if (temp != null) {
            raf.write(temp);
        }
//        log.log(Level.INFO, "APEv2标签写出完毕...");
    }

    /**
     * 得到标签所代表的字节数组
     * @return 标签所代表的字节数组
     */
    private byte[] getTagBytes() throws UnsupportedEncodingException, IOException {
        int itemCount = map.size();
        body = new TagBody();
        for (Map.Entry<String, String> en : map.entrySet()) {
            body.addTagItem(new TagItem(en.getKey(), en.getValue()));
        }
        byte[] bodyData = body.getBytes();
//        log.log(Level.SEVERE, "BODYSIZE=" + bodyData.length);
        TagHead header = new TagHead();
        header.setFlag(TagHead.HEAD);
        header.setItemCount(itemCount);
        header.setTagSize(bodyData.length + 32);
        header.setVersion(TagHead.V2);

        TagHead foot = new TagHead();
        foot.setFlag(TagHead.FOOT);
        foot.setItemCount(itemCount);
        foot.setTagSize(bodyData.length + 32);
        foot.setVersion(TagHead.V2);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bout.write(header.getBytes());
        bout.write(bodyData);
        bout.write(foot.getBytes());
        bout.flush();
        return bout.toByteArray();
    }

    /**
     * 检查是否已经存在APE的标签了,主要查两个地方
     * 一个是最后的字节,还有一个是最后128字节以上的字节
     * 因为最后的字节可能写入了ID3v1标签
     * @param raf 文件
     * @return 得到标签头
     * @throws java.io.IOException
     */
    private TagHead checkTag(RandomAccessFile raf) throws IOException {
        raf.seek((int) (raf.length() - 32));
        byte[] buffer = new byte[32];
        raf.read(buffer);
        TagHead header = new TagHead(buffer);
        if (header.isValid()) {
            header.setIndex(0);
            return header;
        } else {
            raf.seek((int) (raf.length() - 32 - 128));
            raf.read(buffer);
            header = new TagHead(buffer);
            if (header.isValid()) {
                header.setIndex(128);
                return header;
            } else {
                return null;
            }
        }
    }

    /**
     * 删除标签,如果存在ID3v1的话,就要先保存它然后删除后面部份
     * 把它写回来
     * @param raf 写出文件
     * @param hasID3v1 是否有ID3v1标签
     * @throws java.io.IOException
     */
    public void delete(RandomAccessFile raf, boolean hasID3v1) throws IOException {
        //如果有ID3标签,则先把它缓存起来,总共128个字节
        byte[] temp = null;
        int deleteLength = 0;
        if (hasID3v1) {
            temp = new byte[128];
            raf.seek(raf.length() - 128);
            raf.read(temp);
            deleteLength += 128;
        }
        TagHead header = checkTag(raf);
        //如果有标头,则说明有APE的标签,还要多删一些
        if (header != null) {
//            log.log(Level.INFO, "原来存在APEv2标签,先删除之...");
            int length = header.getTagSize();
            if (header.hasHeader()) {//如果有标头的话,长度还要加32个字节
                length += 32;
            }
            deleteLength += length;
        }
        raf.setLength(raf.length() - deleteLength);
//        log.log(Level.INFO, "APEv2标签删除完毕...");
    }

    public String getFirstAlbum() {
        return this.album;
    }

    public String getFirstArtist() {
        return artist;
    }

    public String getFirstComment() {
        return comment;
    }

    public String getFirstGenre() {
        return genre;
    }

    public String getFirstTitle() {
        return title;
    }

    public String getFirstTrack() {
        return track;
    }

    public String getFirstYear() {
        return year;
    }
    
    public boolean hasTag() {
    	return hasTag;
    }

}
