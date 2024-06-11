1. springBoot만들기. (https://start.spring.io/)

2. 인텔리J IDE 사용할것으로 컨트롤러, thmyleaf test 용 만들고 > 서버연동하여 제대로 작동하는지 확인하기.

3. oracle를 사용하여 프로젝트에 연동하기.
   3.1. 가장 DBMS중 현재 회사에서 많이 사용하기도 하며, 대용량 DB처리시 용이하여

   3.2. Oracle 설치하기, SQLDevleloper 설치하기 > 계정 접속하기

   3.3. gradle설정에서 JPA 설정을 추가하여 라이브러리를 만들어주기

   3.4. 크게 문제 없이 잘 성공하였지만, 방언 설정시 문제점 발생하였음
      3.4.1. 인터넷에 나온대로 방언을 설정하였지만 자꾸 찾지를 못한다. 그리하여 계속 서칭해보니 하이버네이트 최신 기술에서는 spring.jpa.database-platform=org.hibernate.dialect.OracleDialect으로 방언설정으로 해야 오류 뜨지 않는다는 것을 발견하였다.

4. 부트스트랩 사용하기
   -. 부트스트랩이동하여 원하는 템플릿 설치한 후 압축파일 풀기.
   -. 압축을 푼 폴더에서 폴더파일을 CSS,JS,SCSS등 경로에 넣고 각 html위치에 맞게 이동하기
   -. 각 html에 있는 경로를 맞줘서 작동한다.
   
6. CI-CD 연결 & docker 이미지 자동화배포 (gitauction을 이용하여 배포시)
  -. Dockerfile을 추가하여 Docker이미지 배포시 설정
  -. .github/workflows/gradle.yml 을 추가하여 git에 commit시 DockerHub에 배포 되도록 구현

9. aws클라우드에 임시로만 배포하기.

10. 로그인 기능 구현하기.

11. 
