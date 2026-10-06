package com.ufu.domain.user.presentation.dto.response;

import com.ufu.domain.user.domain.User;
import lombok.Getter;

@Getter
public class AttendanceResponse {
    private final int attendanceDay;

    private final int reward;

    private final int currency;

    public AttendanceResponse(User user, int reward) {
        this.attendanceDay = user.getAttendanceDay();
        this.reward = reward;
        this.currency = user.getCurrency();
    }
}
