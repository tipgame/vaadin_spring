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
            createMatchUserConnections(user);
            createNewStatisticForUser(user);
        } else {
            result.rejectValue("firstname", "error.firstname", "foo");
        }

        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", registrationDto);
        }
        else {
            return new ModelAndView("login", "user", registrationDto);
        }
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

    private boolean doesUserAlreadyExists(RegistrationDto userRegistration) {
        return userRepository.findByUsername(userRegistration.getUsername()) != null;
    }
}
