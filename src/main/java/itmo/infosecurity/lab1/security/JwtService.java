package itmo.infosecurity.lab1.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import itmo.infosecurity.lab1.dto.JwtDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtService {

    @Value("${security.jwt.secret_key}")
    private String jwtSecret;

    public JwtDto generateAuthToken(String email) {
        return new JwtDto(
                generateJwtToken(email),
                generateRefreshToken(email)
        );
    }

    public String refreshAuthToken(String email) {
        return generateJwtToken(email);
    }

    public String getEmailFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }

        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        if (token == null)
            return false;
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ignored) {}

        return false;
    }

    public String generateJwtToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusSeconds(10).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusSeconds(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
