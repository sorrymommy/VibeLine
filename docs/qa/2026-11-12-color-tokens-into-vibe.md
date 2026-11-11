# 화면 코드의 숫자 색을 Vibe 팔레트로 옮김

## 대상
디자인 시스템을 정리하다 보니, 화면 코드에 `Color(0x…)`로 직접 적힌 색이 4개 있었습니다. 디자인 시스템과 앱 코드가 같은 이름을 쓰도록 이 색들을 `Vibe` 팔레트로 옮겼습니다.

| 새 토큰 | 값 | 쓰는 곳 |
|---|---|---|
| `InkMuted` | C6CEC9 | 상세 화면 역 이름 띠(`StationBand`)의 장소명과 날짜 |
| `TrackOff` | CDD3CF | 음성 재생 진행 막대의 빈 트랙, 설정 스위치가 꺼졌을 때의 트랙, 새 기록 화면의 비활성 저장 버튼 바탕 |
| `TrackOffInk` | 4A544E | 비활성 저장 버튼의 글자 |
| `IconDisabled` | A3ACA6 | `RoundButton`의 비활성 아이콘 |

바꾼 파일은 다음과 같습니다.
- `ui/Theme.kt`
- `ui/components/Route.kt`
- `ui/detail/EntryDetailScreen.kt`
- `ui/settings/SettingsScreen.kt`
- `ui/newentry/NewEntryScreen.kt`

## 1차 QA (별도 서브에이전트)
| 등급 | 문제 | 발생 상황 | 처리 |
|---|---|---|---|
| Nit | 새 토큰 4개 중 `IconDisabled`에만 용도 주석이 없음 | 코드를 읽을 때 | 수정: `// disabled icon on Card` 주석을 붙임 |
| Nit | `TrackOff`라는 이름에 비해 쓰임이 넓고, 비활성 버튼 바탕에도 쓰임 | 이름만 보고 용도를 추측할 때 | 수정하지 않음: 주석에 세 가지 쓰임을 모두 적어 두었고, 세 곳 모두 "꺼짐·비어 있음"을 같은 회색으로 나타내는 것이라 이름 하나로 묶는 게 맞다고 판단함 |

Critical, Major, Minor는 없었습니다.

## 정상으로 확인된 동작
- 바꾼 6곳의 값이 원래 리터럴과 모두 같습니다. 화면은 달라지지 않습니다.
- `AOS/app/src` 아래에서 `Color(0x`는 이제 `Theme.kt`에만 있습니다.
- 수정한 파일들은 모두 `Color.White`나 `Color.Transparent`를 여전히 쓰므로, 쓰지 않게 된 import는 없습니다.
- 수정한 파일은 모두 LF 줄바꿈이고, 한 파일 안에서 CRLF와 LF가 섞인 곳은 없습니다.
- `NewEntryScreen.kt`에는 아직 커밋하지 않은 PIN 작업 변경이 있습니다. 색 변경과 겹치지 않고, 그 변경을 건드리지도 않았습니다.

## 직접 검증한 것
- `gradlew.bat assembleDebug` 빌드가 통과했습니다.

## 아직 확인하지 못한 것
- 에뮬레이터와 실기기 화면은 보지 않았습니다. 값이 그대로라서 화면 변화는 없을 것으로 판단했습니다.
