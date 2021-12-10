package com.reddit.backend.security;

import com.reddit.backend.exception.ActivationException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class JWTProvider {

    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("/home/kamran/Downloads/backend/keystore.jks"), "kamran".toCharArray());
            System.out.println();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException exception) {
            throw new ActivationException("Exception occurred while loading keystore");
        }
    }

    public String generateToken(Authentication authentication) {
        User princ = (User) authentication.getPrincipal();
        return Jwts.builder().setSubject(princ.getUsername()).signWith(getPrivateKey()).compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            PrivateKey key = (PrivateKey) keyStore.getKey("alias", "kamran".toCharArray());
            return key;
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            throw new ActivationException("Error occurred while retrieving public key");
        }
    }
}
