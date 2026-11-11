# QA 기록

CLAUDE.md 2번 규칙(수정 후 시니어 개발자 관점의 별도 QA)에 따라 진행한 QA의 분석 내용과 수정 결과를 남깁니다. 새 기록은 위에 추가합니다.

| 날짜 | 대상 | 결과 요약 | 문서 |
|---|---|---|---|
| 2026-11-12 | 화면 코드의 숫자 색 4개를 Vibe 팔레트로 옮김 | `InkMuted`·`TrackOff`·`TrackOffInk`·`IconDisabled`를 추가하고 리터럴 6곳을 교체함(값은 같아 화면 변화 없음, 빌드 통과). QA: Nit 2건 중 1건 수정. | [2026-11-12-color-tokens-into-vibe.md](2026-11-12-color-tokens-into-vibe.md) |
| 2026-10-31 | 잠금 화면 위에서 대화상자를 PIN 없이 조작할 수 있는 문제 | 원인: 잠금 화면이 같은 창의 오버레이라 별도 창인 대화상자가 그 위에 뜸. 잠금 화면을 전체 화면 창으로 옮김(에뮬레이터 재현·확인). QA 2차: Major 2·Minor 6·Nit 다수 수정, 재잠금 순간 몇 프레임의 틈은 수용, 가로 화면 키패드 잘림 등 기존 Minor는 후속으로. | [2026-10-31-lock-screen-own-window.md](2026-10-31-lock-screen-own-window.md) |
| 2026-10-30 | 프로세스가 정리된 뒤 다시 열면 PIN 잠금을 건너뛰는 문제 | 원인: 앱을 떠난 시각이 프로세스와 함께 사라짐. 저장 상태에 두고 복원 때도 5분 판단(에뮬레이터 재현·확인, 시나리오 7개 통과). QA: Minor 1·Nit 1 수정. 기존 Major 2(재잠금 때 열린 대화상자가 잠금 위에서 조작 가능 — 재현함, 잠금 뒤 화면의 접근성 노출)는 미수정, 후속으로. | [2026-10-30-pin-relock-after-process-death.md](2026-10-30-pin-relock-after-process-death.md) |
| 2026-10-30 | 사진을 찍고 나면 카메라가 다시 열리는 문제 | 원인: 카메라 사용 중 프로세스가 정리되면 "이미 촬영함" 표시와 찍을 파일 경로가 사라짐. 저장 상태로 옮겨 수정(에뮬레이터 재현·확인). QA: Minor 1 수정, 범위 밖 Major 1(프로세스 정리 후 PIN 잠금 건너뜀)과 초안 유실은 후속으로. | [2026-10-30-camera-relaunch-after-photo.md](2026-10-30-camera-relaunch-after-photo.md) |
| 2026-10-29 | 녹음 압축 저장(m4a), 10분 넘는 녹음은 글로 옮기지 않음 | 5차까지 진행. Major 7건(녹음 유실 2, 편집 중 분석 완료·회전 시 덮어쓰기 5) 모두 수정. 일부 Minor는 알려진 제약으로 남김. 압축 시 크기 약 1/8(에뮬레이터 확인). | [2026-10-29-audio-compression-and-length-limit.md](2026-10-29-audio-compression-and-length-limit.md) |
| 2026-10-29 | 홈 "오늘의 노선" 요약 카드 (가운데 정렬, 6개부터 스크롤, 정류장 사이에만 선) | 1차: Minor 4, Nit 5 → 8건 수정, 1건(이름 폭 1/5 고정)은 의도대로 유지. 2차: Minor 1 수정, Nit 3 유지. 에뮬레이터에서 1·2·6개 확인. | [2026-10-29-today-route-summary.md](2026-10-29-today-route-summary.md) |
| 2026-10-28 | 음성이 일부만 글로 옮겨지는 문제 | 원인: 녹음을 한꺼번에 넣어 엔진이 첫 구간 뒤 종료. 2배속 분할 입력으로 수정(실기기 실험). QA 3차: Major 2, Minor 6, Nit 2 → 모두 수정, 위험 1건 수용. | [2026-10-28-transcription-cut-off.md](2026-10-28-transcription-cut-off.md) |
| 2026-10-28 | 녹음 일시정지·이어서 녹음, 저장 전 음성 변환 | Critical 1, Major 3, Minor 8, Nit 다수 → 모두 수정 또는 의도된 동작으로 정리. 3차 재검토에서 Critical·Major 없음. | [2026-10-28-recording-pause-and-transcript.md](2026-10-28-recording-pause-and-transcript.md) |
