package com.volunteersync.backend.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.volunteersync.backend.dto.GoogleTokenInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {
    
    @Value("${google.oauth.client-id}")
    private String googleClientId;
    
    public GoogleTokenInfo verify(String idToken) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(),
            JacksonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(googleClientId))
            .build();
        
        GoogleIdToken token = verifier.verify(idToken);
        if (token != null) {
            GoogleIdToken.Payload payload = token.getPayload();
            
            GoogleTokenInfo info = new GoogleTokenInfo();
            info.setSub(payload.getSubject());
            info.setEmail(payload.getEmail());
            info.setGivenName((String) payload.get("given_name"));
            info.setFamilyName((String) payload.get("family_name"));
            info.setPicture((String) payload.get("picture"));
            
            return info;
        }
        
        return null;
    }
}