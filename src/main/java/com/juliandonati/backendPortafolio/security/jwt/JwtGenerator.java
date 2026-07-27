package com.juliandonati.backendPortafolio.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtGenerator {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Devuelve la SigningKey.
     * @return Valor en Bytes de la signing key en UTF_8
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();

        return token;
    }

    public String getUsernameFromJwt(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
        }
        catch (MalformedJwtException e){
            System.err.println("JWT inválido: " + e.getMessage());
            return false;
        }
        catch (ExpiredJwtException e){
            System.err.println("JWT expirado: " + e.getMessage());
            return false;
        }
        catch (UnsupportedJwtException e){
            System.err.println("JWT no soportado: "+ e.getMessage());
            return false;
        }
        catch (IllegalArgumentException e){
            System.err.println("String de Claim JWT vacío: " + e.getMessage());
            return false;
        }
        catch (SignatureException e){
            System.err.println("JWT firma fallada: " + e.getMessage());
            return false;
        }

        return true;
    }
}
