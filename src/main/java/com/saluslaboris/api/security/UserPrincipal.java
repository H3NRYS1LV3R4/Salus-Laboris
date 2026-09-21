package com.saluslaboris.api.security;


import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UserPrincipal(Integer id, Integer idPersona, Integer idRol,
                            String username, String password, boolean enabled,
                            Collection<? extends GrantedAuthority> authorities) implements UserDetails {
    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }
    @Override public boolean isEnabled() { return enabled; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public String toString() { return "UserPrincipal[id=" + id + "]"; }
}
