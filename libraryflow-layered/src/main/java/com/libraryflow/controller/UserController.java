package com.libraryflow.controller;

import com.libraryflow.model.Loan;
import com.libraryflow.model.User;
import com.libraryflow.service.LoanService;
import com.libraryflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoanService loanService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDetailResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        List<Loan> activeLoans = loanService.getActiveLoansForUser(id);

        long overdueLoans = activeLoans.stream()
                .filter(loan -> loan.getDueDate().isBefore(LocalDate.now()))
                .count();

        UserDetailResponse response = new UserDetailResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setActiveLoans(activeLoans.size());
        response.setOverdueLoans(overdueLoans);
        response.setRemainingBorrowSlots(3 - activeLoans.size());

        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
