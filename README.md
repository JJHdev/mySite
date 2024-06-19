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
  -. .github/workflows/gradle.yml 을 추가하여 git에 commit시 DockerHub에 및 gitauction에 CI-CD 구현
  -. githubAuction은 workflow를 자동화 할수 있도록 도와주는 도구이며, 여러 Job으로 구성되고, Event에 의해 트리거될 수 있는 자동화된 프로세스
  -. Workflow 파일은 YAML으로 작성되고, Github Repository의 .github/workflows 폴더 아래에 저장됨

8. 회원 기능 구현하기.
  -. 회원가입 기능 구현 (jpa 이용)
    -. Exception을 직접 만들어 에러페이지로 이동하는게 아닌 각 유효성검사마다 원하는 설계대로 작성
    -. UserForm을 만들어 view에서 컨트롤러로 파라미터 받는 역할 만들고, User 도메인을 만들어 Service에서 UserForm에서 데이터를 받아 DB에 입력하도록 변경
    -. Email인증을 통해 보안성 강화
    -. 비밀번호를 SHA-256 적용예정




tip 개념잡기
** URL 요청 (절대경로, 상대경로, 정적자원,동적자원)
   * 절대 경로 (HTML에서 url 이동시)
      -. @{/css/style.css} 일 경우 static, public, resources 등 정적자원에서 먼저 style.css를 찾는다. 없을경우 컨트롤러로 전달되지 않고 404에러를 발생시킨다.
      -. @{/api/users} 정적자원 폴더에서 api/users 파일을 찾는다. 없을 경우 컨트롤러로 가 찾는다.
   * 상대 경로 (HTML에서 url 이동시)
      -. 현재 파일의 위치를 기준으로 경로를 해석하며, 이는 thymeleaf를 이용한다고 하더라도 상대경로를 > 브라우저 > 웹서버로 경로를 찾으며 이는 static(정적자원) 경로에서 찾는다.
      -. 해당 정적자원 경로에 파일이 없으며, 뒤에 .css, .html등 확장자가 없을 경우 컨트롤러로 이동하여 해당 경로가 있는지 찾는다. 
   * 뷰리졸버 (웹서버 > 뷰리졸버 이동시)
      -. temlplates(동적자원에서) 해당경로에 매핑하여 찾는다.
      -. 즉 HTML에서 컨트롤러로 이동 > 뷰리졸버로 이동 해야지만 templates 자원을 사용할수 있는것이다.

** thymeleaf 변동시 아무리 캐시새로고침해도 반영이 안되었는데, 1. thymeleaf 캐쉬 false 설정하기. 2 ide 설정하기 로 반영했다. 나중에 배포시에는 없애야할 코드임
     
     
