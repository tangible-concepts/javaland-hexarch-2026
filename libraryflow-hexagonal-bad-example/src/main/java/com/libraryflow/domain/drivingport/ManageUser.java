package com.libraryflow.domain.drivingport;

import com.libraryflow.domain.model.Loan;
import com.libraryflow.domain.model.User;
import com.libraryflow.domain.model.UserDetail;
import com.libraryflow.domain.model.UserId;

import java.util.List;

public interface ManageUser {

    User findById(UserId userId);

    UserDetail findUserDetailById(UserId userId);

    List<User> findAllUsers();

    User createUser(String name, String email);

    List<Loan> findActiveLoansForUser(UserId userId);
}
