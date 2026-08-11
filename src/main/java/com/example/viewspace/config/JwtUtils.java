package com.example.viewspace.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;



import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtils {


    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    
    
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    
   
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();   

       
        return Jwts.builder()
                .subject((userPrincipal.getUsername()))  
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    
    
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())   //first verifying secret key.
                .build()
                .parseSignedClaims(token)     //extracting claims.    // claims are pieces of information or statements about an entity (usually the user) and metadata about the token itself. They are stored as key-value pairs inside the token's payload
                .getPayload()    //extracting payload from calims
                .getSubject();   //extracting subject(i.e username) from payload.
    }

    
    
    
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);   //we will verify with key which we've mentioned in application.properties.
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

}













/*trick to generate jwt key
 * 
 * cmd>jshell
 * import java.security.SecureRandom;
import java.util.Base64;
byte[] key = new byte[32];
new SecureRandom().nextBytes(key);
System.out.println(Base64.getEncoder().encodeToString(key));

cmd> /exit
 * 
 * */
 