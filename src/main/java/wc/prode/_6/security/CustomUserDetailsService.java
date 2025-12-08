package wc.prode._6.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import wc.prode._6.entity.User;
import wc.prode._6.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User appUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        String roleWithPrefix = "ROLE_" + appUser.getRole().name();
        authorities.add(new SimpleGrantedAuthority(roleWithPrefix));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(appUser.getEmail())
                .password("") // No se usa password en autenticación JWT/OAuth2.0
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * Carga un usuario desde un token JWT válido
     * Extrae el rol y otra información del token
     */
    public UserDetails loadUserFromToken(String token) {
        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (role != null && !role.isEmpty()) {
                // Asegurar que el rol tenga el prefijo ROLE_
                String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                authorities.add(new SimpleGrantedAuthority(roleWithPrefix));
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            
            return org.springframework.security.core.userdetails.User.builder()
                    .username(email)
                    .password("")
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
        } catch (Exception e) {
            log.error("Error loading user from token: {}", e.getMessage());
            throw new UsernameNotFoundException("Invalid token", e);
        }
    }
}

