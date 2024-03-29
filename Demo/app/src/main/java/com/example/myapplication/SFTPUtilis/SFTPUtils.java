package com.example.myapplication.SFTPUtilis;

import android.annotation.SuppressLint;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

    public class SFTPUtils {
        /*
          1.JSCH API Document: https://epaul.github.io/jsch-documentation/javadoc/
          2.JSCH tutor in StackOverflow https://stackoverflow.com/questions/41050989/basic-ssh-connection-via-jsch-on-android
          3.JSCH android sample:https://www.jianshu.com/p/64d571913185
         */


        private String TAG="SFTPUtils";
        private String host;
        private String username;
        private String password;
        private int port = 22;
        private ChannelSftp sftp = null;
        private Session sshSession = null;

        public SFTPUtils (String host, String username, String password) {
            this.host = host;
            this.username = username;
            this.password = password;
        }

        /**
         * connect server via sftp
         */
        public ChannelSftp connect() {
            JSch jsch = new JSch();
            try {
                sshSession = jsch.getSession(username, host, port);
                sshSession.setPassword(password);
                Properties sshConfig = new Properties();
                sshConfig.put("StrictHostKeyChecking", "no");
                sshSession.setConfig(sshConfig);
                sshSession.connect();
                Log.e(TAG,"here is connect");
                Channel channel = sshSession.openChannel("sftp");
                if (channel != null) {
                    channel.connect();
                    Log.e(TAG,"channel exist");
                } else {
                    Log.e(TAG, "channel connecting failed.");
                }
                sftp = (ChannelSftp) channel;
            } catch (JSchException e) {
                e.printStackTrace();
            }
            return sftp;
        }




        /**
         * disconnect
         */
        public void disconnect() {
            if (this.sftp != null) {
                if (this.sftp.isConnected()) {
                    this.sftp.disconnect();
                    Log.d(TAG,"sftp is closed already");
                }
            }
            if (this.sshSession != null) {
                if (this.sshSession.isConnected()) {
                    this.sshSession.disconnect();
                    Log.d(TAG,"sshSession is closed already");
                }
            }
        }

       public boolean isChannelConnected(){
            if(sftp!=null){
                if(this.sftp.isConnected()){
                    return true;
                }
                Log.d(TAG,"line 95");
                return false;
            }
            Log.d(TAG,"line 98");
            return false;
       }

        /**
         * upload
         * @param remotePath
         * @param remoteFileName
         * @param localPath
         * @param localFileName
         * @return
         */
        public boolean uploadFile(String remotePath, String remoteFileName,
                                  String localPath, String localFileName) {
            FileInputStream in = null;
            try {
                createDir(remotePath);
                System.out.println(remotePath);
                File file = new File(localPath + "/"+localFileName);
                in = new FileInputStream(file);
                System.out.println(in);
                sftp.put(in, remoteFileName);
                System.out.println(sftp);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (SftpException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }




        /**
         * Download
         * @param remotePath
         * @param remoteFileName
         * @param localPath
         * @param localFileName
         * @return
         */
        public boolean downloadFile(String remotePath, String remoteFileName,
                                    String localPath, String localFileName) {
            try {
               // sftp.cd(remotePath);
                File file = new File(localPath +"/"+ localFileName);
                mkdirs(localPath + localFileName);
                sftp.get(remoteFileName, new FileOutputStream(file));
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (SftpException e) {
                e.printStackTrace();
            }

            return false;
        }


        public boolean createDir(String createpath) {
            try {
                if (isDirExist(createpath)) {
                    this.sftp.cd(createpath);
                    Log.d(TAG,createpath);
                    return true;
                }
                String pathArry[] = createpath.split("/");
                StringBuffer filePath = new StringBuffer("/");
                for (String path : pathArry) {
                    if (path.equals("")) {
                        continue;
                    }
                    filePath.append(path + "/");
                    if (isDirExist(createpath)) {
                        sftp.cd(createpath);
                    } else {
                        sftp.mkdir(createpath);
                        sftp.cd(createpath);
                    }
                }
                this.sftp.cd(createpath);
                return true;
            } catch (SftpException e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * Does dir exist
         * @param directory
         * @return
         */

        public boolean isDirExist(String directory) {
            boolean isDirExistFlag = false;
            try {
                SftpATTRS sftpATTRS = sftp.lstat(directory);
                isDirExistFlag = true;
                return sftpATTRS.isDir();
            } catch (Exception e) {
                if (e.getMessage().toLowerCase().equals("no such file")) {
                    isDirExistFlag = false;
                }
            }
            return isDirExistFlag;
        }




        /**
         * make dirs
         * @param path
         */
        public void mkdirs(String path) {
            File f = new File(path);
            String fs = f.getParent();
            f = new File(fs);
            if (!f.exists()) {
                f.mkdirs();
            }
        }

        /**
         * 列出目录文件
         * @param directory
         * @return
         * @throws SftpException
         */

        @SuppressWarnings("rawtypes")
        public Vector listFiles(String directory){
            Vector v =null;
            try {
                v =  sftp.ls(directory);
            }catch (SftpException e){
                e.printStackTrace();
            }
                return v;
            }


        public boolean cdDeeper(String directory){
            try{
                sftp.cd(directory);
                return true;
            }catch (SftpException e){
                e.printStackTrace();
                return false;
            }

        }




        public String currentRemotePath(){
            String s= "Failed";
            try {
                    s = sftp.pwd();
                    Log.d(TAG, s);
            }catch (SftpException e){
                e.printStackTrace();
                Log.d(TAG,"error");
            }
            return s;
        }





    }


