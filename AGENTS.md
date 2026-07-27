# UFU Backend Agent Guide

## Stack
- Java
- Spring Boot
- JPA
- MySQL
- Redis

## Core Rules
- 백엔드 상세 컨벤션은 `CONVENTIONS.md`를 따른다.
- 이미지 저장 정책은 `STORAGE.md`를 따른다.
- 기존 패키지 구조를 유지한다.
- API 스펙, DB 구조, 인증/인가 구조 변경은 사용자 승인 후 진행한다.
- 불필요한 리팩터링은 하지 않는다.

## Package Rules
- Controller는 `presentation`에 둔다.
- DTO는 `presentation/dto/request`, `presentation/dto/response`로 분리한다.
- Entity는 `domain`에 둔다.
- Repository는 `repository`에 둔다.
- Service는 `service`에 둔다.
  - 현재는 도메인별 하나의 Service 클래스 안에 여러 메서드를 두는 방식을 유지한다.
- 공통 설정/예외/보안/스토리지는 `global` 하위에 둔다.

## Done Definition
- 변경 파일을 요약한다.
- 구현 내용을 요약한다.
- 가능한 경우 `./gradlew build` 또는 관련 테스트 실행 여부를 확인한다.
  - 빌드/테스트 명령을 실행할 수 없는 경우, 그 이유와 수동 검증 방법을 설명한다.
- 테스트 방법을 설명한다.
- 남은 리스크나 TODO를 정리한다.