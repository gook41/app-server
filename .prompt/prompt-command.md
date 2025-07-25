---
filename: prompt-command.md
description: 프로젝트의 작업 요청 형식을 정의하는 문서입니다.
gemini_cli: 사용자 명령어 파싱, 적절한 응답 형식 선택, 워크플로 실행, 명령어별 맞춤 처리
---

# INDEX

## Command Formats

### General Request

```
-command_type

**설명:**
(description of the task)

**파일:**
(list of files)

**예시 코드:**
(optional example code or links)

```

### Error Fix Request

```
-error

**설명:**
(error symptoms and reproduction steps)

**에러 로그:**
(error logs)

**파일:**
(files to modify)

```

### Code Review Request

```
-review
설명:
(summary of changes and review points)

PR 링크 (선택):
(PR/commit/issue URL)

파일:
review.md

```
### Test Request

```
-test

설명:
(test scope, target methods, boundary conditions)

파일:
(relevant source and test files)

```

### Feature Implementation Request

```
-code
설명:
(task description)

파일:
(list of relevant files)
```


---


### Documentation Request

```
-doc
설명:
(task description)

파일:
(list of relevant files)
```

---

### Read Request

```
-read
설명: 
(task description)

참고:
.prompt\read.md

```

---

```
-schedule 
 
설명:
(Understand project schedule and perform actions based on options. If no option, suggest tasks with estimated time and impact.)

옵션:{ /update , /new , /add , /log }

- /update: 
  (Summarize progress and update .prompt\app-server-progress.md REPORT section. Milestones and logs may change.)
  
- /new: (Check goal achievement, overwrite with new milestones. If unachieved, add to issue.md.)
  - 예시: 
    ```
    - 제목: 프로젝트 초기 설정
      - 시작일: 2025-07-22
      - 종료일: 2025-07-23
      - 목표: 
        1. 예1) 프로젝트 AI 프롬프트 설계            
    ```
- /add: (Add new goal lists.)
    - 예시: 
        ```
        - 제목: 프로젝트 초기 설정
        - 시작일: 2025-07-22
        - 종료일: 2025-07-23
        - 목표: 
        1. 예1) 프로젝트 AI 프롬프트 설계  
        2. 예2) 일정 수립 << 이전 마일스톤에 추가
        ```
- /log: (Record project changes from previous log to current time, with timestamp.)
    - 예시:
        ```
        [YYYY-MM-DD HH:MM] 작업 요약
        - 주요 변경 사항 1
        - 주요 변경 사항 2
        ---
        ```



파일:
.prompt\app-server-roadmap.md
.prompt\app-server-progress.md

```




```





