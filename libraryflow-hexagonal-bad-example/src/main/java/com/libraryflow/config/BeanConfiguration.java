package com.libraryflow.config;

import com.libraryflow.domain.drivenport.FindBooks;
import com.libraryflow.domain.drivenport.FindLoans;
import com.libraryflow.domain.drivenport.FindUsers;
import com.libraryflow.domain.drivenport.UpdateBook;
import com.libraryflow.domain.drivenport.RecordLoan;
import com.libraryflow.domain.drivenport.RegisterUser;
import com.libraryflow.domain.service.LibraryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Explizites Wiring: Domain-Service wird hier erstellt und verdrahtet
// Der LibraryService selbst hat KEINE Spring-Annotationen!
@Configuration
public class BeanConfiguration {

    @Bean
    public LibraryService libraryService(
            FindBooks findBooksPort,
            UpdateBook updateBookPort,
            FindUsers findUsersPort,
            RegisterUser registerUserPort,
            FindLoans findLoansPort,
            RecordLoan recordLoanPort) {
        return new LibraryService(
                findBooksPort,
                updateBookPort,
                findUsersPort,
                registerUserPort,
                findLoansPort,
                recordLoanPort
        );
    }
}
