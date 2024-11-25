package uz.rivoj.education.service.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import uz.rivoj.education.entity.UserEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.expiry}")
    private Integer expiry;

    @Value("${jwt.secret}")
    private String secret;

    private final RedisTemplate<String, String> redisTemplate;

    public List<String> generateToken(UserEntity user) {
        Date iat = new Date();
        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime() + expiry))
                .addClaims(getAuthorities(user))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        String oldToken = redisTemplate.opsForValue().get(user.getId().toString());
        if (oldToken != null) {
            redisTemplate.delete(oldToken);
        }
        String refreshToken = generateRefreshToken(user);
        List<String> tokens = new ArrayList<>();
        tokens.add(token);
        tokens.add(refreshToken);
        redisTemplate.opsForValue().set(String.valueOf(user.getId()), token);
        return tokens;

    }
    public String generateRefreshToken(UserEntity user) {
        Date iat = new Date();
        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime() + 1296000000L))
                .addClaims(getAuthorities(user))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        String oldToken = redisTemplate.opsForValue().get(user.getId() + "r");
        System.out.println("Old Token = " + oldToken);
        if (oldToken != null) {
            redisTemplate.delete(oldToken);
        }
        System.out.println("New token = " + token);
        redisTemplate.opsForValue().set(user.getId() + "r", token);
        return token;
    }
    public boolean checkRefreshToken(UserEntity user, String token){
        String oldRefreshToken = redisTemplate.opsForValue().get(user.getId() + "r");
        Date expiration = extractToken(oldRefreshToken).getBody().getExpiration();
        if(!expiration.after(new Date())){
            return false;
        }
        assert oldRefreshToken != null;
        return oldRefreshToken.equals(token);
    }

    public Jws<Claims> extractToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token);
    }

    public Map<String, Object> getAuthorities(UserEntity user) {
        return Map.of("roles",
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());
    }
}
