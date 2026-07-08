# UFU Backend Code Conventions

## Branch And Git

### Branches

- 기본 개발 기준 브랜치는 `develop`입니다.
- 기능 작업은 항상 최신 `develop`에서 새 브랜치를 생성해 진행합니다.
- `main`은 배포 가능한 안정 브랜치로 두고 직접 작업하지 않습니다.
- 브랜치명은 작업 목적이 드러나게 작성합니다.

| Type | Example |
| --- | --- |
| Feature | `feature/item-submission-api` |
| Feature | `feature/frontend-api-integration` |
| Fix | `fix/swagger-auth` |
| Docs | `docs/git-convention` |

### Commit Units

- 너무 작은 수정마다 커밋하지 않고, 의미 있는 작업 단위로 커밋합니다.
- 기능 1차 구현, 버그 수정, 설정 변경, 문서 수정은 가능하면 분리합니다.
- 여러 작업이 섞이면 커밋을 나누는 것을 권장합니다.
- 빌드가 완전히 깨진 상태는 가능하면 커밋하지 않습니다.
- 작업 백업이 필요하면 `WIP` 커밋을 사용할 수 있습니다.

### Commit Message Types

기본 형식은 `type: short description`입니다.

- 설명은 필요한 기술 용어만 영어로 쓰고, 내용은 한글로 작성해도 됩니다.
- 제목은 너무 길게 쓰지 않습니다.
- 작업 범위가 여러 타입에 걸치면 커밋을 나누는 것을 우선합니다.

| Type | When To Use | Example |
| --- | --- | --- |
| `feat` | 새로운 기능 추가 | `feat: 아이템 제출 API 추가` |
| `fix` | 버그 수정 | `fix: 아이템 제출 Swagger 인증 설정 수정` |
| `refactor` | 기능 변화 없이 코드 구조 개선 | `refactor: storage service 분리` |
| `chore` | 빌드, 설정, 의존성, 개발환경 변경 | `chore: 로컬 파일 저장 설정 추가` |
| `docs` | 문서 수정 | `docs: Git 컨벤션 추가` |
| `style` | 포맷팅, 세미콜론, 들여쓰기 등 로직 변화 없는 수정 | `style: item controller 포맷 정리` |
| `test` | 테스트 코드 추가/수정 | `test: item submission service 테스트 추가` |
| `rename` | 파일/패키지명 변경 | `rename: item request dto 이름 변경` |
| `remove` | 불필요한 코드/파일 삭제 | `remove: 사용하지 않는 mock 데이터 삭제` |

### Codex Commit Rules

- Codex는 사용자가 명시적으로 커밋을 요청한 경우에만 `commit`을 수행합니다.
- 커밋 전 반드시 `git status`로 변경사항을 확인합니다.
- 커밋 전 변경 파일 목록과 포함 범위를 사용자에게 요약합니다.
- 커밋 메시지는 이 문서의 컨벤션에 맞춰 제안하거나 작성합니다.
- `git push`는 사용자가 명시적으로 요청한 경우에만 수행합니다.
- 사용자 승인 없이 `add`, `commit`, `push`, `reset`, `rebase`, `restore`, `force push`를 수행하지 않습니다.

### Push

- `commit`은 작업 단위로 비교적 자주 수행할 수 있습니다.
- `push`는 기능이 어느 정도 안정됐거나 하루 작업 종료 시점에 권장합니다.
- `develop` 병합 전에는 작업 브랜치를 원격에 push합니다.
- 빌드 실패 상태를 원격에 올리는 것은 가능하면 피합니다.

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
- Request DTO에는 필요한 validation을 추가합니다.
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
