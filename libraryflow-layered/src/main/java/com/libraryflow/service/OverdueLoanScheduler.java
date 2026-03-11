package com.libraryflow.service;

import com.libraryflow.infrastructure.repository.LoanRepository;
import com.libraryflow.model.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OverdueLoanScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueLoanScheduler.class);

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkOverdueLoans() {
        List<Loan> overdueLoans = loanRepository.findByReturnedFalseAndDueDateBefore(LocalDate.now());
        log.info("Prüfe überfällige Ausleihen: {} gefunden", overdueLoans.size());

        for (Loan loan : overdueLoans) {
            notificationService.notifyOverdueLoans(loan);
        }
    }
}
