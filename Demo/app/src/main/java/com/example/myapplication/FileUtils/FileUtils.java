package com.example.myapplication.FileUtils;

import com.example.myapplication.R;

import java.io.File;

public class FileUtils {

    String filename;
    String filepath;
    public static int getFileIcon(String file) {
        switch (fileType(file)) {

            case "folder":
                return R.mipmap.fd;
            case "html":
                return R.mipmap.html;
            case "css":
                return R.mipmap.css;
            case "js":
                return R.mipmap.js;
            case "jpg":
                return R.mipmap.jpg;
            case "gif":
                return R.mipmap.gif;
            case "zip":
                return R.mipmap.zip;
            case "txt":
                return R.mipmap.txt;
            case "rtf":
                return R.mipmap.rtf;
            case "pdf":
                return R.mipmap.pdf;
            case "svg":
                return R.mipmap.svg;
            case "xls":
                return R.mipmap.xls;
            case "doc":
                return R.mipmap.doc;
            case "png":
                return R.mipmap.png;
            case ".":
                return R.mipmap.fd;
            default:
                return R.mipmap.folder;

        }

    }
    public static int getFileIcon(File file) {
        switch (fileType(file.getName())) {

            case "folder":
                return R.mipmap.fd;
            case "html":
                return R.mipmap.html;
            case "css":
                return R.mipmap.css;
            case "js":
                return R.mipmap.js;
            case "jpg":
                return R.mipmap.jpg;
            case "gif":
                return R.mipmap.gif;
            case "zip":
                return R.mipmap.zip;
            case "txt":
                return R.mipmap.txt;
            case "rtf":
                return R.mipmap.rtf;
            case "pdf":
                return R.mipmap.pdf;
            case "svg":
                return R.mipmap.svg;
            case "xls":
                return R.mipmap.xls;
            case "doc":
                return R.mipmap.doc;
            case "png":
                return R.mipmap.png;
            default:
                return R.mipmap.folder;

        }

    }
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

    public static String fileType(String fileName) {
        if (fileName.lastIndexOf(".") != -1 ) {
            String suffix = fileName.substring(fileName.lastIndexOf(".")).substring(1);
            return suffix;
        } else{
            return "folder";}
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