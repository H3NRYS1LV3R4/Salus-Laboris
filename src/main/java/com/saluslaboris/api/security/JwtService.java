package com.saluslaboris.api.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMs;
    private final String issuer;

    public JwtService(@Value("${app.jwt.secret-base64}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs,
                      @Value("${app.jwt.issuer}") String issuer) {
        try {
            key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("JWT_SECRET_BASE64 debe contener al menos 32 bytes aleatorios en Base64");
        }
        if (expirationMs < 1000 || expirationMs > 86400000 || issuer.isBlank()) {
            throw new IllegalArgumentException("Configura un emisor JWT y una duración de 1 segundo a 24 horas");
        }
        this.expirationMs = expirationMs;
        this.issuer = issuer;
    }
    public String issue(UserPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder().setIssuer(issuer).setSubject(principal.id().toString())
            .setId(UUID.randomUUID().toString()).setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(expirationMs)))
            .claim("cv", credentialsFingerprint(principal.password()))
            .signWith(key, SignatureAlgorithm.HS256).compact();
    }
    public Claims parse(String token) {
        Jws<Claims> jwt = Jwts.parserBuilder().setSigningKey(key).requireIssuer(issuer)
            .build().parseClaimsJws(token);
        Claims claims = jwt.getBody();
        if (!SignatureAlgorithm.HS256.getValue().equals(jwt.getHeader().getAlgorithm())
                || claims.getExpiration() == null || claims.getIssuedAt() == null
                || claims.getSubject() == null || claims.get("cv", String.class) == null) {
            throw new MalformedJwtException("Token inválido");
        }
        return claims;
    }
    public boolean matchesCredentials(Claims claims, UserPrincipal user) {
        return MessageDigest.isEqual(claims.get("cv", String.class).getBytes(StandardCharsets.UTF_8),
            credentialsFingerprint(user.password()).getBytes(StandardCharsets.UTF_8));
    }
    public long expiresInSeconds() { return expirationMs / 1000; }

    // Permite invalidar tokens al cambiar la contraseña, sin publicar el hash BCrypt.
    private String credentialsFingerprint(String passwordHash) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(
                mac.doFinal(passwordHash.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.GeneralSecurityException ex) {
            throw new IllegalStateException("No se pudo inicializar HMAC", ex);
        }
    }
}
