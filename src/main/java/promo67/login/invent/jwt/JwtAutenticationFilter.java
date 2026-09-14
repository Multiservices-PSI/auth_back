package promo67.login.invent.jwt;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAutenticationFilter extends OncePerRequestFilter{

    private final JwtService serv;
    private final UserDetailsService uds;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response, FilterChain filterChain) 
        throws ServletException, IOException {

            final String token = getTokenFromRequest(request);
            final String username;

            if(token == null){
                filterChain.doFilter(request, response);
                return;
            }

            username = serv.getUsernameFromToken(token);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails user = uds.loadUserByUsername(username);

                if(serv.isTokenValid(token, user)){
                    UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken
                    (user, null, user.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(auth) &&  auth.startsWith("Bearer ")){
            return auth.substring(7);
        }
        return null;
    }



}
