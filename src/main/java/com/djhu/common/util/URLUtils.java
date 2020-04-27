package com.djhu.common.util;

import sun.net.util.URLUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;

/**
 * @author cyf
 * @description
 * @create 2020-04-27 11:10
 **/
public class URLUtils {

    public static boolean isConnection(String url) {
        try {
            URL url1 = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();

            int code = connection.getResponseCode();
            if (code == 200) {
            }
            System.out.println("URL可用！");
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }


    public static void main(String[] args) throws IOException {
//        boolean connection = isConnection("http://192.168.120.75:8096/test/data/receive");

        String url = "http://192.168.120.75:8096/test/data/receive";

        URL url1 = new URL(url);
        Permission permission = URLUtil.getConnectPermission(url1);
//        System.out.println(" connection:  "+connection);
        System.out.println(" permission:  "+permission);
    }



}
