package com.azhon.appupdate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 项目名:    AppUpdate
 * 包名       com.azhon.appupdate.utils
 * 文件名:    FileUtil
 * 创建时间:  2018/1/27 on 16:34
 * 描述:     TODO
 *
 * @author 阿钟
 */


public final class FileUtil {

    /**
     * 创建保存的文件夹
     */
    public static void createDirDirectory(String downloadPath) {
        File dirDirectory = new File(downloadPath);
        if (!dirDirectory.exists()) {
            dirDirectory.mkdirs();
        }
    }

    /**
     * 创建一个随机读写
     */
    public static RandomAccessFile createRAFile(String downloadPath, String fileName) {
        //断点读写
        try {
            return new RandomAccessFile(createFile(downloadPath, fileName), "rwd");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建一个文件
     *
     * @param downloadPath 路径
     * @param fileName     名字
     * @return 文件
     */
    public static File createFile(String downloadPath, String fileName) {
        return new File(downloadPath, fileName);
    }

    /**
     * 查看一个文件是否存在
     *
     * @param downloadPath 路径
     * @param fileName     名字
     * @return true | false
     */
    public static boolean fileExists(String downloadPath, String fileName) {
        return new File(downloadPath, fileName).exists();
    }

    /**
     * 删除一个文件
     *
     * @param downloadPath 路径
     * @param fileName     名字
     * @return true | false
     */
    public static boolean delete(String downloadPath, String fileName) {
        return new File(downloadPath, fileName).delete();
    }

    /**
     * @param file 获取一个文件
     * @return  文件md5值
     */
    public static String md5(File file) {
        MessageDigest digest = null;
        FileInputStream fis = null;
        byte[] buffer = new byte[1024];

        try {
            if (!file.isFile()) {
                return "";
            }

            digest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);

            while (true) {
                int len;
                if ((len = fis.read(buffer, 0, 1024)) == -1) {
                    fis.close();
                    break;
                }

                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger var5 = new BigInteger(1, digest.digest());
        return String.format("%1$032x", new Object[]{var5});
    }
}
