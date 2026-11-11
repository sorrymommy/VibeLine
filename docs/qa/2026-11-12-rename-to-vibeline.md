# 앱 이름을 VibeDiary에서 VibeLine으로 변경

## 대상
앱이 아직 배포 전이라, 이름에 묶인 곳을 모두 새 이름으로 바꿨습니다.

| 항목 | 이전 | 이후 |
|---|---|---|
| 패키지 이름, namespace, applicationId | `com.vibediary.app` | `com.vibeline.app` |
| 소스 폴더 | `AOS/app/src/main/java/com/vibediary/app` | `.../com/vibeline/app` (`git mv`) |
| Room 스키마 폴더 | `AOS/app/schemas/com.vibediary.app.data.AppDatabase` | `com.vibeline.app.data.AppDatabase` |
| 앱 이름(`app_name`), 테마 | `VibeDiary`, `Theme.VibeDiary` | `VibeLine`, `Theme.VibeLine` |
| DB 파일, SharedPreferences | `vibediary.db`, `vibediary`, `vibediary_lock` | `vibeline.db`, `vibeline`, `vibeline_lock` |
| 갤러리 사본 폴더와 파일 이름 앞부분 (`MediaExport`) | `VibeDiary` | `VibeLine` |
| 문구 | 잠금 화면의 PIN 분실 안내, 설정의 갤러리 사본 설명 | 새 이름으로 바꿈 |
| 기타 | `settings.gradle.kts`의 rootProject.name, `AOS/README.md`, `CLAUDE.md` 제목, 아이콘 벡터의 주석 | 새 이름으로 바꿈 |

지난 QA 문서(`docs/qa`)는 당시 기록이므로 그대로 두었습니다.

## 1차 QA (별도 서브에이전트)
| 등급 | 문제 | 발생 상황 | 처리 |
|---|---|---|---|
| Major (콘솔 작업) | Maps API 키가 `com.vibediary.app` + SHA-1로 제한되어 있으면 지도 타일이 인증에 실패함 | 새 패키지로 지도를 열 때. 키가 있는지만 검사하므로 빈 회색 격자만 보이고 오류 문구는 나오지 않음 | 코드 문제가 아님. 사용자가 Google Cloud Console에서 키의 Android 앱 제한에 `com.vibeline.app` + SHA-1을 추가해야 함 |
| Major (콘솔 작업) | Drive OAuth Android 클라이언트는 패키지 하나에 묶여 있음 | 새 앱에서 드라이브를 연결할 때 DEVELOPER_ERROR로 실패 | 코드 문제가 아님. **같은 Cloud 프로젝트**에 `com.vibeline.app` + SHA-1로 Android OAuth 클라이언트를 새로 만들어야 함. 같은 프로젝트여야 기존 appDataFolder 백업을 읽을 수 있음 |
| Minor | 기기 안의 옛 데이터는 새 앱으로 자동으로 옮겨지지 않음 | 옛 앱을 먼저 지울 때 | 순서를 안내함: 옛 앱에서 미디어 업로드를 켜고 동기화 → 새 앱에서 같은 계정으로 연결해 복원 확인 → 옛 앱 삭제 |
| Minor | 드라이브 연결 실패 토스트가 원인과 관계없이 "인터넷 연결과 구글 계정을 확인해 주세요" 하나뿐임 | 콘솔 설정이 빠졌을 때 원인을 오해하게 됨 | 수정하지 않음. 원래 있던 코드이고 이번 변경 범위 밖이라 후속으로 남김 |
| Nit | `.claude/settings.local.json`에 허용해 둔 adb 명령이 옛 패키지를 가리킴 | 다음에 에뮬레이터로 자동 검증할 때 | 수정하지 않음. 사용자의 권한 설정 파일이라 직접 바꾸지 않고 사용자에게 알림 |
| Nit | 이미 남긴 갤러리 사본은 `VibeDiary` 앨범에 그대로 있음 | 갤러리에 앨범이 둘로 보임 | 수정하지 않음. 옛 앱이 만든 파일이라 새 앱이 건드릴 수 없고, 기능에는 영향이 없음 |
| Nit | `.idea/workspace.xml`과 저장소 폴더 이름이 아직 VibeDiary임 | IDE에서 | 수정하지 않음. 로컬 IDE 파일임. 폴더와 GitHub 저장소 이름은 사용자가 정함 |

## 정상으로 확인된 동작
- 코드, 리소스, gradle, manifest에 옛 이름이 남은 곳이 없습니다. "diary"는 일반명사로 쓰인 주석에만 남아 있습니다.
- FileProvider authority가 `${applicationId}.files`, 코드에서는 `packageName` 기준이라 새 패키지를 자동으로 따라갑니다.
- WorkManager 작업 이름과 Drive 파일 이름(`entry_<id>.json`)은 앱 이름과 관계없습니다.
- 새로 설치하는 것이라 DB나 prefs 이름이 바뀌어도 마이그레이션 문제가 없습니다. `lastPullTime`이 비어 있어 처음 동기화할 때 백업 전체를 받아 옵니다.
- 문구 두 곳이 앱 정보에 보이는 이름과 갤러리 폴더 이름과 맞습니다.
- git에 소스 34개 파일과 스키마 2개가 이름 변경으로 기록되어 히스토리가 유지됩니다.
- 커밋하지 않은 PIN 작업과 색 토큰 변경은 패키지와 import 줄만 바뀌었고, 다른 부분은 깨지지 않았습니다.

## 직접 검증한 것
- `gradlew.bat clean assembleDebug` 빌드가 통과했습니다.

## 아직 확인하지 못한 것
- 실기기에 새 패키지로 설치한 뒤의 동작은 보지 않았습니다. 지도 타일, 드라이브 연결, 백업 복원이 여기에 해당하며, 콘솔 설정을 한 뒤에 확인해야 합니다.
- Google Cloud Console에 실제로 어떻게 설정되어 있는지는 볼 수 없었습니다.
