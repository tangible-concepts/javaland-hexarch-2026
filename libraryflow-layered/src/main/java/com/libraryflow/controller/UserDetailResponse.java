package com.libraryflow.controller;

public class UserDetailResponse {

    private Long id;
    private String name;
    private String email;
    private int activeLoans;
    private long overdueLoans;
    private int remainingBorrowSlots;

    public UserDetailResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getActiveLoans() {
        return activeLoans;
    }

    public void setActiveLoans(int activeLoans) {
        this.activeLoans = activeLoans;
    }

    public long getOverdueLoans() {
        return overdueLoans;
    }

    public void setOverdueLoans(long overdueLoans) {
        this.overdueLoans = overdueLoans;
    }

    public int getRemainingBorrowSlots() {
        return remainingBorrowSlots;
    }

    public void setRemainingBorrowSlots(int remainingBorrowSlots) {
        this.remainingBorrowSlots = remainingBorrowSlots;
    }
}
