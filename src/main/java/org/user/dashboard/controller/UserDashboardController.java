package org.user.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.user.dashboard.exception.UserDashboardException;
import org.user.dashboard.exception.UserNotFoundException;
import org.user.dashboard.model.User;
import org.user.dashboard.service.UserService;

@RestController
@RequestMapping("/users/{id}")
public class UserDashboardController {

    private final UserService userService;

    public UserDashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> getUserDetails(@PathVariable String id)
            throws UserDashboardException, UserNotFoundException {

        return ResponseEntity.ok(userService.getUser(id));
    }

}
