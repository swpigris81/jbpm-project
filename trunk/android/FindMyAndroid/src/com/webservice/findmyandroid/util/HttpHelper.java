package com.webservice.findmyandroid.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class HttpHelper {
    private static final String CHARSET = "UTF-8";
    private static final int DEFAULT_CONNECTION_TIMEOUT = (20 * 1000); // milliseconds
    private static final int DEFAULT_SOCKET_TIMEOUT = (30 * 1000); // milliseconds

    /**
     * <p>Discription:[Http提交数据]</p>
     * @param path 提交URL
     * @param params 参数
     * @return
     * @throws Exception
     * @author:大牙
     * @update:2013-3-9
     */
    public static String post(final String path, final Map<String, String> params) throws Exception {
        HttpURLConnection conn = null;
        DataOutputStream outStream = null;
        
        try {
            URL url = new URL(path);
            byte[] data = parseParams(params).getBytes(CHARSET);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            conn.setReadTimeout(DEFAULT_SOCKET_TIMEOUT);
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", CHARSET);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            outStream = new DataOutputStream(conn.getOutputStream());
            outStream.write(data);
            outStream.flush();
            
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                StringBuffer sb = new StringBuffer();
                InputStreamReader reader = new InputStreamReader(in, CHARSET);
                char[] buff = new char[1024];
                int len;
                while ((len = reader.read(buff)) > 0) {
                    sb.append(buff, 0, len);
                }
                return sb.toString();
            } else {
                throw new Exception("ResponseCode=" + conn.getResponseCode());
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
            if (null != conn) {
                conn.disconnect();
            }
        }
    }
    
    private static String parseParams(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        return builder.toString();
    }
}
