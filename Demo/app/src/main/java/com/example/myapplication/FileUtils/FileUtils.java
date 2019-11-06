package com.example.myapplication.FileUtils;

import java.io.File;

public class FileUtils {

    String filename;
    String filepath;

    /**
     * 2019/11/6
     * get the type of file by suffix
     * Input: File
     * if is a folder return -f. if a file return -[type], if no suffix, return only -
     * @param  file
     * @return filename
     */

    public static String getSuffix(File file) {

        String fileName = file.getName();
        if(fileName.contains(".")) {
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

            return "-"+suffix;

        }else {
            return "-"+"f";
        }
        }
}

//This class is used to store some code
// Java 获取文件后缀
//    public static void main(String[] args) {
//        File file = new File("HelloWorld.java");
//        String fileName = file.getName();
//        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//        System.out.println(suffix);
//    }