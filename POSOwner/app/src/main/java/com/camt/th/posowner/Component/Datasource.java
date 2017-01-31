package com.camt.th.posowner.Component;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by W.J on 1/23/2016.
 */
public class Datasource {
    private URL url;
    private String method = "GET";
    public HttpURLConnection conn;
    public static String WEBSERVICE = "http://192.168.1.4:3000/";

    public static String getWEBSERVICE() {
        return WEBSERVICE;
    }

    public static void setWEBSERVICE(String WEBSERVICE) {
        Datasource.WEBSERVICE = WEBSERVICE;
    }

    public Datasource(String sourceUrl) {
        try {
            this.url = new URL(WEBSERVICE + sourceUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Datasource(String sourceUrl, String method) {
        try {
            this.url = new URL(WEBSERVICE + sourceUrl);
            this.method = method;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject execute() {
        JSONObject  result = null;
        InputStream is = null;
        try {
            this.conn = (HttpURLConnection) this.url.openConnection();
            this.conn.setRequestMethod(this.method);
            this.conn.setReadTimeout(10000);
            this.conn.setConnectTimeout(15000);
            this.conn.setDoInput(true);
            this.conn.connect();
            is = this.conn.getInputStream();
            String raw = convertInputStreamToString(is);
            if (raw.equals("false")) {
                return null;
            }
            result = new JSONObject(raw);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public JSONArray execute(String query) {
        JSONArray result = null;
        InputStream is = null;
        try {
            this.conn = (HttpURLConnection) this.url.openConnection();
            this.conn.setRequestMethod(this.method);
            this.conn.setReadTimeout(10000);
            this.conn.setConnectTimeout(15000);
            this.conn.setDoInput(true);
            this.conn.connect();
            if (query != null) {
                try {
                    DataOutputStream output = new DataOutputStream(this.conn.getOutputStream());
                    output.writeBytes(query);
                    output.flush();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            is = this.conn.getInputStream();
            String raw = convertInputStreamToString(is);
            if (raw.equals("false")) {
                return null;
            }
            result = new JSONArray(raw);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
