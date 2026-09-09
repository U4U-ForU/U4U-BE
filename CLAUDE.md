# 프로젝트 컨텍스트
코드에선 가독성과 기존의 코드베이스를 잘 지키는 것이 우선입니다.
아키텍쳐 적으로 바뀌는 부분이 있다면 작업전에 꼭 검토요청을 하세요.

## 이 프로젝트 관련 정보
사용자가 제출한 아이템을 기준으로 로직이 진행되는 Spring RestAPI입니다.
Redis는 사용자의 JWT(Refresh)를 저장하기 위해 사용되었습니다.
S3는 이미지를 저장하기 위해 사용되었습니다.

## 주요 패키지
'~/global/S3' - 이미지 관리 및 조회는 해당 패키지의 클레스를 의존성 주입받아 사용한다.
'~/global/error' - 에러 핸들링, 에러 관리 등 에외처리는 해당 패키지의 규약을 따른다.
'~/global/security' - 인증/인가를 위한 JWT, auth 코드는 해당 패키지에서 사용한다.

## 규칙
- 모든 Entity는 `BaseEntity`를 상속한다.
- Entity 필드를 추가/변경하면 `db/migration/V{n}__설명.sql`을 반드시 함께 만든다.
  `ddl-auto: validate`라서 마이그레이션 없이는 기동 시 스키마 검증에서 실패한다.
- 비즈니스 예외는 `BusinessException`을 상속한 전용 클래스를 만들고
  `static final EXCEPTION` 싱글턴으로 던진다. `new BusinessException(...)`을 직접 쓰지 않는다.
  (예: `throw TradePostNotFoundException.EXCEPTION;`)
- API로 노출되는 ID는 `public_xxx_id` UUID다. Long PK를 응답이나 URL에 넣지 않는다.
  주의: `getId()`가 PK, `getItemId()`가 UUID로 이름이 직관과 반대다.
- `TradeTransactionService`는 `@Transactional`이 없다. 호출자가 트랜잭션을 열어야 하며,
  없이 호출하면 락이 즉시 풀려 부분 커밋이 발생한다.
- 여러 유저의 자산을 다루는 로직은 `TradeTransactionService.lockUsers()`를 거친다.
  (락 순서 고정으로 데드락 방지)

## 공통 명령어
```bash
./gradlew bootRun               # 로컬 실행
./gradlew build                 # 빌드 (테스트 포함)
```

## 참고 사항
