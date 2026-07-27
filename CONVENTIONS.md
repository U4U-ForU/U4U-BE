# UFU Backend Code Conventions

## Backend Package Structure

UFU 백엔드는 DDD-lite 스타일을 사용합니다.

```text
domain
  item
    domain
    repository
    service
    presentation
      dto
        request
        response
global
  config
  entity
  error
  security
  storage
```

- Controller는 각 도메인의 `presentation`에 둡니다.
- DTO는 `presentation/dto/request`, `presentation/dto/response`로 분리합니다.
- Entity는 각 도메인의 `domain`에 둡니다.
- Repository는 각 도메인의 `repository`에 둡니다.
- 핵심 비즈니스 로직은 각 도메인의 `service`에 둡니다.
- 공통 설정은 `global/config`에 둡니다.
- 공통 Entity 기반 클래스는 `global/entity`에 둡니다.
- 예외 처리는 `global/error` 스타일을 따릅니다.
- Security/JWT 코드는 `global/security` 아래에 둡니다.
- 파일 저장처럼 외부 저장소로 교체 가능한 기능은 `global/storage`에 둡니다.
- 현재 단계에서는 `global.response` 패키지를 만들지 않습니다.

## DTO

- Request DTO와 Response DTO를 분리합니다.
- 목록 조회와 상세 조회의 응답 DTO는 필요한 데이터가 다르면 분리합니다.
- 목록 조회는 화면 목록 렌더링에 필요한 최소 필드만 담는 `SummaryResponse` 사용을 우선 검토합니다.
- 상세 조회, 생성, 수정, 취소 응답은 화면 갱신에 필요한 충분한 필드를 담는 `Response`를 사용할 수 있습니다.
- 단순히 DTO 수를 줄이기 위해 목록 API에 사용하지 않는 긴 설명, 본문, 내부 값을 과하게 포함하지 않습니다.
- Request DTO에는 필요한 validation을 추가합니다.
- DTO에는 `@Setter`를 기본으로 사용하지 않습니다.
- JSON 요청 DTO는 기본 생성자와 getter 중심으로 구성합니다.
- `multipart/form-data`의 `@ModelAttribute` 바인딩처럼 setter가 필요해 보이는 경우에도 먼저 Controller의 바인딩 방식이나 전용 요청 객체를 검토합니다.
- Swagger 설명이 필요한 필드는 `@Schema`를 사용합니다.
- 사용자 화면이나 API 응답에 내부 구현 용어를 과하게 노출하지 않습니다.
- `DB`, `PK`, `JWT`, `Entity`, `user_id` 같은 용어를 사용자-facing 응답/문구에 직접 노출하지 않습니다.

## Entity Time Fields

- JPA Entity의 생성/수정 시간은 `BaseEntity`를 상속해 관리합니다.
- `BaseEntity`는 `@CreatedDate`, `@LastModifiedDate`를 사용합니다.
- 각 Entity에 `createdAt`, `updatedAt` 필드를 반복해서 선언하지 않습니다.

## Entity Constructors

- Entity는 `@NoArgsConstructor(access = AccessLevel.PROTECTED)`를 기본으로 둡니다.
- 생성자는 외부에서 직접 호출하지 않도록 `private`으로 두고, 필요한 생성은 `@Builder`를 사용합니다.
- 생성 시점에 기본 상태값이 필요한 경우 생성자 내부에서 명확히 지정합니다.
- Enum은 문자열 저장을 기본으로 검토하고, JPA에서는 `@Enumerated(EnumType.STRING)`을 사용합니다.

## Image Fields

- DB에는 이미지 파일 자체가 아니라 저장된 파일 경로 또는 URL 문자열만 저장합니다.
- 현재 아이템 제출 API는 로컬 파일 저장 후 반환된 `/uploads/{fileName}` 형식의 경로를 저장합니다.
- base64 data URL처럼 긴 이미지 본문을 DB에 직접 저장하지 않습니다.
- S3 같은 외부 저장소가 필요해지면 `StorageService` 구현체를 교체하는 방식으로 확장합니다.

## Service

- Controller는 요청/응답 처리와 인증 사용자 추출에 집중합니다.
- 핵심 비즈니스 로직은 Service에 위치합니다.
- 파일 저장 같은 외부 의존 로직은 도메인 Service 안에 직접 넣지 않고 별도 Service 또는 인터페이스로 분리합니다.

## Swagger

- Controller에는 `@Tag`를 사용해 API 그룹을 설명할 수 있습니다.
- API 메서드에는 `@Operation`을 사용해 동작을 설명할 수 있습니다.
- API별 주요 성공/실패 응답은 `@ApiResponses`로 문서화합니다.
- 공통 에러 형식은 `ErrorResponse` schema로 표시합니다.
- DTO 필드에는 `@Schema`를 사용해 요청/응답 예시를 제공합니다.
- 인증이 필요한 API는 Swagger에서 Bearer 토큰을 넣고 테스트할 수 있도록 `@SecurityRequirement`를 사용합니다.
- Swagger 설명은 사용자와 개발자가 이해하기 쉬운 문장으로 작성합니다.

## Auth And Security

- JWT 관련 기존 구조는 사용자 승인 없이 임의로 변경하지 않습니다.
- 비밀번호 저장 시 `PasswordEncoder.encode`로 BCrypt 해시를 저장합니다.
- 로그인 검증 시 `PasswordEncoder.matches`를 사용합니다.
- 회원가입 기본 Role은 `USER`로 둡니다.
- `ADMIN`은 추후 관리자 기능이 생겼을 때 별도로 부여합니다.

## Frontend

- 현재 프론트엔드는 React Router가 아니라 `App`의 `view` 상태 기반 화면 전환을 유지합니다.
- 기존 화면과 기능을 임의로 삭제하지 않습니다.
- 사용자 화면에는 개발자 내부 용어를 노출하지 않습니다.
