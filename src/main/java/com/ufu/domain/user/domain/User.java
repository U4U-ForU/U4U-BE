package com.ufu.domain.user.domain;

import com.ufu.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "user_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    public static final int CYCLE_DAYS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, length = 20)
    private String loginId;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "currency", nullable = false)
    private int currency;

    @Column(name = "last_attended_date")
    private LocalDate lastAttendedDate;

    @Column(name = "attendance_day", nullable = false)
    private int attendanceDay;

    @Builder
    private User(String loginId, String password, String email, String nickname, Role role) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.currency = 20;
        this.attendanceDay = 0;
    }

    public boolean hasEnoughCurrency(int amount) {
        return currency >= amount;
    }

    public void deductCurrency(int amount) {
        this.currency -= amount;
    }

    public void increaseCurrency(int amount) {
        this.currency += amount;
    }

    public boolean hasAttendedOn(LocalDate date) {
        return date.equals(lastAttendedDate);
    }

    public void attend(LocalDate today) {
        this.attendanceDay = attendanceDay % CYCLE_DAYS + 1;
        this.lastAttendedDate = today;
    }

    public int currentAttendanceReward() {
        return rewardOf(attendanceDay);
    }

    // 1~3일 4, 4~6일 6, 7일 20
    private static int rewardOf(int day) {
        if (day == CYCLE_DAYS) {
            return 20;
        }
        if (day <= 3) {
            return 4;
        }
        return 6;
    }
}
