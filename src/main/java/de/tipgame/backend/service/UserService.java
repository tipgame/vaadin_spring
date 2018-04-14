package de.tipgame.backend.service;

import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.data.entity.UserStatisticEntity;
import de.tipgame.backend.data.dtos.User;
import de.tipgame.backend.repository.UserRepository;
import de.tipgame.backend.repository.UserStatisticRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private UserStatisticRepository userStatisticRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserStatisticRepository userStatisticRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userStatisticRepository = userStatisticRepository;
        this.modelMapper = new ModelMapper();
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsersSortedByRank() {
        List<UserStatisticEntity> userStatisticEntities = userStatisticRepository.findAll();

        Map<Integer, UserEntity> userEntityMap = userRepository.findAll()
                .stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        List<User> users = userStatisticEntities.stream()
                .map(userStatisticEntity -> buildUserDtoFromEntities(userStatisticEntity,
                        userEntityMap.get(userStatisticEntity.getUserId())))
                .sorted(Comparator.comparing(User::getPoints).reversed())
                .collect(Collectors.toList());

        return users;
    }

    private User buildUserDtoFromEntities(UserStatisticEntity userStatisticEntity, UserEntity userEntity) {
        User user = new User();
        user.setFirstname(userEntity.getFirstname());
        user.setLastname(userEntity.getLastname());
        user.setPoints(userStatisticEntity.getPoints());
        return user;
    }

}
