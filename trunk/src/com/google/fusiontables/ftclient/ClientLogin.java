// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.fusiontables.ftclient;

import java.net.URLEncoder;

import com.google.fusiontables.url.RequestHandler;

/**
 * Helper class for getting auth token.
 * 
 * @author kbrisbin@google.com (Kathryn Hurley)
 */
public class ClientLogin {
  private final static String authURI =
      "https://www.google.com/accounts/ClientLogin";

  /**
   * Get auth token.
   *
   * @param username  the username
   * @param password  the password
   * @return the client login token
   */
  public static String authorize(String username, String password) {
    String token = "";
    try {
      // Encode the body
      String body = "Email=" + URLEncoder.encode(username) + "&" +
        "Passwd=" + URLEncoder.encode(password) + "&" +
        "service=" + URLEncoder.encode("fusiontables") + "&" +
        "accountType=" + URLEncoder.encode("HOSTED_OR_GOOGLE");

      // Send the response and parse results to get token
      String response = RequestHandler.sendHttpRequest(authURI, "POST",
          body, null);
      token = response.trim().split("=")[3];

    } catch(Exception ex) {
      ex.printStackTrace();
    }

    return token;
  }
}
