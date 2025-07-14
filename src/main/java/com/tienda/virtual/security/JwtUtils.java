// src/main/java/com/tienda/virtual/security/JwtUtils.java
package com.tienda.virtual.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${tienda.app.jwtSecret}") // Se leerá de application.properties/yml
    private String jwtSecret;

    @Value("${tienda.app.jwtExpirationMs}") // Se leerá de application.properties/yml
    private int jwtExpirationMs;

    private Key getSigningKey() {
        // Genera una clave segura para HmacSha256 desde el secreto de la aplicación.
        // Asegúrate de que jwtSecret sea lo suficientemente largo y complejo.
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     * El subject del token será el nombre de usuario (email) del principal.
     *
     * @param authentication Objeto de autenticación de Spring Security.
     * @return El token JWT generado como String.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // El email del usuario es el subject
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Genera un token JWT directamente para un email de usuario.
     * Este método es útil para escenarios donde el usuario ya ha sido creado
     * o verificado y se necesita generar un token sin pasar por el proceso
     * de autenticación completo de Spring Security (ej. después de la verificación de email).
     *
     * @param email El email del usuario para el cual se generará el token.
     * @return El token JWT generado como String.
     */
    public String generateTokenForUser(String email) {
        return Jwts.builder()
                .setSubject(email) // El email del usuario es el subject
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (subject) de un token JWT.
     *
     * @param token El token JWT del cual extraer el nombre de usuario.
     * @return El nombre de usuario (email) como String.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida la integridad y validez de un token JWT.
     *
     * @param authToken El token JWT a validar.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT ha expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Cadena de JWT vacía: {}", e.getMessage());
        } catch (SignatureException e) { // Añadido para manejar problemas de firma
            logger.error("Firma JWT inválida: {}", e.getMessage());
        }
        return false;
    }
}