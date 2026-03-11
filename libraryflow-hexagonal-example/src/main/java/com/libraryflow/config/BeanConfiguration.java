package com.libraryflow.config;

import com.libraryflow.app.service.AdministrationService;
import com.libraryflow.app.service.LoanService;
import com.libraryflow.app.service.UserManagementService;
import com.libraryflow.app.service.CatalogService;
import com.libraryflow.app.service.DunningService;
import com.libraryflow.app.ports.driven.forcatalogmanagement.BookCatalog;
import com.libraryflow.app.ports.driven.forcatalogmanagement.LoanManagement;
import com.libraryflow.app.ports.driven.fornotifications.NotificationSender;
import com.libraryflow.app.ports.driven.forusermanagement.UserRepository;
import com.libraryflow.app.ports.driving.foradministration.ForAdministration;
import com.libraryflow.app.ports.driving.forloans.ForLoans;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Explizites Wiring aller Domain-Services.
 *
 * In der hexagonalen Architektur wird die Verdrahtung der Domain-Services mit den Ports
 * in einer zentralen Konfiguration vorgenommen. Die Domain-Services selbst tragen keine
 * Spring-Annotationen — sie bleiben frei von Framework-Abhängigkeiten.
 * Nur die Adapter (Driven + Driving) und diese Konfiguration kennen Spring.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public ForLoans forLoans(BookCatalog bookCatalog, LoanManagement loanManagement, UserRepository userRepository) {
        return new LoanService(bookCatalog, loanManagement, userRepository);
    }

    @Bean
    public CatalogService catalogService(BookCatalog bookCatalog) {
        return new CatalogService(bookCatalog);
    }

    @Bean
    public UserManagementService userManagementService(UserRepository userRepository, LoanManagement loanManagement) {
        return new UserManagementService(userRepository, loanManagement);
    }

    @Bean
    public ForAdministration forAdministration(CatalogService catalogService, UserManagementService userManagementService) {
        return new AdministrationService(catalogService, userManagementService);
    }

    @Bean
    public DunningService dunningService(LoanManagement loanManagement, UserRepository userRepository,
                                         BookCatalog bookCatalog, List<NotificationSender> notificationSenders) {
        return new DunningService(loanManagement, userRepository, bookCatalog, notificationSenders);
    }
}
