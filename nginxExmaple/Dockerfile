# 베이스 이미지
FROM nginx:1.25.0-alpine
# 하나씩 하나씩 해당 경로에 복사한다.
COPY index-image.html /usr/share/nginx/html/index.html
COPY docker_logo.png /usr/share/nginx/html/docker_logo.png
#80포트를 연다
EXPOSE 80
#nginx를 백그라운드로 실행한다.
CMD ["nginx", "-g", "daemon off;"]

#이미지에서 컨테이너로 바뀌면 정적환경에서 동적환경으로 바뀐다.
#동적환경에서의 핵심은 컨테이너 자체에서 프로세스가 동작된다는것이 동적환경이다
#엔진엑스 어플리케이션 컨테이너는  이 컨테이너의 주인이기 때문에 반드시 엔진엑스 데몬이 켜져야된다
#CMD 명령어 안에는 거의 대부분 실행 명령어가 들어간다.

