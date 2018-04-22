package de.tipgame.backend.controller;


import de.tipgame.backend.data.entity.GameMatchEntity;
import de.tipgame.backend.data.entity.UserEntity;
import de.tipgame.backend.data.dtos.RegistrationDto;
import de.tipgame.backend.data.entity.UserMatchConnectionEntity;
import de.tipgame.backend.data.entity.UserStatisticEntity;
import de.tipgame.backend.repository.UserRepository;
import de.tipgame.backend.repository.UserStatisticRepository;
import de.tipgame.backend.service.GameMatchService;
import de.tipgame.backend.service.UserMatchConnectionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.Registration;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserController {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private GameMatchService gameMatchService;
    private ModelMapper modelMapper;
    private UserMatchConnectionService userMatchConnectionService;
    private UserStatisticRepository userStatisticRepository;

    @Autowired
    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          GameMatchService gameMatchService,
                          UserMatchConnectionService userMatchConnectionService,
                          UserStatisticRepository userStatisticRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.gameMatchService = gameMatchService;
        this.userMatchConnectionService = userMatchConnectionService;
        this.userStatisticRepository = userStatisticRepository;
        modelMapper = new ModelMapper();
    }

    // Return registration form template
    @RequestMapping(value="user/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationPage(ModelAndView modelAndView, RegistrationDto registrationDto){
        modelAndView.addObject("user", registrationDto);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, value = "user/register")
    public ModelAndView processRegistrationForm(@Valid RegistrationDto registrationDto,
                                                BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        Boolean shouldRegisterNewUser = validateRegistration(result, registrationDto);

        if (shouldRegisterNewUser) {
            UserEntity user = modelMapper.map(registrationDto, UserEntity.class);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("player");
            userRepository.save(user);
            createMatchUserConnections(user);
            createNewStatisticForUser(user);
        }

        if (result.hasErrors()) {
            modelAndView.setViewName("registration");
        }
        else {
            modelAndView.addObject("successMessage", "Der Benutzer wurde erfolgreich angelegt.");
            modelAndView.addObject("registrationDto", new RegistrationDto());
            modelAndView.setViewName("registration");
        }

        return modelAndView;
    }

    private Boolean validateRegistration(BindingResult result, RegistrationDto registrationDto) {
        Boolean isRegistrationValid = true;

        if(registrationDto.getFirstname() == null || registrationDto.getFirstname().isEmpty()) {
            result.rejectValue("firstname", "error.user","Bitte das Feld ausfüllen.");
            isRegistrationValid = false;
        }

        if (registrationDto.getLastname() == null || registrationDto.getLastname().isEmpty()) {
            result.rejectValue("lastname", "error.user","Bitte das Feld ausfüllen.");
            isRegistrationValid = false;
        }

        if (registrationDto.getPassword() == null || registrationDto.getPassword().isEmpty()) {
            result.rejectValue("password", "error.user","Bitte das Feld ausfüllen.");
            isRegistrationValid = false;
        }

        if (registrationDto.getEmail() == null || registrationDto.getEmail().isEmpty()) {
            result.rejectValue("email", "error.user","Bitte das Feld ausfüllen.");
            isRegistrationValid = false;
        }

        if (registrationDto.getRetypepassword() == null || registrationDto.getRetypepassword().isEmpty()) {
            result.rejectValue("retypepassword", "error.user","Bitte das Feld ausfüllen.");
            isRegistrationValid = false;
        }
        else if(!doPPasswordsMatch(registrationDto)) {
            result.rejectValue("retypepassword", "error.user","Die Passwörter stimmen nicht überein.");
            isRegistrationValid = false;
        }

        if (registrationDto.getUsername() == null || registrationDto.getUsername().isEmpty()) {
            result.rejectValue("username", "error.user","Bitte das Feld ausfüllen.");
            isRegistrationValid = false;
        }
        else if (isUserAlreadyRegistered(registrationDto)) {
            result.rejectValue("username", "error.user","Der Benutzername ist bereits vorhanden.");
            isRegistrationValid = false;
        }

        if (registrationDto.getRegistrationcode() == null || registrationDto.getRegistrationcode().isEmpty()) {
            result.rejectValue("registrationcode", "error.user","Bitte das Feld ausfüllen.");
            isRegistrationValid = false;
        }
        else if (!registrationDto.getRegistrationcode().equals("A")) {
            result.rejectValue("registrationcode", "error.user","Der Code für die Registrierung stimmt nicht.");
            isRegistrationValid = false;
        }

        return isRegistrationValid;
    }

    private void createMatchUserConnections(UserEntity user)
    {
        List<UserMatchConnectionEntity> userMatchConnections = new ArrayList<UserMatchConnectionEntity>();
        Iterable<GameMatchEntity> listOfMatchesInPrelim = gameMatchService.getAllMatches();


        for (GameMatchEntity gameMatch : listOfMatchesInPrelim) {
            UserMatchConnectionEntity userMatchConnection = new UserMatchConnectionEntity();
            userMatchConnection.setGameMatchId(gameMatch.getMatchId());
            userMatchConnection.setUserId(user.getId());
            userMatchConnection.setResultTippAwayTeam("");
            userMatchConnection.setResultTippHomeTeam("");
            userMatchConnection.setAlreadyProcessed(false);
            userMatchConnection.setRound(gameMatch.getRound());
            userMatchConnections.add(userMatchConnection);
        }
        userMatchConnectionService.saveUserMatchConnections(userMatchConnections);
    }

    private void createNewStatisticForUser(UserEntity user)
    {
            UserStatisticEntity statistic = new UserStatisticEntity();
            statistic.setUserId(user.getId());
            userStatisticRepository.save(statistic);
    }

    private boolean isUserAlreadyRegistered(RegistrationDto userRegistration) {
        return userRepository.findByUsername(userRegistration.getUsername()) != null;
    }

    private boolean doPPasswordsMatch(RegistrationDto registrationDto) {
        return registrationDto.getPassword().equals(registrationDto.getRetypepassword());
    }
}
