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