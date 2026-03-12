package com.libraryflow.domain.model;

public record UserDetail(
        UserId id,
        String name,
        String email,
        int activeLoans,
        long overdueLoans,
        int remainingBorrowSlots
) {

    public static UserDetail from(User user, int activeLoans, long overdueLoans) {
        return new UserDetail(user.getId(), user.getName(), user.getEmail(), activeLoans, overdueLoans, 3 - activeLoans);
    }
}
