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

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .pictureUrl(request.getPicture())
                .role(Role.USER)
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
                    User newUser = User.builder()
                            .email(request.getEmail())
                            .name(request.getName())
                            .pictureUrl(request.getPicture())
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });

        // Actualizar información si cambió
        if (!user.getName().equals(request.getName()) || 
            (request.getPicture() != null && !request.getPicture().equals(user.getPictureUrl()))) {
            user.setName(request.getName());
            if (request.getPicture() != null) {
                user.setPictureUrl(request.getPicture());
            }
            user = userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getName(), user.getPictureUrl(), user.getRole().name());

        AuthResponse response = userMapper.toAuthResponse(user);
        response.setToken(token);
        response.setRole(user.getRole().name());

        return response;
    }
}

