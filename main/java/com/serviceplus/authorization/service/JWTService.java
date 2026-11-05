package com.serviceplus.authorization.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.serviceplus.authorization.dto.UserSessionObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import static com.serviceplus.authorization.utility.Utility.SHA256;

@Service
public class JWTService {

	@Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public String generateToken(UserSessionObject user,String uuid) {
    	Map<String,Object> claims = new HashMap<>();
    	claims.put("sid", uuid);
        return generateToken(claims, user);
    }
    
    public String extractClaimNode(String token) {
    	Claims claim =  Jwts
			        .parserBuilder()
			        .setSigningKey(getSignInKey())
			        .build()
			        .parseClaimsJws(token)
			        .getBody();
        return (String) claim.get("sid");
    }

    public String extractClaim(String token,String node) {
        Claims claim =  Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claim.get(node);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserSessionObject userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserSessionObject user) {
        return buildToken(extraClaims, user, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserSessionObject user,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUserID().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserSessionObject user,String uuid) {
        final String username = extractUsername(token);
        final String oUUId = extractClaimNode(token);
        return   (username.equals(user.getUserID().toString()) && SHA256(oUUId).equals(uuid)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaimsAllowExpired(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims();
        }
    }

    public boolean isTokenExpiredClaim(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public boolean isTokenValidUsingClaims(Claims claims,String clientSid,UserSessionObject user) {
        final String username = claims.getSubject();
        final String oUUId = (String) claims.get("sid");
        return   (username.equals(user.getUserID().toString()) && SHA256(oUUId).equals(clientSid));
    }

}
