package io.quatara.backend.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

@Getter
public class ClerkUserPrincipal {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Jwt jwt;

    public ClerkUserPrincipal(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        this.jwt = jwt;
        this.authorities = authorities;
        this.id = jwt.getSubject(); // Clerk User ID maps to 'sub'
        this.firstName = jwt.getClaim("first_name") != null ? jwt.getClaimAsString("first_name") : "";
        this.lastName = jwt.getClaim("last_name") != null ? jwt.getClaimAsString("last_name") : "";
        this.email = jwt.getClaimAsString("email");
    }
}
