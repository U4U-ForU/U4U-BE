# 프로젝트 중심 규칙
코드에선 가독성과 기존의 코드베이스를 잘 지키는 것이 우선입니다.
아키텍쳐 적으로 바뀌는 부분이 있다면 작업전에 꼭 검토요청을 하세요.
API 스펙, DB 구조, 인증/인가 구조 변경은 승인 후 진행합니다.

## 이 프로젝트 관련 정보
사용자가 제출한 아이템을 기준으로 로직이 진행되는 Spring RestAPI입니다.
Redis는 사용자의 JWT(Refresh)를 저장하기 위해 사용되었으며, S3는 이미지를 저장하기 위해 사용되었습니다.
DDD-lite 구조이며, 기존 패키지 구조를 유지합니다.

## 주요 패키지
- `global/S3` - 이미지 관리/조회는 해당 패키지 클래스를 주입받아 사용한다.
- `global/error` - 예외 처리는 해당 패키지의 규약을 따른다.
- `global/security` - JWT, auth 코드는 해당 패키지에서 관리한다.
  JWT 관련 기존 구조는 승인 없이 변경하지 않는다.

## Entity
- 모든 Entity는 `BaseEntity`를 상속한다. `createdAt`, `updatedAt`을 직접 선언하지 않는다.
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`를 기본으로 둔다.
- 생성자는 `private`으로 두고 `@Builder`로 생성한다.
- Enum 컬럼은 `@Enumerated(EnumType.STRING)`을 사용한다. (기본값 ORDINAL 금지)
- 이미지는 파일 경로/URL 문자열만 저장한다. base64 본문을 DB에 넣지 않는다.
- Entity 필드를 추가/변경하면 `db/migration/V{n}__설명.sql`을 반드시 함께 만든다.
  `ddl-auto: validate`라서 마이그레이션 없이는 기동 시 스키마 검증에서 실패한다.
- API로 노출되는 ID는 `public_xxx_id` UUID다. Long PK를 응답이나 URL에 넣지 않는다.
  주의: `getId()`가 PK, `getItemId()`가 UUID로 이름이 직관과 반대다.

## Service
- Controller가 호출하는 메서드 1개당 Service 클래스 1개를 두고,
  진입점 public 메서드는 1개, 이름은 `execute`로 통일한다.
  목록은 `GetXxxListService`, 상세는 `GetXxxDetailService`로 구분한다.
- 유스케이스 내부 전용 헬퍼는 private 메서드, 공유 헬퍼는 `XxxSupport`로 분리한다.
  (`cauldron` 도메인 적용 완료, 나머지는 순차 적용)
- 서비스 빈은 `@Service`, `global` 하위 인프라 클래스만 `@Component`를 쓴다.
- 파일 저장 등 외부 의존 로직은 도메인 Service에 직접 넣지 않고 분리한다.

## 트랜잭션 (중요)
- 아래 협력자 클래스들은 `@Transactional`이 **의도적으로** 없다.
  호출자가 트랜잭션을 열어야 하며, 없이 호출하면 비관적 락이 즉시 풀려 부분 커밋이 발생한다.
  Controller에 직접 주입하지 않으며, 이유를 클래스 Javadoc에 남긴다.
  - `TradeTransactionService`
  - `CauldronRecipeSupport`, `CauldronUserItemSupport`
- 여러 유저의 자산을 다루는 로직은 `TradeTransactionService.lockUsers()`를 거친다.
  (ID 오름차순 일괄 잠금으로 데드락 방지)

## 예외
- 비즈니스 예외는 `BusinessException`을 상속한 전용 클래스를 만들고
  `static final EXCEPTION` 싱글턴으로 던진다. `new BusinessException(...)`을 직접 쓰지 않는다.
  (예: `throw TradePostNotFoundException.EXCEPTION;`)

## DTO
- Request/Response를 분리하고, `presentation/dto/request`, `.../response`에 둔다.
- 목록 조회는 필요한 최소 필드만 담는 `SummaryResponse`를 우선 검토한다.
- 상태 변경 API도 성공 시 응답 DTO를 반환한다. 본문 생략은 요구사항에 명시된 경우만.
- `@Setter`를 기본으로 쓰지 않는다. JSON 요청 DTO는 기본 생성자 + getter 중심.
- `DB`, `PK`, `JWT`, `Entity`, `user_id` 같은 내부 용어를 사용자 대면 응답에 노출하지 않는다.

## 작업 완료 시
- 변경 파일과 구현 내용을 요약한다.
- `./gradlew build` 실행 여부를 확인한다. 못 하면 이유와 수동 검증 방법을 설명한다.
- 남은 리스크나 TODO를 정리한다.

## 공통 명령어
```bash
./gradlew bootRun               # 로컬 실행
./gradlew build                 # 빌드 (테스트 포함)
```
