package de.tipgame.app;

import de.tipgame.app.security.SecurityConfig;
import de.tipgame.backend.controller.UserController;
import de.tipgame.backend.repository.UserRepository;
import de.tipgame.backend.service.UserService;
import de.tipgame.ui.AppUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.vaadin.spring.events.annotation.EnableEventBus;


@SpringBootApplication(scanBasePackageClasses = { AppUI.class, Application.class, UserService.class,
        SecurityConfig.class, UserRepository.class, UserController.class})
@EnableEventBus
@EntityScan("de.tipgame.backend.data.entity")
@EnableJpaRepositories("de.tipgame.backend.repository")
public class Application extends SpringBootServletInitializer {

    public static final String APP_URL = "/";
    public static final String LOGIN_URL = "/login.html";
    public static final String LOGOUT_URL = "/login.html?logout";
    public static final String LOGIN_FAILURE_URL = "/login.html?error";
    public static final String LOGIN_PROCESSING_URL = "/login";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
