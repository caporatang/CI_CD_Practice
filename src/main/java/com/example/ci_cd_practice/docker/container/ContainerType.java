package com.example.ci_cd_practice.docker.container;

/**
 * packageName : com.example.ci_cd_practice.docker.container
 * fileName : ContainerType
 * author : taeil
 * date : 12/31/23
 * description :
 * =======================================================
 * DATE          AUTHOR                      NOTE
 * -------------------------------------------------------
 * 12/31/23        taeil                   최초생성
 */
public class ContainerType {
    // 컨테이너는 어떤 타입으로 생성되는가?

    // 컨테이너 패키징 메커니즘 : 시스템 / 애플리케이션 / 라우터 컨테이너

    // 시스템(or OS) 컨테이너
    // 1. 호스트OS 위에 Ubuntu와 같은 배포판 리눅스 Image를 통해 배포되는 컨테이너다.
    // 2. 또다른 VM의 형태이고, 내부에 다양한 애플리케이션 및 라이브러리 도구를 설치, 실행 가능하다.
    // 3. 대표적으로 LXC, LXD, OpenVZ, Linux VServer, BSD Jails 등이 있다.

    // 애플리케이션 컨테이너 -> 보통 docker를 사용하는 이유
    // 1. 단일 애플리케이션 실행을 위해 해당 서비스를 패키징하고 실행하도록 설계된 컨테이너
    // 2. 3-tier 애플리케이션과 같은 경우 각 tier (frontend-backend-DB)를 별개 컨테이너로 실행하여 연결한다.
    // 3. 대표적으로 WhatIsDocker container runtime, Rocket 등이 있다.

    // 주로 애플리케이션 컨테이너를 연습할 필요가 있음.
}