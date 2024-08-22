# Github Action 
GitHub 에서 제공하는 CI/CD DevOps 파이프라인 자동 플랫폼이다. 깃허브 액션의 작동 단계는 코드를 작성하고 특정 이벤트 트리거를 통해서 '실행기'에서 정의된 작업을 수행한다.  

## 워크플로우?
이벤트 발생시, 어떠한 행위를 할 것인가에 대한 작업을 정의한다.

## 워크플로우(Workflow) 구성 요소
1. Job : 워크플로우 내 작동하는 작업 단위
2. Runner(작업기) : 작업(Job)별 별도의 공간에서 실행, 작업 공간에 대한 정의다. 작업간 내용 공유는 기본적으로 제공되지 않는다.
3. Step : 작업(Job) 내 개별 실제 수행되는 액션에 대한 정의 (단계들의 묶음)

````yaml
name: Basic Sample # 워크플로우를 식별할 수 있는 이름 
on: workflow_dispatch # 이벤트 트리거를 정의 (workflow_dispatch : 사용자가 수동으로 클릭)
jobs: # 작업 
  build:
    runs-on: ubuntu-latest # runner
    steps: # step은 결국 어떤 내용을 실행할것인가에 대한 정의
    - name: Checkout 
      uses: actions/checkout@v2
    - name: Build project
      run: |
        echo "Build Project"
    - name: Run tests
      run: |
        echo "Run Test"
  deploy: # runner
    needs: build
    runs-on: ubuntu-latest
    steps:
    - name: Deploy to production
      run: |
        echo "Deploying to production server"
````

## 변수 (Variables)
워크 플로우의 값을 동적으로 처리하고 싶은 경우, 해당 값에 따라 수행 값을 바꾸거나 다르게 동작하게 하고 싶은 경우
워크플로우 '파일'에서 사용되는 동적인 값으로 보다 유연한 워크플로우를 사용하게 해주고, 환경 변수(Enviroment Variables), 시크릿(Secret) 값을 젖아하고 접근할 때 사용한다.   
Job(작업) 또는 워크플로우 단계에서 실행되는 명령은 변수를 만들고 읽고 수정 할 수 있다.  

### 특징
- 개별 변수값의 최대 데이터 사이즈는 48kb
- 최대 1000개 조직 변수, 500개 레포지토리 변수
- 변수 우선 순위 - 범위가 작은 것이 우선이다. 동일한 변수명으로 설정했을때 조직 변수 < 워크플로우 변수
- 규칙 : 
  - 이름에 영문 숫자 또는 언더바(_)만 사용 가능하다
  - 공백은 허용하지 않는다
  - GITHUB_로 시작하면 안된다.
  - 숫자로 시작하면 안된다.
  - 대소문자는 구분하지 않는다

  
### 변수 설정 방법
1. 워크플로우 파일에 env 키로 정의하여, 단일 워크플로우에서 정의하는 방식
````yaml
---
name: Variables 1
on: workflow_dispatch
env:
  fruit: Apple
jobs:
  build_1:
    runs-on: ubuntu-latest
    env:
      fruit: Orange
    steps:
    - name: Step 1 
      run: |
        echo "Run Step 1, Make $fruit Juice!"    
    - name: Step 2
      run: |
        echo "Run Step 2, Make $fruit Juice!"    
      env:
        fruit: Strawberry
    - name: Step 3
      run: |
        echo "Run Step 3, Make $fruit Juice!"     
  build_2:
    runs-on: ubuntu-latest
    steps:
    - name: Step 1
      run: |
        echo "Run Step 1, Make $fruit Juice!"
````
각 스텝 내부에 정의되어 있는 변수 값에 따라 fruit이 달라지고, build2의 변수는 상단에 환경변수로 설정해둔 값이 지정되어 출력된다. 

2. Default Variables
GitHub에서 내부적으로 관리 중인 기본 환경 변수, prefix로 GITHUB_* 또는 RUNNER_* 설정된 네이밍을 사용한다.
````yaml
---
name: Variables_2
on: workflow_dispatch
env:
  GITHUB_ACTOR: "caporatang"
jobs:
  build_1:
    runs-on: ubuntu-latest
    steps:
      - name: Step 1
        run: |
          echo "Run Step 1, Say Hello to ${{ env.GITHUB_ACTOR }}"  
          echo "Hello to $GITHUB_ACTOR"
          echo "Event : $GITHUB_EVENT_NAME"
      - name: Step 2
        run: |
          echo "Job: $GITHUB_JOB"
          echo "Run ID : $GITHUB_RUN_ID"
````
  

- 제공되는 Default Variables  

| 변수                      | 내용                        |
|-------------------------|---------------------------|
| GITHUB_ACTION           | 현재 실행중인 Action ID         |
| GITHUB_ACTION_PATH      | 현재 실행중인 Action의 경로        |
| GITHUB_ACTOR            | 워크플로우를 시작한 사람 또는 앱 이름     |
| GITHUB_ACTOR_ID         | 워크플로우를 시작한 사람 또는 앱의 ID    |
| GITHUB_ENV              | 러너 내 변수 설정 파일 경로          |
| GITHUB_EVENT_NAME       | 워크플로우를 트리거한 이벤트 이름        |
| GITHUB_EVENT_JOB        | 현재 실행중인 작업 ID             |
| GITHUB_EVENT_REPOSITORY | 레포지토리 소유자와 이름             |
| GITHUB_EVENT_WORKFLOW   | 워크플로우 이름                  |
| GITHUB_EVENT_ARCH       | 실행기(Runner) 의 architecture |
| GITHUB_EVENT_NAME       | 실행기(Runner) 이름            |
| GITHUB_EVENT_OS         | 실행기(Runner) 의 운영체제        |

