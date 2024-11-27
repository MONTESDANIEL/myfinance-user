package com.myfinance.backend.users.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Genera una clave segura
                                                                                     // automáticamente
    private final long validityInMilliseconds = 5 * 60 * 60 * 1000; // 5 horas en milisegundos

    // Conjunto para almacenar tokens revocados
    private static final Set<String> revokedTokens = new HashSet<>();

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
     * Valida si el token es correcto, no está expirado y no está revocado.
     *
     * @param token
     *            Token a validar
     * @return true si el token es válido, false de lo contrario
     */
    public boolean validateToken(String token) {
        try {
            // Verificar si el token está revocado
            if (revokedTokens.contains(token)) {
                return false; // El token está revocado
            }

            // Validar el token
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

    /**
     * Revoca un token, es decir, lo agrega a la lista de tokens revocados.
     *
     * @param token
     *            El token que deseas revocar
     */
    public void revokeToken(String token) {
        revokedTokens.add(token);
    }
}
