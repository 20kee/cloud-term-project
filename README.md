# 프로젝트 명

# 프로젝트 멤버 이름 및 멤버 별 담당한 파트 소개

## 김만재
- ERD 설계
- 크롤링한 json 데이터 처리 및 MySQL RDBMS에 저장
- SpringBoot를 활용하여 식당 리스트, 식당 별 리뷰 리스트, 사용자의 리뷰 리스트 출력 기능 구현
- mustache, css를 활용하여 Server Side Rendering 구현

## 이창민
- 식당 리뷰 감정 분석 모델 설계 및 학습
- 리뷰 데이터 정규화 점수 산출

## 하현진
- 크롤링
- Docker 관련
  - SpringBoot 프로젝트 Dockerfile 작성 및 설정 변경
  - docker mysql과 연동을 위한 docker-compose 작성
- azure vm을 이용해 배포
- 
# 프로젝트 소개
- 이 프로젝트는 진정한 맛집을 찾기 위한 프로젝트입니다.
- 진정한 식당의 가치를 점수로 표현하기 위해 리뷰 데이터를 분석하고 평가하여 신뢰성 있는 우리만의 점수를 부여합니다.
- 이 점수로 식당을 방문한 사용자들은 우리 서비스를 통해 행복한 식사를 경험할 수 있을 것입니다.

# 프로젝트 필요성
- 현재의 리뷰 평점 시스템은 보통 5점을 주는 사용자의 3점과, 보통 1점을 주는 사용자의 3점을 같은 가치로 평가합니다.
  하지만 실제로 전자의 3점은 후자의 3점보다 가치가 떨어진다고 판단해야 하므로 이는 문제가 있는 방식입니다.
  또한 같은 5점을 가진 리뷰이더라도 리뷰에 들어간 정성이나 표현에 따라 서로 다른 만족을 느꼈다고 판단하는 것이 타당합니다.
  현재 리뷰 시스템은 이러한 문제들을 고려하고 있지 않으므로, 리뷰만 보고 식당을 고른다면 기대와 다른 경험을 하게 됩니다.
  따라서 이러한 문제들을 고려한 신뢰성 있는 새로운 평가 지표가 필요합니다.

# 관련 기술/논문/특허 조사 내용 소개
- 영화 리뷰에 대한 데이터셋과 감정 분석 딥러닝 모델은 존재하지만, 맛집 리뷰에 대한 데이터와 자료는 많이 존재하지 않았습니다.
  
- 네이버 식당 리뷰, 카카오 식당 리뷰는 위와 같은 이슈를 고려하고 있지 않았습니다.
  카카오는 사용자의 평균 평점을 제공하긴 하지만 이를 이용해 새로운 점수를 보여주진 않습니다.

# 프로젝트 개발 결과물

# 개발 결과물을 사용하는 방법
- 부산대 근처 식당의 모든 리뷰에 대한 모델 적용이 완료되었으므로, 여러분은 그저 도출된 결과만을 구경하면 됩니다.
  저희가 새로 산출한 점수와 리뷰를 이용하여 식당을 방문해보세요.

# 개발 결과물의 활용 방안
- 이 프로젝트는, 부산대 근처 식당들의 리뷰만 분석하였습니다.
  하지만 얼마든지 다른 지역, 다른 식당의 리뷰들도 분석할 수 있습니다.
  특히 기본으로 제공되지 않는 특정 식당에 대한 점수만 궁금하다면, 그 식당에 대한 분석만을 제공하는 서비스도 생각해볼 수 있습니다.

- 식당 리뷰 말고도 리뷰 시스템이 존재하는 모든 곳에 적용할 수 있는 프로젝트입니다.
  펜션과 같은 숙소를 예약할 때, 냉동 음식을 구매할 때, 스킨로션과 같은 미용품을 구매할 때도
  이와 같은 리뷰 정규화 아이디어를 사용하면 더 행복한 선택을 할 수 있습니다.
