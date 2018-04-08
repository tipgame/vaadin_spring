package de.tipgame.backend.controller;


import de.tipgame.backend.dtos.RegistrationDto;
import de.tipgame.backend.repository.UserRepository;
import de.tipgame.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserController {

    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    public UserController(UserRepository userRepository,
                          UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "user/register", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity register(@RequestBody RegistrationDto registrationDto) {
        return userService.registerUser(registrationDto);
    }
}
