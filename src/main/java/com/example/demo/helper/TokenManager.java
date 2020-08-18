package com.example.demo.helper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;


public class TokenManager {
	//private static String CLIENT_ID="11715483564-jf27oosp8krevks4o2s6bavf17dqk4bi.apps.googleusercontent.com";
	private static final HttpTransport transport = new NetHttpTransport();
	private static final JsonFactory jsonFactory = new JacksonFactory();
	
	public boolean verifyToken(String token) throws GeneralSecurityException, IOException {
		if(token==null||(!token.startsWith("Bearer")))
			return false;
		GoogleIdToken googleToken = GoogleIdToken.parse(jsonFactory,token.substring(7));
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier(transport, jsonFactory);
		return verifier.verify(googleToken);
			
		//String tokenString=token.substring(7);
        //GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        //        .setAudience(Arrays.asList(CLIENT_ID))
        //        .build();
        //GoogleIdToken idToken = verifier.verify(tokenString);
        //if (idToken != null)
        //	return true;
        //else
        //	return false;
        
	}
}
