# Workflow
자동화 작업의 가장 상위 개념으로, 하나 잇앙의 작업(Job)을 실행시키는 자동화 구성, GitHub 레포지토리(Repository) 내 ./github/workflows 라는 디렉토리에 YAML 파일 단위로 정의된다.  
레포지토리에서 이벤트(푸시, 이슈 생성)가 발생할 때 자동으로 실행한다.  

## 구성
- name : UI에 표기될 워크플로우 이름
- on : 워크플로우 실행을 위한 트리거를 정의
- jobs : 워크플로우에서 실행하고자 하는 작업에 대한 정의로, 하나 이상의 작업(job) 으로 구성된다. 각 작업은 별도의 실행기(Runner) 로 구성된다.  

## trigger
워크플로우를 실행하게 하는 이벤트로 트리거를 어떻게 설정하느냐에 따라 워크플로우 실행 조건을 다르게 구성한다.  
1. 트리거의 종류는 4가지가 있다
   - 워크플로우가 위치한 레포지토리에서 발생한 이벤트로 트리거 
   - GitHub 레포지토리 외 관련 서비스에서 발생한 이벤트로 트리거
   - 예약된 시간에 트리거 
   - 수동으로 직접 이벤트 트리거 (workflow_dispatch)

````yaml
name: Trigger when Push
on: 
  push:
    branches:
    # 특정 브랜치에 push가 발생하면 실행
    # 여러 조건도 걸 수 있다.
      - '*'
      - '!master'
      - '!test-**'
      - 'test-**'
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout 
      uses: actions/checkout@v2
    - name: Build project
      run: |
        echo "Build Project"
        echo "Branch: $GITHUB_REF"
    - name: Run tests
      run: |
        echo "Run Test"
  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
    - name: Deploy to production
      run: |
        echo "Deploying to production server"

````
### 워크플로우가 위치한 레포지토리에서 발생한 이벤트로 트리거
- 다양한 이벤트를 통해 워크플로우를 실행할 수 있다.
````yaml
   - on : 
     - push
     - pull_request
     - fork
     - release
````
1. paths-ignore를 활용해, master 브랜치에서 push가 발생하더라도, 특정 파일 (빌드가 필요없는 파일)만 제외 시킬수도있다.
````yaml
on:
  push:
    branches:
       - master
    paths-ignore:
      'README.md'
      '.github/workflows/**'
````
2. Tag가 생성되었을때 "v1.X" 형태의 태그 명일 경우 워크플로우 트리거를 발생시킬수 있다 
````yaml
name: Trigger with Tags
on: 
  push:
    tags:
    - v1.*
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout 
      uses: actions/checkout@v2
    - name: Build project
      run: |
        echo "Build Project"
        echo "GitHub REF: $GITHUB_REF"
    - name: Run tests
      run: |
        echo "Run Test"
  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
    - name: Deploy to production
      run: |
        echo "Deploying to production server"
````
3. pull_request에도 조건을 따로 지정해서 사용할 수 있다. pr의 정보와 특정 유저를 조건으로 걸어서 트리거를 실행할 수 있다
````yaml
  name: PR
  on: 
    pull_request:
      types:
      - opened
      paths:
      - 'trigger/test'
  
  permissions:
    contents: read
    pull-requests: write
  
  jobs:
    trigger:
      if: ${{ github.event.pull_request.user.login != 'xxxxx' }}
      runs-on: ubuntu-latest
      steps:
      - name: "Comment about changes we can't accept"
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          PR: ${{ github.event.pull_request.html_url }}
        run: |
          gh pr edit $PR --add-label "invalid"
          gh pr comment $PR --body "Sorry, we can't accept changes to this repository from your account. Please contact us if you have any questions."
          gh pr close $PR
