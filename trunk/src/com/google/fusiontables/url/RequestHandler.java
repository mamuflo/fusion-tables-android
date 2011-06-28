package com.google.fusiontables.url;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Helper class for sending HTTP requests.
 */
public class RequestHandler {

  /**
   * Send an HTTP POST or GET request.
   *
   * @param uri  the URL
   * @param method  either POST or GET
   * @param body  the body of the HTTP request, can be null
   * @param headers  a map of any headers to add to the request, can be null
   * @return the string response
   */
  public static String sendHttpRequest(String uri, String method,
      String body, HashMap<String, String> headers)  {

    StringBuilder sb = new StringBuilder();

    try {
      // Open the connection
      URL url = new URL(uri);
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setRequestMethod(method);

      // Set Content type header
      if (method == "POST") {
        conn.setRequestProperty("Content-type",
            "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
      }

      // Set headers
      if (headers != null) {
        Set<String> keys = headers.keySet();
        Iterator<String> it = keys.iterator();
        String header = "";
        while (it.hasNext()) {
          header = it.next();
          conn.setRequestProperty(header, headers.get(header));
        }
      }

      // Add the body
      if (body != null) {
        OutputStream out = conn.getOutputStream();
        Writer writer = new OutputStreamWriter(out, "UTF-8");
        writer.write(body);
        writer.flush();
        writer.close();
        out.close();
      }

      // Read the response
      InputStreamReader reader = new InputStreamReader(conn.getInputStream());
      BufferedReader bufferedReader = new BufferedReader(reader);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line);
      }
      bufferedReader.close();
      conn.disconnect();

    } catch (MalformedURLException ex) {
      System.out.println("URL was malformed: " + uri);
      ex.printStackTrace();
      return null;

    } catch (FileNotFoundException ex) {
      System.out.println("Something was wrong with the connection.");
      System.out.println("Check your url: " + uri);
      ex.printStackTrace();
      return null;

    } catch (ProtocolException ex) {
      System.out.println("Protocol is incorrect: " + uri);
      ex.printStackTrace();
      return null;

    } catch (IOException ex) {
      System.out.println("Error during IO");
      ex.printStackTrace();
      return null;

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return sb.toString();
  }
}
