package com.mobi.ripple_be.service;


import com.mobi.ripple_be.configuration.security.AuthPrincipal;
import com.mobi.ripple_be.model.AppUserModel;
import com.mobi.ripple_be.util.AppUserDetails;
import com.mobi.ripple_be.util.jwt.ExtraClaimKey;
import com.mobi.ripple_be.util.jwt.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${ripple.secret-key}")
    private CharSequence SECRET_KEY;

    @Value("${ripple.key-id}")
    private String KEY_ID;


    @Value("${ripple.refresh-token-expiration-minutes}")
    private Integer REFRESH_TOKEN_EXPIRATION_TIME_MIN;

    @Value("${ripple.access-token-expiration-minutes}")
    private Integer ACCESS_TOKEN_EXPIRATION_TIME_MIN;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Date extractIssueDate(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateRefreshToken(AppUserModel userModel) {
        UserDetails userDetails = AuthPrincipal.builder()
                .id(userModel.getId())
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .build();
        return generateRefreshToken(userDetails);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(
                Map.of(ExtraClaimKey.TOKEN_TYPE.getClaimName(),
                        TokenType.REFRESH_TOKEN.getTokenName()
                ),
                userDetails,
                REFRESH_TOKEN_EXPIRATION_TIME_MIN
        );
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(
                Map.of(ExtraClaimKey.TOKEN_TYPE.getClaimName(),
                        TokenType.ACCESS_TOKEN.getTokenName()
                ),
                userDetails,
                ACCESS_TOKEN_EXPIRATION_TIME_MIN
        );
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            Integer expirationMinutes
    ) {
        var jwtHeader = Jwts
                .builder()
                .header()
                .keyId(KEY_ID);


        return Jwts
                .builder()
                .header()
                .keyId(Jwts.SIG.HS256.getId())
                .and()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, AppUserDetails user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername()) &&
                !isTokenExpired(token) &&
                isTokenIssuedAfterLastTokenRevocation(token, user)
        );
    }

    private boolean isTokenIssuedAfterLastTokenRevocation(String token, AppUserDetails user) {
        Date tokenIssuedAt = extractIssueDate(token);
        return tokenIssuedAt.after(Date.from(user.getLastIssuedTokenRevocation()));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(Instant.now()));
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
