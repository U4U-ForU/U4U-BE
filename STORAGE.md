# UFU Storage Guide

## Current Decision

현재 UFU는 AWS 계정과 S3 Bucket이 준비되지 않았기 때문에 실제 S3 연동을 구현하지 않습니다.

아이템 제출 이미지는 백엔드 로컬 디렉토리에 저장하고, DB에는 이미지 파일 자체가 아니라 저장된 경로 문자열만 저장합니다.

```yaml
file:
  upload-dir: uploads
```

## Local Storage Flow

1. 클라이언트가 `multipart/form-data`로 아이템 이름, 설명, 이미지 파일을 전송합니다.
2. Controller는 인증된 사용자를 확인하고 요청을 Service로 전달합니다.
3. `ItemSubmissionService`는 이미지 저장을 `StorageService`에 위임합니다.
4. 현재 구현체인 `LocalStorageService`가 파일을 `uploads` 디렉토리에 저장합니다.
5. 저장 파일명은 원본 파일명을 그대로 쓰지 않고 UUID 기반으로 생성합니다.
6. DB에는 `/uploads/{uuid}.{extension}` 형식의 경로만 저장합니다.

## Validation

- 이미지 파일은 필수입니다.
- 허용 확장자는 `jpg`, `jpeg`, `png`, `gif`, `webp`입니다.
- `Content-Type`은 `image/`로 시작해야 합니다.
- 파일명 충돌 방지를 위해 UUID 파일명을 사용합니다.

## S3 Migration Plan

S3가 준비되면 현재 도메인 코드를 크게 바꾸지 않고 저장소 구현체만 교체합니다.

- 유지할 인터페이스: `StorageService`
- 현재 구현체: `LocalStorageService`
- 추후 구현체 예시: `S3StorageService`

S3 전환 시 필요한 작업:

- AWS SDK 의존성 추가
- S3 Bucket 설정
- Access Key / Secret Key 또는 IAM Role 설정
- `S3StorageService` 구현
- 운영 환경에서 `StorageService` 구현체 선택 방식 결정

현재 단계에서는 AWS SDK, S3 인증 정보, Bucket 설정, 실제 S3 연결 테스트를 추가하지 않습니다.

## API Request Format

아이템 제출 API는 파일 업로드를 위해 `multipart/form-data`를 사용합니다.

```text
POST /api/items/submissions
Authorization: Bearer {AccessToken}
Content-Type: multipart/form-data
```

Fields:

- `name`: 아이템 이름
- `description`: 아이템 설명
- `image`: 이미지 파일