3. GitHub 조직(Organization) 또는 레포지토리에서 정의
Repository -> Settings -> Security -> Secrets and variables -> Actions -> Variables  
설정한 변수값은 워크플로우에서 vars로 변수에 접근한다

````yaml
name: Variables_3
on: workflow_dispatch
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v2
      - name: Build project
        run: |
          echo "Build Project ${{ vars.PROJECT_NAME }}"
      - name: Run tests
        run: |
          echo "Run Test ${{ vars.TEST_ENVIRONMENT }}"
  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to production
        run: |
          echo "Deploying to production server ${{ vars.PRODUCTION_SERVER }}"
````

## 작업(Job) 데이터 활용
워크플로우의 경우 작업(Job) 단위로 수행한다. 작업 내 여러 개의 단계(Step)이 있으며, 각 단계(Step)은 정의된 고유의 작업을 실행하는 형태이다.  

### Step 사이의 데이터 전달
$GITHUB_OUTPUT을 활용해 전달할 수 있다. Key=value 형태로 $GITHUB_OUTPUT에 기록되며, steps.<step_id>.outputs.<key> 로 데이터에 접근한다.

````yaml
---
name: Step Output 1
on: workflow_dispatch
jobs:
  test-job:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v2
      - id: generate-random-id
        name: Generate random string
        run: echo "random_id=$RANDOM" >> "$GITHUB_OUTPUT" # random값 지정
      - id: build-project
        name: Build project
        run: |
          echo "Random ID: ${{ steps.generate-random-id.outputs.random_id }}"
      - id: run-tests
        name: Run tests
        run: |
          echo "Random ID: ${{ steps.generate-random-id.outputs.random_id }}"
````

### 작업(Job) - 작업(Job) 끼리의 데이터 전달
Job은 실행될 때 실행기(Runner)에 각각 할당되어 실행되기 때문에 기본적으로 전달이 불가능하다.  
**Artifact**와 같은 다른 기능을 사용하거나 외부 스토리지를 활용하는 형태여야 가능하다.

## 조건 및 연산자 (Expression) 
특정 구문을 프로그래밍 형태로 설정하여 결과를 활용한다.
- ${{ expression }}
  - env : 
    - ENV_EXAMPLE: ${{github.ref == 'refs/heads/main && 'main_branch'||'sub_branch'

### Condition & Operator
1. 조건식 if 키워드를 활용해 작업(Job)의 실행 여부를 결정
````yaml
  name: Condition 1
  on: workflow_dispatch
  jobs:
    build:
      # 레포지토리가 CI_CD_Practice 라면 run
      if: github.repository == 'caporatang/CI_CD_Practice'
      runs-on: ubuntu-latest
      steps:
        - name: Step 1
          run: |
            echo "Run Step 1, Say Hello"
        - name: Step 2
          # 이벤트가 해당 이벤트라면 run
          if: github.event_name == 'xxxxxxx'
          run: |
            echo "Run Step 2, Say Hello"
````

2. 복합 if 문으로 실행
````yaml
  name: Condition 2
  on: workflow_dispatch
  jobs:
    build:
      runs-on: ubuntu-latest
      if: ${{ github.event_name == 'workflow_dispatch' && github.ref == 'refs/heads/main' }}
      steps:
        - name: Say Hello
          run: |
            echo "Say Hello to $GITHUB_ACTOR"    
````

### Functions
조건식 if와 함께, 다양한 기본 함수 사용, 예시코드는 contains를 사용했다.

| 변수                                                  | 내용                     |
|-----------------------------------------------------|------------------------|
| startsWith (searchString, searchValue)              | 특정 문자열로 시작 시, true     |
| endsWith (searchString, searchValue)                | 특정 문자열로 끝날 시, true 반환  |
| format (string, replaceValue(), replaceValue1, ... ) | 특정 포맷의 string 값을 변수로 변경 |
| join ( array, optionalSeparator )                   | Array 배열의 문자열 값을 연결    |

````yaml
  name: Function 1
  on:
    issues:
      # issue 가 생성되거나 수정 되었을 때
      types: [opened, edited, labeled, unlabeled]
  jobs:
    auto-assignee:
      runs-on: ubuntu-latest
      permissions:
        issues: write
      # Labels에 bug 라는 문자열이 있는 경우, 
      # 환경변수에 설정된 BUG_HUNTERS로 issue 담당자를 자동 지정
      if: ${{ contains(github.event.issue.labels.*.name, 'bug') }}
      steps:
        - name: Auto assign issue
          uses: pozil/auto-assign-issue@v1
          with:
            assignees: ${{ vars.BUG_HUNTERS }}
````
### Status check Functions
이전 단계의 성공 또는 실패 여부에 따라 **실행 여부** 결정 가능
````yaml
  jobs:
    build:
      runs-on: ubuntu-lastest
      steps:
        # doSomething
        - name: Job is succeeded
          if: ${{suceess()}}

  jobs:
    build:
      runs-on: ubuntu-lastest
      steps:
        # doSomething
        - name: Job is failed
          if: ${{failure()}}

  jobs:
    build:
      runs-on: ubuntu-lastest
      steps:
        # doSomething
        - name: Job is canceled
          if: ${{canceled()}}

  jobs:
    build:
      runs-on: ubuntu-lastest
      steps:
        # doSomething
        - name: Job is always start
          if: ${{always()}}

````