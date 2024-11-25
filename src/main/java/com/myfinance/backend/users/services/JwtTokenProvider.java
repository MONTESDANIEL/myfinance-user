package com.myfinance.backend.users.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Genera una clave segura
                                                                                     // automáticamente
    private final long validityInMilliseconds = 5 * 60 * 60 * 1000; // 5 horas en milisegundos

    /**
     * Genera un token JWT con un tiempo de validez de 5 horas.
     *
     * @param username
     *            Nombre de usuario para el token
     * @return Token generado
     */
    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256) // Usa la clave segura generada
                .compact();
    }

    /**
     * Valida si el token es correcto y no está expirado.
     *
     * @param token
     *            Token a validar
     * @return true si el token es válido, false de lo contrario
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae el nombre de usuario del token.
     *
     * @param token
     *            Token del cual extraer el nombre de usuario
     * @return Nombre de usuario extraído
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
