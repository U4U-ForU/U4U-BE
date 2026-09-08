-- password 컬럼에는 BCrypt 해시만 저장되며 길이는 항상 60자로 고정이다.
-- baseline 시점의 varchar(255)를 엔티티 정의(@Column(length = 60))에 맞춘다.
-- 기존 행의 값도 모두 60자이므로 잘리는 데이터는 없다.
ALTER TABLE user_tbl MODIFY password VARCHAR(60) NOT NULL;
