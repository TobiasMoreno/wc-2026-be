package wc.prode._6.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wc.prode._6.dto.request.LoginRequest;
import wc.prode._6.dto.request.RegisterRequest;
import wc.prode._6.dto.response.AuthResponse;
import wc.prode._6.entity.Role;
import wc.prode._6.entity.User;
import wc.prode._6.exception.BadRequestException;
import wc.prode._6.mapper.UserMapper;
import wc.prode._6.repository.UserRepository;
import wc.prode._6.security.JwtUtil;
import wc.prode._6.service.AuthService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("User with email " + request.getEmail() + " already exists");
        }

        Role userRole = determineUserRole(request.getName(), request.getEmail());

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .pictureUrl(request.getPicture())
                .role(userRole)
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getName(), user.getPictureUrl(), user.getRole().name());

        AuthResponse response = userMapper.toAuthResponse(user);
        response.setToken(token);
        response.setRole(user.getRole().name());

        return response;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    // Si el usuario no existe, lo creamos (OAuth2.0 flow)
                    Role userRole = determineUserRole(request.getName(), request.getEmail());
                    User newUser = User.builder()
                            .email(request.getEmail())
                            .name(request.getName())
                            .pictureUrl(request.getPicture())
                            .role(userRole)
                            .build();
                    return userRepository.save(newUser);
                });

        // Verificar si el usuario debe ser admin (por si cambió su nombre)
        Role newRole = determineUserRole(request.getName(), request.getEmail());
        boolean roleChanged = user.getRole() != newRole;
        if (roleChanged) {
            user.setRole(newRole);
        }

        // Actualizar información si cambió
        boolean nameOrPictureChanged = !user.getName().equals(request.getName()) || 
            (request.getPicture() != null && !request.getPicture().equals(user.getPictureUrl()));
        
        if (nameOrPictureChanged || roleChanged) {
            if (nameOrPictureChanged) {
                user.setName(request.getName());
                if (request.getPicture() != null) {
                    user.setPictureUrl(request.getPicture());
                }
            }
            user = userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getName(), user.getPictureUrl(), user.getRole().name());

        AuthResponse response = userMapper.toAuthResponse(user);
        response.setToken(token);
        response.setRole(user.getRole().name());

        return response;
    }

    /**
     * Determina el rol del usuario basándose en su nombre o email.
     * Si el nombre o email contiene "tobias" o "lautaro" (case-insensitive), se asigna rol ADMIN.
     * De lo contrario, se asigna rol USER.
     */
    private Role determineUserRole(String name, String email) {
        String nameLower = name != null ? name.toLowerCase() : "";
        String emailLower = email != null ? email.toLowerCase() : "";
        
        if (nameLower.contains("tobias") || nameLower.contains("lautaro") ||
            emailLower.contains("tobias") || emailLower.contains("lautaro")) {
            log.info("Assigning ADMIN role to user: {} ({})", name, email);
            return Role.ADMIN;
        }
        
        return Role.USER;
    }
}

