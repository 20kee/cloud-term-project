import user_crawler
import json
import time
from time import sleep
import re
import user_crawler

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

def crawler():

    time.sleep(0.2)

	# (3) 각 요소들 전체 긁어오기
    review_list = user_crawler.driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem')
    names = user_crawler.driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem > .group_tit > .tit_evaluation > .link_txt')
    ratings = user_crawler.driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem > .rating > .score > em')
    created_dates = user_crawler.driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem > .desc_directory')

    for index in range(len(review_list)):
        print(index)

		# (4) 유저 이름
        restaurant_name = names[index].text
        names[index].send_keys(Keys.ENTER)
        print(restaurant_name)

		# (5) 장소 유형
        restaurant_type = types[index].text
        print(restaurant_type)

		# (6) 주소
        address = address_list.__getitem__(index).find_elements(By.CSS_SELECTOR, 'p')
        
        new_addr = address.__getitem__(0).text
        print(new_addr)
        
        old_addr = address.__getitem__(1).text[5:]
        print(old_addr)

        # (7) 별점
        average_score = rating[index].text
        print(average_score)

        # (8) 리뷰 개수
        review_count = counts[index].text
        review_count = review_count[3:]
        print(review_count)

        # dict에 데이터 집어넣기
        dict_temp = {
            'returantName': restaurant_name,
            'reviewScore': review_score,
            'userId': user_id,
            'description': description,
            'createdAt': created_date,
        }

        restaurant_dict['식당 정보'].append(dict_temp)
        print(f'{restaurant_name} ...완료')

def user_crawler(user_profile_links):
    
    # css를 찾을때 까지 10초 대기
    user_crawler.time_wait(10, '#other\.review > ul')

    # (2) 후기 탭 클릭
    place_tab = user_crawler.driver.find_element(By.CSS_SELECTOR, '#info\.other > div.header > div > div.FavoriteOtherMethodType > ul > li.ACTIVE > a')
    place_tab.send_keys(Keys.ENTER)

    sleep(1)

    # 리뷰 리스트
    review_list = user_crawler.driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem')

    # dictionary 생성
    user_reviews_dict = {'유저별 리뷰': []}

    # 시작시간
    start = time.time()
    print('[크롤링 시작...]')

    # 페이지 리스트만큼 크롤링하기
    page = 1    # 현재 크롤링하는 페이지가 전체에서 몇번째 페이지인지
    page2 = 0   # 1 ~ 5번째 중 몇번째인지
    error_cnt = 0

    while 1:
        # 페이지 넘어가며 출력
        try:
            page2 += 1
            print("**", page, "**")

            # (7) 페이지 번호 클릭
            user_crawler.driver.find_element(By.XPATH, f'//*[@id="info.search.page.no{page2}"]').send_keys(Keys.ENTER)
            
            # 주차장 리스트 크롤링
            crawler()

            # 해당 페이지 리뷰 리스트
            review_list = user_crawler.driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem')

            # 한 페이지에 장소 개수가 15개 미만이라면 해당 페이지는 마지막 페이지
            if len(review_list) < 15:
                break
            # 다음 버튼을 누를 수 없다면 마지막 페이지
            if not user_crawler.driver.find_element(By.XPATH, '//*[@id="info.search.page.next"]').is_enabled():
                break

            # (8) 다섯번째 페이지까지 왔다면 다음 버튼을 누르고 page2 = 0으로 초기화
            if page2 % 5 == 0:
                user_crawler.driver.find_element(By.XPATH, '//*[@id="info.search.page.next"]').send_keys(Keys.ENTER)
                page2 = 0

            page += 1

        except Exception as e:
            error_cnt += 1
            print(e)
            print('ERROR!' * 3)

            if error_cnt > 5:
                break

    print('[데이터 수집 완료]\n소요 시간 :', time.time() - start)
    user_crawler.driver.quit()  # 작업이 끝나면 창을 닫는다.

    # json 파일로 저장
    with open('data/user_reviews_dict.json', 'w', encoding='utf-8') as f:
        json.dump(user_reviews_dict, f, indent=4, ensure_ascii=False)