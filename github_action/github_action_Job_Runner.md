# Job Runner란?
Job이 실행되는 환경을 정의하며, 다양한 OS가 지원된다. OS가 지원되는 이유는 보통 Runner에서 코드를 다운 받고, 빌드를 위한 소프트웨어 설치 및 다양한 사전 실행 작업 및 빌드를 해야하기 때문에 OS환경이 필요할 수 있다.
````yaml
jobs: 
  job_1:
    name: Job 1
    runs-on: ubuntu-latest
    steps: 
      # do something..
````

## Job_Runner 실행 방식
Job Runner는 GitHub Hosting Runner, Self-hosted runner 두 가지 방식으로 나뉜다.  

## GitHub Hosting Runner  
GitHub Action에서 제공하는 공식적인 Runner로 GitHub 에서 관리하는 클라우드 기반 Runner다. GitHub에서 제공하는 클라우드 인프라에서 수행된다.
   - 특징 :
     - 1. 기본 제공 : GitHub 에서 무료로 제공
     - 2. 자동 업데이트 : GitHub 에서 자체 관리하기 때문에 최신 OS와 보안 업데이트를 바로 이용 가능
     - 3. 다양한 환경 : 다양한 운영체제 환경과 버전에 맞춘 Runner 제공
     - 4. 동시성 제한 : 동시에 실행할 수 있는 Job에 제한이 있을 수 있다. (계정 유형과 월간 사용량 제한)

- 제공되는 Hosting Runner
  - Ubuntu :
    - ubuntu-lastest or ubuntu-22.04
    - ubuntu-20.04
  - Windows 
    - windows-alstest or windows-2022
    - windows-2019
  - macOS
    - macos-lastest(Monterey) or macos-12
    - macos-lastest-xl or macos-12-xl
    - macos-11 (Big Sur)

### 단일 OS 설정
inputs를 사용해서 os를 선택하게끔 설정 
````yaml
name: Select Runner
on: 
  workflow_dispatch:
    inputs:
      # default는 ubuntu
      # select 박스를 만들어서 선택하게끔 설정
      os:
        description: 'Runner OS'
        required: true
        default: 'ubuntu-latest'
        type: choice
        options:
        - 'ubuntu-latest'
        - 'windows-latest'
        - 'macos-latest'
jobs:
  build:
    name: Build
    runs-on: ${{ github.event.inputs.os }}
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v2
    - id: run
      name: Run
      run: |
        echo "OS: ${{ github.event.inputs.os}}"
        echo "Hello World"
````
### 멀티 OS 설정
strategy.matrix를 사용해서 1차원 배열 수 만큼 실행한다.
````yaml
name: Multi Runner
on: workflow_dispatch
jobs:
  build:
    name: Multi OS Build
    # 3개의 키워드가 배열로 설정되었으니, 총 3번 각각의 os로 실행된다.
    strategy:
      matrix:
        os: [ubuntu-latest, windows-latest, macos-latest]
    runs-on: ${{ matrix.os }}
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v2
    - id: run
      name: Run
      run: |
        echo "$RUNNER_OS"
        echo "Hello World"
````
### 멀티 OS 설정 - 2차원 배열 설정
strategy.matrix로 os, version을 정의한다.   
os 배열 2개 X version 배열 3개 = 총 6개 job을 실행한다.
````yaml
name: Multi Matrix Runner
on: workflow_dispatch
jobs:
  build:
    name: Multi OS Build
    strategy:
      matrix:
        # ubuntu-20.04에 node-version 10 12 14
        # ubuntu-22.04에 node-version 10 12 14
        os: [ubuntu-20.04, ubuntu-22.04]
        version: [10, 12, 14]
    runs-on: ${{ matrix.os }}
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v2
    - id: setup-node
      name: Setup Node.js
      uses: actions/setup-node@v2
      with:
        node-version: ${{ matrix.version }}
````
## Self-hosted Runner  
GitHub Action에서 제공하는 공식 Runner가 아닌 사용자가 직접 호스팅하고 관리하는 Runner로 사용자의 로컬 서버 또는 클라우드 인스턴스에서 실행한다.  
EC2나 서버에서 별도의 설정이 필요하며, 워크플로우 코드도 따로 작성해주어야한다.
- 특징 :
  - 자체 관리 : 사용자가 직접 관리하므로, 특정 보안 정책이나 네트워크 설정 등을 적용할 수 있다는 장점.
  - 맞춤 설정 : 원하는 운영체제, 환경, 패키지 설치 등을 자유롭게 구성 가능
  - 비용 발생 : 인프라를 직접 관리함에 따른 추가 비용 발생

## Job 동시성 이해
Job은 기본적으로 동시 실행이다. 동일 workflow안에 별다른 설정이 없다면 동시에 병렬 수행이 기본 정책이다. 어떤 Job이 가장 먼저 실행될지 또는 가장 먼저 끝날지는 알 수 없다.

### 동시 실행되는 workflow
아래 워크플로우를 실행하면 job 1,2,3,4 전부 다 동시에 시작된다. 끝나는 시간도 각각이다. 
````yaml
name: Concurrency Job
on: workflow_dispatch
jobs:
  job-1:
    name: Concurrency Job Test 1
    runs-on: ubuntu-latest
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-1
      name: Test 1
      run: echo "Test 1"
    - id: test-2
      name: Test 2
      run: echo "Test 2"
  job-2:
    name: Concurrency Job Test 2
    runs-on: ubuntu-latest
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-3
      name: Test 3
      run: echo "Test 3"
    - id: test-4
      name: Test 4
      run: echo "Test 4"
  job-3:
    name: Concurrency Job Test 3
    runs-on: ubuntu-latest
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-5
      name: Test 5
      run: echo "Test 5"
    - id: test-6
      name: Test 6
      run: echo "Test 6"
  job-4:
    name: Concurrency Job Test 4
    runs-on: ubuntu-latest
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-7
      name: Test 7
      run: echo "Test 7"
    - id: test-8
      name: Test 8
      run: echo "Test 8"
