# 코드 리뷰 Fix 태스크

## 1. Backend - 입력 검증 (HIGH)

- [x] DTO에 JSR-303 검증 애너테이션 추가 (`@NotBlank`, `@NotNull` 등)
  - `ReminderRequest`: title 필수, listId 필수
  - `ReminderListRequest`: name 필수, color 필수
- [x] Controller에 `@Valid` 적용
- [x] `Priority.valueOf()` 실패 시 예외 처리 (`IllegalArgumentException` → 400)
- [x] `GlobalExceptionHandler`에 `IllegalArgumentException`, `MethodArgumentNotValidException` 핸들러 추가

## 2. Backend - Cascade & 데이터 무결성 (HIGH)

- [x] `ReminderList` 삭제 시 하위 Reminder cascade 삭제 설정
- [x] ReminderList-Reminder 양방향 관계에서 orphan 레코드 방지

## 3. Backend - 성능 (MEDIUM)

- [x] 목록 조회 API에서 리마인더 카운트를 함께 반환 (프론트 N+1 제거)
- [x] `ReminderRepository` 쿼리에 `countByListIdAndCompletedFalse` 추가하여 N+1 방지
- [x] `findByListId` 정렬 기준을 `displayOrder` 우선으로 변경

## 4. Backend - 예외 처리 & 로깅 (MEDIUM)

- [x] `GlobalExceptionHandler`에 기본 예외 핸들러 추가 (500 에러 표준화)
- [x] 에러 응답 형식 표준화 (error code, message, timestamp 포함)
- [x] Service 클래스에 로깅 추가 (`@Slf4j`)

## 5. Backend - 테스트 보강 (MEDIUM)

- [x] 유효하지 않은 입력값 테스트 (null title, 잘못된 priority 등)
- [x] 존재하지 않는 listId로 리마인더 생성 테스트
- [x] ReminderList 삭제 시 cascade 동작 테스트
- [x] `GlobalExceptionHandler` 예외 응답 테스트

---

## 6. Frontend - 로직 오류 (HIGH)

- [x] `page.tsx`: `loadLists`의 `useCallback` 의존성에서 `selectedListId` 제거
- [x] `ReminderListView.tsx`: useEffect 의존성에서 `editData` 제거, `useRef`로 최신값 참조

## 7. Frontend - 성능 (HIGH)

- [x] `loadAllCounts` N+1 제거: 백엔드 카운트 API 활용으로 교체
- [x] `handleDataChange`에서 불필요한 전체 카운트 재조회 최적화

## 8. Frontend - UX & 접근성 (MEDIUM)

- [x] `confirm()` 대신 커스텀 확인 모달 컴포넌트 구현
- [x] API 호출 실패 시 사용자 에러 피드백 추가 (toast 등)
- [x] `ListModal`: Escape 키로 닫기, 포커스 트랩 추가
- [x] `Sidebar` 컨텍스트 메뉴: 키보드 내비게이션, 화면 밖 방지

## 9. Frontend - 설계 (MEDIUM)

- [x] 에러 바운더리 컴포넌트 추가
- [x] 선택된 목록 상태 URL 파라미터로 유지 (새로고침 시 복원)

## 10. Frontend - CSS & 반응형 (LOW)

- [x] `100vh` → `100dvh` 변경 (모바일 주소바 대응)
- [x] 사이드바, 모달, subtext 등 하드코딩된 너비값 반응형으로 개선
- [x] 모바일 뷰포트 대응 (사이드바 축소)