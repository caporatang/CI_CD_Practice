# Job
workflow 내에서 실행되는 단위 작업에 대한 정의로 각각의 Job은 개별의 실행기(Runner)에서 수행되며, default는 동시에 병렬로 수행되지만, 순차적으로 수행도 가능하다.

## 정의되는 설정
1. id : 작업에 대한 고유 식별자
2. name : 작업 이름. UI상 작업 이름을 표시하게된다.
3. runs-on : 실행기(runner)에 대한 정의. 작업을 수행할 머신의 형식을 결정한다.
4. step : Job 내에서 순차적으로 수행할 실제 명령
````yaml
name: sample
on: workflow_dispatch
jobs:
  build: 
    name: Build Test
    runs-on: ubuntu-lastest
    steps:
      - name: Checkout
        uses: actions/checkout@v2
      - name: Build project
        run: |
          echo: "Build Project"
      - name: Run tests
        run: |
          echo "Run Test"
````

### default
Job 내 모든 Step에 적용될 기본 설정 적용
````yaml
name: Job with Default 
on: workflow_dispatch
jobs:
  build:
    name: Build and Test
    runs-on: ubuntu-latest
    defaults:
      run:
        shell: bash
        working-directory: ./build_sample/python
    steps:
    - id: checkout
      name: Checkout 
      uses: actions/checkout@v2
    - name: Build project
      run: |
        pwd
        echo "Build Project"
    - name: Run tests
      run: |
        pwd
        echo "Run Test"
````

## Step 설정 내용
1. id : 각 step의 고유 식별자
2. name : 각 step의 이름으로, UI에서 표시할 Step 이름
3. run : 쉘을 사용하여 명령어 실행. 단일/다중 명령 실행 가능
4. if : 해당 step에 대한 실행 여부 조건
5. uses : 재사용가능한 명령 패키지 실행 
6. with : uses 패키지 종류에 따라 필요한 추가 파라미터 설정
````yaml
    steps:
      - id: checkout
        name: Checkout
        uses: actions/checkout@v2
      - id: build-test
        name: Run npm build
        run: |
          npm ci
          npm run build
      - name: Login to Docker Hub
        if: always()
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKERHUB_USER }}
          password: ${{ secrets.DOCKERHUB_PASSWORD }}
````

