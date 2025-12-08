package wc.prode._6.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Esta implementación básica crea un usuario con permisos básicos
        // Puedes expandir esto para cargar usuarios desde la base de datos
        
        // Por ahora, creamos un usuario básico con el email
        // El rol se puede extraer del token si es necesario
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        return User.builder()
                .username(email)
                .password("") // No se usa password en autenticación JWT
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
            
            return User.builder()
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