````
### 동시 작업 (with needs)
같은 workflow 내 Job 간에는 needs 를 활용하여 Job 간 종속성을 설정할 수 있고, 이전 작업이 반드시 종료된 후, 후속으로 이어서 하는 Job인 경우에 주로 사용한다.
`````yaml
name: Concurrency Job with needs
on: workflow_dispatch
jobs:
  job-1:
    name: Concurrency Job Test 1
    runs-on: ubuntu-latest
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-1
      name: Test 1
      run: echo "Test 1"
    - id: test-2
      name: Test 2
      run: echo "Test 2"
  job-2:
    name: Concurrency Job Test 2
    runs-on: ubuntu-latest
    # Job 2는 1이 끝나고 실행
    needs: job-1
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-3
      name: Test 3
      run: echo "Test 3"
    - id: test-4
      name: Test 4
      run: echo "Test 4"
  job-3:
    name: Concurrency Job Test 3
    runs-on: ubuntu-latest
    # job3은 job2가 끝나고 실행
    needs: job-2
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-5
      name: Test 5
      run: echo "Test 5"
    - id: test-6
      name: Test 6
      run: echo "Test 6"
  job-4:
    name: Concurrency Job Test 4
    runs-on: ubuntu-latest
    # job4는 job3이 끝나고 실행
    needs: job-3
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-7
      name: Test 7
      run: echo "Test 7"
    - id: test-8
      name: Test 8
      run: echo "Test 8"
`````
### 동시 작업 (with Groups)
동시 작업 Group를 설정하여, Group 내 작업(Job)의 동시 수행을 하나만 되도록 설정 가능하다.
Job1,2 같은 그룹 3,4는 다른 그룹이다.   
이는 각각 다른 그룹이기 때문에 Job 1 3 이 먼저 실행된다.
````yaml
name: Concurrency Job with 2 Groups
on: workflow_dispatch
jobs:
  job-1:
    name: Concurrency Job Test 1
    runs-on: ubuntu-latest
    concurrency:
      # group의 이름은 워크플로우와 브랜치 이름을 조합해서 그룹 이름을 설정
      group: ${{ github.workflow }}-${{ github.ref }}-1
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-1
      name: Test 1
      run: echo "Test 1"
    - id: test-2
      name: Test 2
      run: echo "Test 2"
  job-2:
    name: Concurrency Job Test 2
    runs-on: ubuntu-latest
    concurrency:
      # group의 이름은 워크플로우와 브랜치 이름을 조합해서 그룹 이름을 설정
      group: ${{ github.workflow }}-${{ github.ref }}-1
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-3
      name: Test 3
      run: echo "Test 3"
    - id: test-4
      name: Test 4
      run: echo "Test 4"
  job-3:
    name: Concurrency Job Test 3
    runs-on: ubuntu-latest
    concurrency:
      # group의 이름은 워크플로우와 브랜치 이름을 조합해서 그룹 이름을 설정
      group: ${{ github.workflow }}-${{ github.ref }}-2
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-5
      name: Test 5
      run: echo "Test 5"
    - id: test-6
      name: Test 6
      run: echo "Test 6"
  job-4:
    name: Concurrency Job Test 4
    runs-on: ubuntu-latest
    concurrency:
      # group의 이름은 워크플로우와 브랜치 이름을 조합해서 그룹 이름을 설정
      group: ${{ github.workflow }}-${{ github.ref }}-2
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v3
    - id: test-7
      name: Test 7
      run: echo "Test 7"
    - id: test-8
      name: Test 8
      run: echo "Test 8"
````

## Job 권한
실행하고자 하는 행위가 GitHub 이나, Issue, Discussion, Pages 등 해당 서비스에 접근하기 위해 권한을 필요로 하는 경우가 존재한다.  
이런 상황에서는 permissions 라는 설정을 통해 관련 서비스에 접근할 권한을 자동으로 획득하도록 설정할 수 있다.  
````yaml
# do something..
permissions:
  contents: read
  pull-requests: write
  issues: write
  discussion: read
  packages: write
# do something...
````
당연히 따로 적절한 권한 설정을 하지 않으면, 403 에러가 발생한다.  
- 설정 가능 옵션   

| permission      | 내용                                           |
|-----------------|----------------------------------------------|
| actions         | GitHub Action 작업 수행 권한 설정 ex) action 실행 취소 수행 |
| checks          | 검사 실행                                        |
| contents        | Repository 컨텐츠 접근 작업                         |
| discussions     | GitHub discussion 접근 작업                      |
| id-token        | OIDC (OpenID Connect) 토큰을 가져오는 작업            |
| issues          | GitHub Issue 접근 작업                           |
| pages           | GitHub Pages 접근 작업                           |
| pull-Requests   | Repository 의 Pull Request 관련 작업              |
| security-events | GitHub 코드 검사 및 Dependabot 경고 관련 작업           |'

권한 설정은 read / write / none 으로 설정 가능하다. 
- write : 해당 서비스에 쓰기 및 편집 접근
- read : 해당 서비스에 읽기만 가능
- none : 지정하지 않음. permissions에 키를 지정하지 않으면 모두 none으로 설정된다.