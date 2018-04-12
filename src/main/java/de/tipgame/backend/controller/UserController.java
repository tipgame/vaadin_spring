package de.tipgame.backend.controller;


import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.dtos.RegistrationDto;
import de.tipgame.backend.repository.UserRepository;
import de.tipgame.backend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserController {

    private UserRepository userRepository;
    private UserService userService;
    private final PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;

    @Autowired
    public UserController(UserRepository userRepository,
                          UserService userService,
                          PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = new ModelMapper();;
    }

    // Return registration form template
    @RequestMapping(value="user/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationPage(ModelAndView modelAndView, RegistrationDto registrationDto){
        modelAndView.addObject("user", registrationDto);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, value = "user/register", produces = APPLICATION_JSON_VALUE)
    public ModelAndView processRegistrationForm(ModelAndView modelAndView,
                                                @Valid RegistrationDto registrationDto,
                                                BindingResult result,
                                                WebRequest request,
                                                Errors errors) {

        Boolean isUserRegistered = doesUserAlreadyExists(registrationDto);
        if (!isUserRegistered) {
            UserEntity user = modelMapper.map(registrationDto, UserEntity.class);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("player");
            userRepository.save(user);
        } else {
            result.rejectValue("email", "message.regError");
        }

        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", registrationDto);
        }
        else {
            return new ModelAndView("login", "user", registrationDto);
        }
    }

    private boolean doesUserAlreadyExists(RegistrationDto userRegistration) {
        return userRepository.findByUsername(userRegistration.getUsername()) != null;
    }
}
