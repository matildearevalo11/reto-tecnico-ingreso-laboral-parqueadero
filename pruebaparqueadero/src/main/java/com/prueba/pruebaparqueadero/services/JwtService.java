package com.prueba.pruebaparqueadero.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.prueba.pruebaparqueadero.entities.Token;
import com.prueba.pruebaparqueadero.repositories.TokenRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY="586E3272357538782F413F4428472B4B6250655368566B597033733676397924";
    private final TokenRepository tokenRepository;

    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String,Object> extraClaims, UserDetails user) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*360))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmailFromToken(String token) {
            return getClaim(token, Claims::getSubject);
        }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = getEmailFromToken(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenRevoked(token));
    }



    private Claims getAllClaims(String token){
            return Jwts
                    .parserBuilder().setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }

        public <T> T getClaim(String token, Function<Claims,T> claimsResolver){
            final Claims claims=getAllClaims(token);
            return claimsResolver.apply(claims);
        }

        private Date getExpiration(String token){
            return getClaim(token, Claims::getExpiration);
        }

        private boolean isTokenExpired(String token) {
            return getExpiration(token).before(new Date());
        }

        private boolean isTokenRevoked(String token) {
            return tokenRepository.findByToken(token).isPresent();
        }

        public void revokeToken(String token) {
            Date expiration = new Date();

            Token revokedToken = Token.builder()
                    .token(token)
                    .expiration(expiration)
                    .build();
            tokenRepository.save(revokedToken);
            System.out.println("Token revocado: " + token);
        }

    public Long getUserId(String token) {
        Integer idInteger = getClaim(token, claims -> claims.get("id", Integer.class));
        return idInteger != null ? idInteger.longValue() : null;
    }

    }
