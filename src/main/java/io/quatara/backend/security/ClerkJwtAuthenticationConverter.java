package io.quatara.backend.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class ClerkJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        Object claim = source.getClaim("roles");

        if(claim != null) {
            List<String> roles = source.getClaimAsStringList("roles");
            if(roles != null) {
                authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .collect(Collectors.toList());
            }else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
        }else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }


        ClerkUserPrincipal principal = new ClerkUserPrincipal(source, authorities);
        return new JwtAuthenticationToken(source, authorities, source.getSubject()){
            @Override
            public Object getPrincipal() {
                return principal;
            }
        };
    }
}
