package com.libraryflow.drivingadapter.rest.dto;

import com.libraryflow.domain.model.UserDetail;

public record UserDetailDTO(
        Long id,
        String name,
        String email,
        int activeLoans,
        long overdueLoans,
        int remainingBorrowSlots
) {

    public static UserDetailDTO from(UserDetail detail) {
        return new UserDetailDTO(
                detail.id().value(),
                detail.name(),
                detail.email(),
                detail.activeLoans(),
                detail.overdueLoans(),
                detail.remainingBorrowSlots()
        );
    }
}
