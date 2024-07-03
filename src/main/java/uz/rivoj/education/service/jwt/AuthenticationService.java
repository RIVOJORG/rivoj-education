package uz.rivoj.education.service.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    public void authenticate(
            Claims claims,
            HttpServletRequest request
    ) {
        String userId = claims.getSubject();
        List<String> roles = (List<String>) claims.get("roles");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userId, null, getAuthorities(roles)
                );

        authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public List<SimpleGrantedAuthority> getAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
