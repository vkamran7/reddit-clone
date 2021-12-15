package com.reddit.backend.security;

import com.reddit.backend.exception.ActivationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import static io.jsonwebtoken.Jwts.parserBuilder;

@Service
public class JWTProvider {

    private KeyStore keyStore;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationMillis;

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
        return generateTokenWithUsername(princ.getUsername());
    }

    public String generateTokenWithUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationMillis)))
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("alias", "kamran".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            throw new ActivationException("Error occurred while retrieving public key");
        }
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("alias").getPublicKey();
        } catch (KeyStoreException ex) {
            throw new ActivationException("Exception occurred retrieving public key");
        }
    }

    public boolean validateToken(String token) {
        parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token);
        return true;
    }

    public String getNameFromJWT(String token) {
        Claims claims = parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Long getJwtExpirationMillis() {
        return jwtExpirationMillis;
    }
}
