package com.libraryflow.adapter.driving.administrationapi;

import com.libraryflow.app.administration.UserDetail;

/**
 * DTO für die Benutzerdetails über die Admin-REST-API.
 */
public record UserDetailResponse(
        Long id,
        String name,
        String email,
        int activeLoans,
        long overdueLoans,
        int remainingBorrowSlots
) {
    static UserDetailResponse from(UserDetail detail) {
        return new UserDetailResponse(
                detail.id().value(),
                detail.name(),
                detail.email(),
                detail.activeLoans(),
                detail.overdueLoans(),
                detail.remainingBorrowSlots()
        );
    }
}
