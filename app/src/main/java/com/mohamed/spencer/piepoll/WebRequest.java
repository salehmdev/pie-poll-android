package com.mohamed.spencer.piepoll;

import android.util.Log;

import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Map;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class WebRequest {

    public class Response {
      Integer code;
      String body;
      String session = "";

      public Response(Integer code, String body, String... session) {
        this.code = code;
        this.body = body;
        if(session.length > 0) {
          this.session = session[0];
        }
      }

      public Integer getResponseCode() {
        return code;
      }

      public String getResponseBody() {
        return body;
      }

      public String getSessionId() {
        return session;
      }

    }

    public Response getRequest(String URL, Map<String, String> parameters) throws Exception {
        String params = "?";
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
          params += URLEncoder.encode(entry.getKey(), "UTF-8");
          params += "=";
          params += URLEncoder.encode(entry.getValue(), "UTF-8");
          params += "&";
        }

    		URL obj = new URL(URL + params);
    		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

    		conn.setRequestMethod("GET");

    		int responseCode = conn.getResponseCode();

    		BufferedReader in = new BufferedReader(
    		        new InputStreamReader(conn.getInputStream()));
    		String inputLine;
    		StringBuffer response = new StringBuffer();

    		while ((inputLine = in.readLine()) != null) {
    			response.append(inputLine);
    		}
    		in.close();

        Response r = new Response(responseCode, response.toString(), "");
        return r;
    }

    public Response postRequest(String URL, Multimap<String, String> parameters, String... phpsession) throws Exception {
      URL obj = new URL(URL);
      String sessionId = "";
      if(phpsession.length > 0) {
        sessionId = phpsession[0];
      }

      String params = "";

      for (Map.Entry<String, String> entry : parameters.entries()) {
        params += URLEncoder.encode(entry.getKey(), "UTF-8");
        params += "=";
        params += URLEncoder.encode(entry.getValue(), "UTF-8");
        params += "&";
        Log.i("entry", entry.getValue());
      }

      HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Cookie", sessionId);
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestProperty("Content-Length", Integer.toString(params.getBytes().length));
      conn.setUseCaches(false);
      conn.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
      wr.writeBytes(params);
      wr.flush();
      wr.close();

      int responseCode = conn.getResponseCode();

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      String session = "";
      try {
        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");

        for (String cookie : cookies) {
          String noCrap = cookie.split(";", 2)[0];
          String key = noCrap.split("=")[0];
          String value = noCrap.split("=")[1];
          if(key.equals("PHPSESSID")) {
            session = cookie;
          }
        }
      }catch(Exception e) {

      }

      Response r = new Response(responseCode, response.toString(), session);
      return r;
    }

}
