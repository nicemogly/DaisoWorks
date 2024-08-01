package com.example.daisoworks.GlideFTP;

//import android.support.annotation.NonNull;
import androidx.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;


public class FTPDataFetcher implements DataFetcher<InputStream> {
    private final FTPModel model;
    private InputStream stream;
    FTPClient ftpClient;

    FTPDataFetcher(FTPModel model) {

        androidx.media3.common.util.Log.d("testest" , "5555");


        this.model = model;
        ftpClient = new FTPClient();
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.setConnectTimeout(15 * 1000); // 15s

        androidx.media3.common.util.Log.d("testest" , "55551");

    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
        try {


            int reply; // 服务器响应值
            String kkk = model.getServer();
            androidx.media3.common.util.Log.d("testest" , kkk);
            if (model.getPort() == null) {
                androidx.media3.common.util.Log.d("testest" , "55554");
                ftpClient.connect(model.getServer());
            } else {
                androidx.media3.common.util.Log.d("testest" , "55555");

                try {
                    ftpClient.connect(model.getServer(), Integer.parseInt(model.getPort()));
                    ftpClient.login("posweb","posweb1089");
                    System.out.println("status :: " + ftpClient.getStatus());
                    androidx.media3.common.util.Log.d("testest" , "status + ftpClient.getStatus()");
                } catch (Exception ex) {
                    Log.d("FTP","Error: " + ex.getMessage());

                }


             //   ftpClient.connect(model.getServer(), Integer.parseInt(model.getPort()));
                androidx.media3.common.util.Log.d("testest" , "melong0");
            }
            // 获取响应值
            androidx.media3.common.util.Log.d("testest" , "melong1");
            reply = ftpClient.getReplyCode();
            androidx.media3.common.util.Log.d("testest" , "melong2");

            if (!FTPReply.isPositiveCompletion(reply)) {

                androidx.media3.common.util.Log.d("testest" , "55553");
                // 断开连接
                ftpClient.disconnect();
                throw new Exception("Can't connect to " + model.getServer()
                        + ":" + model.getPort()
                        + ". The server response is: " + ftpClient.getReplyString());

            }

            ftpClient.login(model.getUsername(), model.getPassword());
            // 获取响应值
            reply = ftpClient.getReplyCode();
            String kkk1 = Integer.toString(reply);
            androidx.media3.common.util.Log.d("testest" , "teststest" + kkk1);
            if (!FTPReply.isPositiveCompletion(reply)) {

                androidx.media3.common.util.Log.d("testest" , "LOGINNOOK");
                // 断开连接
                ftpClient.disconnect();
                throw new Exception("Error while performing login on " + model.getServer()
                        + ":" + model.getPort()
                        + " with username: " + model.getUsername()
                        + ". Check your credentials and try again.");

            } else {
                // 获取登录信息
                androidx.media3.common.util.Log.d("testest" , "LOGINOK");

                FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
                config.setServerLanguageCode("zh");
                ftpClient.configure(config);
                // 使用被动模式设为默认
                ftpClient.enterLocalPassiveMode();
                // 二进制文件支持
                ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
            }
            // 先判断服务器文件是否存在
            FTPFile[] files = ftpClient.listFiles(model.getFilePath());
            if (files.length == 0 || !files[0].isFile()) {
                Log.d("", "文件不存在");
            }

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            stream = ftpClient.retrieveFileStream(model.getFilePath());
            callback.onDataReady(stream);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (Exception exc) {
                }
            }
            ftpClient = null;
        }
    }

    @Override
    public void cleanup() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
//    @Override
//    public String getId() {
//        return model.ftpPath;
//    }

    @Override
    public void cancel() {
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }

}
