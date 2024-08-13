package uz.rivoj.education.service.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;

    private AuthenticationService authenticationService;

    private final RedisTemplate<String, String> redisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        Jws<Claims> claimsJws = jwtUtil.extractToken(token);
        String phoneNumber = claimsJws.getBody().getSubject();
        String storedToken = redisTemplate.opsForValue().get(phoneNumber);
        System.out.println("phoneNumber = " + phoneNumber);
        System.out.println("storedToken = " + storedToken);

        if (!token.equals(storedToken)){
            System.out.println("checking>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            filterChain.doFilter(request, response);
            return;
        }

        authenticationService.authenticate(claimsJws.getBody(), request);

        filterChain.doFilter(request, response);
    }
}
