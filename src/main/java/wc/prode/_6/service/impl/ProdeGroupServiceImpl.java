package wc.prode._6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wc.prode._6.dto.request.JoinGroupRequest;
import wc.prode._6.dto.response.ProdeGroupResponse;
import wc.prode._6.dto.response.RankingEntryResponse;
import wc.prode._6.entity.ProdeGroup;
import wc.prode._6.entity.User;
import wc.prode._6.exception.BadRequestException;
import wc.prode._6.exception.ResourceNotFoundException;
import wc.prode._6.repository.ProdeGroupRepository;
import wc.prode._6.repository.UserRepository;
import wc.prode._6.service.ProdeGroupService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdeGroupServiceImpl implements ProdeGroupService {

    private final ProdeGroupRepository prodeGroupRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ProdeGroupResponse joinGroup(String userEmail, JoinGroupRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        ProdeGroup group = prodeGroupRepository.findByName(request.getGroupName())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with name: " + request.getGroupName()));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), group.getPassword())) {
            throw new BadRequestException("Incorrect password for group");
        }

        // Asociar usuario al grupo
        user.setGroup(group);
        userRepository.save(user);

        return ProdeGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .build();
    }

    @Override
    public List<RankingEntryResponse> getGroupRanking(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        if (user.getGroup() == null) {
            throw new BadRequestException("User is not part of any group");
        }

        ProdeGroup group = user.getGroup();

        // Obtener todos los usuarios del grupo usando el repositorio
        List<User> groupUsers = userRepository.findByGroupId(group.getId())
                .stream()
                .sorted(Comparator.comparing(User::getTotalPoints).reversed()
                        .thenComparing(User::getName))
                .collect(Collectors.toList());

        // Construir respuesta del ranking
        List<RankingEntryResponse> ranking = groupUsers.stream()
                .map(u -> RankingEntryResponse.builder()
                        .userId(u.getId())
                        .userName(u.getName())
                        .userEmail(u.getEmail())
                        .pictureUrl(u.getPictureUrl())
                        .totalPoints(u.getTotalPoints())
                        .position(groupUsers.indexOf(u) + 1)
                        .build())
                .collect(Collectors.toList());

        return ranking;
    }
}

