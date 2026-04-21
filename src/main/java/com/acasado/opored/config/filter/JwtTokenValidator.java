package com.acasado.opored.config.filter;

import com.acasado.opored.util.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Get the token from the request header
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7); // Removes the unnecessary "Bearer"

            // Check the validity of the token
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

            // Get the user and their permissions
            String user = jwtUtils.extractUser(decodedJWT);
            String authoritiesString = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();
            if (authoritiesString == null) authoritiesString = "";
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesString);

            // Add the user and its authorities to the spring security context holder
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }
        // If token is not present, it continues with the next filter and fails the authentication
        filterChain.doFilter(request, response);
    }
}
