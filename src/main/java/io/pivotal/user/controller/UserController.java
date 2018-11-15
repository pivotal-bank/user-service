package io.pivotal.user.controller;

import io.pivotal.user.domain.RegistrationRequest;
import io.pivotal.user.domain.User;
import io.pivotal.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

/**
 * REST controller for the accounts microservice. Provides the following
 * endpoints:
 * <p>
 * <ul>
 * <li>GET <code>/users/{id}</code> retrieves the user with given id.
 * <li>POST <code>/users</code> stores the account object passed in body.
 * </ul>
 * <p>
 *
 * @author David Ferreira Pinto
 * @author Maxim Avezbakiev
 * @author Simon Rowe
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {


    /**
     * The service to delegate calls to.
     */
    @Autowired
    private UserService service;

    /**
     * REST call to retrieve the account with the given id as userId.
     *
     * @param email The id of the user to retrieve.
     * @return The user object if found.
     */
    @GetMapping("{email}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable("email") String email) {
        log.info("UserController.find: username=" + email);
        return this.service.get(email);
    }

    /**
     * REST call to save the user provided in the request body.
     *
     * @param registrationRequest The user to save.
     * @return
     */
    @PostMapping(value = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User save(@Valid @RequestBody RegistrationRequest registrationRequest) {
        log.debug("Attempting to register user with email address=" + registrationRequest.getEmail());
        return this.service.register(registrationRequest);
    }

}
