package de.tipgame.backend.service;

import java.util.Optional;

import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.dtos.RegistrationDto;
import de.tipgame.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserEntity findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public ResponseEntity registerUser(RegistrationDto registrationDto) {
		return new ResponseEntity(HttpStatus.OK);
	}

}
