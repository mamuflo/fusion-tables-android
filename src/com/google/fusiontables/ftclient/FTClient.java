package com.google.fusiontables.ftclient;

import java.net.URLEncoder;
import java.util.HashMap;

import com.google.fusiontables.url.RequestHandler;

/**
 * Helper class for sending Fusion Tables requests.
 */
public class FTClient {
  private String token;
  private final String requestURL =
      "https://www.google.com/fusiontables/api/query";

  /**
   * Constructor, sets the auth token.
   *
   * @param token  the ClientLogin token
   */
  public FTClient(String token) {
    this.token = token;
  }

  /**
   * Send the query to Fusion Tables.
   *
   * @param query  the query to send
   * @return the results of the query
   */
  public String query(String query) {
    String result = "";

    // Create the auth header
    HashMap<String, String> headers = new HashMap<String, String>();
    headers.put("Authorization", "GoogleLogin auth=" + this.token);

    // Convert to lower
    String lower = query.toLowerCase();
    // Encode the query
    query = "sql=" + URLEncoder.encode(query);

    // Determine POST or GET based on query
    if (lower.startsWith("SELECT") ||
        lower.startsWith("SHOW") ||
        lower.startsWith("DESCRIBE")) {

      result = RequestHandler.sendHttpRequest(this.requestURL + "?" + query,
          "GET", null, headers);

    } else {

      result = RequestHandler.sendHttpRequest(this.requestURL,
          "POST", query, headers);
    }

    return result;
  }
}
