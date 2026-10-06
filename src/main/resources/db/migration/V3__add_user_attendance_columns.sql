-- 출석 체크 상태. last_attended_date 는 마지막 출석일(한 번도 없으면 NULL),
-- attendance_day 는 7일 주기 안의 일차(0 = 아직 없음, 1~7).
-- 기존 유저는 0 / NULL 로 시작해 첫 출석이 1일차가 된다.
ALTER TABLE user_tbl
    ADD COLUMN last_attended_date DATE NULL,
    ADD COLUMN attendance_day INT NOT NULL DEFAULT 0;
