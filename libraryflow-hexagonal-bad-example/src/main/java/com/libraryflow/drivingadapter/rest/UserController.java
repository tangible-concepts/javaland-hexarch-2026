package com.libraryflow.drivingadapter.rest;

import com.libraryflow.drivingadapter.rest.dto.UserDetailDTO;
import com.libraryflow.domain.model.User;
import com.libraryflow.domain.model.UserId;
import com.libraryflow.domain.drivingport.ManageUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ManageUser manageUser;

    public UserController(ManageUser manageUser) {
        this.manageUser = manageUser;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return manageUser.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDetailDTO getUserById(@PathVariable Long id) {
        return UserDetailDTO.from(manageUser.findUserDetailById(UserId.of(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody Map<String, String> request) {
        return manageUser.createUser(request.get("name"), request.get("email"));
    }
}
