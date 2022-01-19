package com.crf.server.rest.security;

import static com.crf.server.rest.security.SecurityConstants.HEADER_STRING;
import static com.crf.server.rest.security.SecurityConstants.SECRET;
import static com.crf.server.rest.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.crf.server.rest.exception.AuthenticateException;

import io.jsonwebtoken.Jwts;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(AuthenticationManager authManager, UserDetailsService userDetailsService) {
        super(authManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected final void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            try {
                String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody().getSubject();
                if (user != null) {
                    MyUserDetails myUserDetails = (MyUserDetails) userDetailsService.loadUserByUsername(user);
                    return new UsernamePasswordAuthenticationToken(myUserDetails, null, myUserDetails.getAuthorities());
                }
            } catch (Exception e) {
                throw new AuthenticateException(e.getMessage());
            }
            return null;
        } else {
            throw new AuthenticateException("Missing authentication token");
        }
    }
}