````
4. 워크플로우가 다른 워크플로우를 트리거 하는 형태로도 사용할 수 있다.
````yaml
name: Workflow Run
on: 
  workflow_run:
     # Trigger with Tags 워크플로우가 실행이 completed 라면 실
    workflows: ["Trigger with Tags"]
    types:
    - completed
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - name: Chec행out 
      uses: actions/checkout@v2
    - name: Build project
      run: |
        echo "Build Project"
    - name: Run tests
      run: |
        echo "Run Test"
  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
    - name: Deploy to production
      run: |
        echo "Deploying to production server"
````
5. 스케줄에 따른 워크플로우 자동 트리거
UTC 기준 설정된 특정 시간에 실행되도록 스케줄링을 설정할 수 있고, 자바의 크론탭과 동일한 문법을 사용해서 설정할 수 있다.
````yaml
name: Schedule for evrery 20 minutes
on: 
  schedule:
  # 20분마다 자동 빌드
  - cron: '*/5 * * * *'
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout 
      uses: actions/checkout@v2
    - name: Build project
      run: |
        echo "Build Project"
    - name: Run tests
      run: |
        echo "Run Test"
  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
    - name: Deploy to production
      run: |
        echo "Deploying to production server"
````

6. 수동 트리거
사용자가 직접 워크플로우를 실행한다.  
Input 구문을 사용해서 워크플로우 실행 시 원하는 값을 전달할 수 있다. 예제는 인풋으로 받은 태그로 빌드 시 태그를 설정하는 예제이다.
````yaml
name: Workflow Dispatch
on: 
  workflow_dispatch:
    inputs:
      tag:
        description: 'Tag to deploy'
        required: true
        default: '1.0.0'
permissions:
  contents: write
env:
  GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
jobs:
  tagging:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout 
      uses: actions/checkout@v2
    - name: Tagging
      run: |
        git config user.email "github-actions[bot]@users.noreply.github.com"
        git config user.name "github-actions[bot]"
        git tag -a -m "Tagging release" ${{ github.event.inputs.tag }}
        git push origin ${{ github.event.inputs.tag }}
  build:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout 
      uses: actions/checkout@v2
    - name: Build project
      run: |
        echo "Build Project"
    - name: Run tests
      run: |
        echo "Run Test"
  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
    - name: Deploy to production
      run: |
        echo "Deploying to production server"
````

## Workflow Artifacts
아티팩트는 개발한 결과, 즉 산출물이다. 보통은 CI에서 빌드 과정 후 생긴 배포 가능한 아웃풋 파일을 의미한다. 그래서 컴파일된 파일이거나 컨테이너 이미지 형태로도 볼 수 있다.
GitHub_Action 에서는 아티팩트 라는 기능을 통해 워크플로우가 실행하는 동안 이전에 동일 업로드된 아티팩트를 다운로드 받을 수 있게 지원한다. 이는 Job마다 별개의 Runner로 작업되어 Job끼리 산출물을 주고 받는것이 제한되기 때문이다.
````yaml
name: Artifacts
on: workflow_dispatch
jobs:
  job-1:
    runs-on: ubuntu-latest
    steps:
    - name: Get Random UUID
      id: uuid
      run: echo "::set-output name=uuid::$(uuidgen)"
    - name: Print UUID
      # uuid를 만들어서 uuid.txt 파일로 생성
      # upload-artifact 기능을 사용해서 아티팩트에 해당 파일 업로드
      run: |
        echo "UUID: ${{ steps.uuid.outputs.uuid }}"
        echo "${{ steps.uuid.outputs.uuid }}" > uuid.txt
    - name: Upload UUID
      uses: actions/upload-artifact@v3
      with:
        name: uuid
        path: uuid.txt
  job-2:
    runs-on: ubuntu-latest
    needs: job-1
    steps:
    - name: Download UUID
      # job-1에서 생성한 uuid 파일을 가져와서 내용을 출력한다.
      uses: actions/download-artifact@v3
      with:
        name: uuid
    - name: Print UUID
      run: |
        echo "UUID: $(cat uuid.txt)"
````