import json
import time
from time import sleep
import re

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.common.exceptions import NoSuchElementException
# dictionary 생성
restaurant_dict = {'식당 정보': []}
user_dict = {'유저 정보': []}
restaurant_review_dict = {'식당 리뷰 정보': []}
user_reviews_dict = {'유저별 리뷰 정보': []}
user_profile_links_dict = {'links': []}

# --크롬창을 숨기고 실행-- driver에 options를 추가해주면된다
# options = webdriver.ChromeOptions()
# options.add_argument('headless')

url = 'https://map.kakao.com/'
driver = webdriver.Chrome() # 크롬창 숨기기
driver.get(url)
key_word = '부산 금정구 부산대학교 주변 식당'  # 검색어

# css 찾을때 까지 10초대기
def css_time_wait(num, code):
    try:
        wait = WebDriverWait(driver, num).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, code)))
    except:
        print(code, '태그를 찾지 못하였습니다.')
        driver.quit()
    return wait

# xpath 찾을때 까지 10초대기
def xpath_time_wait(num, code):
    try:
        wait = WebDriverWait(driver, num).until(
            EC.presence_of_element_located((By.XPATH, code)))
    except:
        print(code, '태그를 찾지 못하였습니다.')
        driver.quit()
    return wait

# 식당 정보 출력
def crawler():
    try:
        time.sleep(0.2)

        # (3) 각 요소들 전체 긁어오기
        restaurant_list = driver.find_elements(By.CSS_SELECTOR, '.placelist > .PlaceItem')
        names = driver.find_elements(By.CSS_SELECTOR, '.head_item > .tit_name > .link_name')
        types = driver.find_elements(By.CSS_SELECTOR, '.head_item > .subcategory')
        address_list = driver.find_elements(By.CSS_SELECTOR, '.info_item > .addr')
        rating = driver.find_elements(By.CSS_SELECTOR, '.rating > .score > .num')
        counts = driver.find_elements(By.CSS_SELECTOR, '.rating > .review')
        details = driver.find_elements(By.CSS_SELECTOR, '.info_item > .contact > .moreview')

        for index in range(len(restaurant_list)):
            #print(index)

            # (4) 장소명
            restaurant_name = names[index].text
            #names[index].send_keys(Keys.ENTER)
            #print(restaurant_name)

            # (5) 장소 유형
            restaurant_type = types[index].text
            #print(restaurant_type)

            # (6) 주소
            address = address_list.__getitem__(index).find_elements(By.CSS_SELECTOR, 'p')
            
            new_addr = address.__getitem__(0).text
            #print(new_addr)
            
            old_addr = address.__getitem__(1).text[5:]
            #print(old_addr)

            # (7) 별점
            average_score = rating[index].text
            #print(average_score)

            # (8) 리뷰 개수
            review_count = counts[index].text
            review_count = review_count[3:]
            #print(review_count)

            # dict에 데이터 집어넣기
            dict_temp = {
                'name': restaurant_name,
                'restaurant_type': restaurant_type,
                'averageScore': average_score,
                'normScore' : "norm_score", # 추가 예정
                'reviewCount': review_count,
                'address1': new_addr,
                'address2': old_addr
            }

            restaurant_dict['식당 정보'].append(dict_temp)
            print(f'{restaurant_name} 정보...완료')
    ############################################################################################################################################################
    # crawl details
    ############################################################################################################################################################
            try:
                details[index].send_keys(Keys.ENTER)
            except NoSuchElementException:
                print("상세정보가 존재하지 않습니다.")
                continue

            #print("후기 크롤링 시작")
            # 상세정보 탭으로 변환
            driver.switch_to.window(driver.window_handles[1])
            #info\.search\.place\.list > li:nth-child(3) > div.info_item > div.contact.clickArea > a.moreview
            # css를 찾을때 까지 10초 대기

            ''' try:
                
                driver.find_element(By.CSS_SELECTOR, '#mArticle > div.cont_essential > div:nth-child(1) > div.place_details')
            except:
                print('#mArticle > div.cont_essential > div:nth-child(1) > div.place_details', '태그를 찾지 못하였습니다.')
                driver.close()
                driver.switch_to.window(driver.window_handles[0])  # 기존 탭으로 전환
                continue'''
        
            try:
                time.sleep(1)
                driver.find_element(By.CSS_SELECTOR, '#mArticle > div.cont_essential > div:nth-child(1) > div.place_details > div > div.location_evaluation > a:nth-child(3)').send_keys(Keys.ENTER)
            except:
                # '후기 미제공'일 경우
                print("후기가 존재하지 않습니다.")
                driver.switch_to.window(driver.window_handles[1])
                driver.close()
                driver.switch_to.window(driver.window_handles[0])  # 기존 탭으로 전환
                css_time_wait(10, 'div.box_searchbar > input.query')
                continue

            try:
                #리뷰 더보기 버튼 클릭
                more = (driver.find_element(By.CSS_SELECTOR, '#mArticle > div.cont_evaluation > div.evaluation_review > .link_more')).text
                #mArticle > div.cont_essential > div:nth-child(1) > div.place_details > div > div.location_evaluation > a:nth-child(3)
                while(more != "후기 접기"):
                    driver.find_element(By.CSS_SELECTOR,'#mArticle > div.cont_evaluation > div.evaluation_review > .link_more').send_keys(Keys.ENTER)
                    more = (driver.find_element(By.CSS_SELECTOR, '#mArticle > div.cont_evaluation > div.evaluation_review > .link_more')).text
                    time.sleep(0.3)
            except:
                pass

            #print("후기 더보기 완료")
            

            # 리뷰 정보
            review_list = driver.find_elements(By.CSS_SELECTOR, '.evaluation_review > .list_evaluation > li')
            
            # 유저 평균평점, 후기 개수
            user_info = driver.find_elements(By.CSS_SELECTOR, '.unit_info > .txt_desc')

            # 후기 생성 날짜
            review_created = driver.find_elements(By.CSS_SELECTOR, '.unit_info > .time_write')
            
            # 유저 이름
            user_names = driver.find_elements(By.CSS_SELECTOR, '.unit_info > .link_user > .inner_user > .txt_username')

            # 유저 레벨
            user_levels = driver.find_elements(By.CSS_SELECTOR, '.unit_info > .link_user > .inner_user > .badge_info > .txt_badge')

            # 유저 프로필 링크
            user_profile_links = driver.find_elements(By.CSS_SELECTOR, '.evaluation_review > .list_evaluation > li > a')
            
            tmp_resaurant_dict = {f'{restaurant_name}' : []}
            for i in range(len(review_list)):
                user_review_cnt_index = i * 2
                user_review_avg_index = i * 2 + 1
                #print(f"review_list{i} 출력")
                
                #유저 프로필 링크
                #user_profile_links_dict['links'].append(user_profile_links[i].get_attribute('href'))

                #유저 아이디
                user_id = review_list[i].get_attribute('data-userid')
                #print(user_id)

                #유저 이름
                user_name = user_names[i].text
                #print(user_name)

                #유저 평균 평점
                user_review_avg = user_info[user_review_avg_index].text
                #print(user_review_avg)

                #유저 레벨
                user_level = user_levels[i].text
                #print(user_level)

                #유저 리뷰개수
                user_review_cnt = user_info[user_review_cnt_index].text
                #print(user_review_cnt)
                dict_temp = {
                    'userId': user_id,
                    'userName': user_name,
                    'averageScore': user_review_avg,
                    'level': user_level,
                    'reviewCount': user_review_cnt
                }

                user_dict['유저 정보'].append(dict_temp)
                #print(f'유저 {user_name} 정보 ...완료')


                #리뷰 생성 날짜
                review_created_date = review_created[i].text
               # print(review_created_date)
                
                # 유저 평점 계산
                style_attr = driver.find_element(By.CSS_SELECTOR, f'#mArticle > div.cont_evaluation > div.evaluation_review > ul > li:nth-child({i+1}) > div.star_info > div > span > span').get_attribute('style')
                width_match = re.search(r'width: (\d+)%', style_attr)
                width_percentage = int(width_match.group(1))
                score = round((width_percentage / 100) * 5) # width 값에 따라 점수 계산 (예: 100% -> 5점)
                #print(score)

                # 리뷰 내용
                try:
                    user_description = driver.find_element(By.CSS_SELECTOR, f'#mArticle > div.cont_evaluation > div.evaluation_review > ul > li:nth-child({i+1}) > div.comment_info > p > span').text
                    #print(user_description)
                except:
                    user_description = ''
                    print("리뷰 내용 오류")

                dict_temp = {
                    'reviewScore': score,
                    'description': user_description,
                    'averageScore': average_score,
                    'normScore' : "normScore", # 추후 추가
                    'userId': user_id,
                    'restaurnatId': "restaurant_id", # 추후 추가
                    #'averageScore': average_score,
                    'level': user_level,
                    'createAt': review_created_date
                }
                
                tmp_resaurant_dict[f'{restaurant_name}'].append(dict_temp)
                #print(f'{user_name} ...완료')

                ############################################################################################################################################################
                # 유저별 리뷰모으기
                ############################################################################################################################################################

                user_profile_links[i].send_keys(Keys.ENTER)
                driver.switch_to.window(driver.window_handles[2])
                time.sleep(0.3)
                #print("유저별 리뷰 크롤링 시작")
                link = driver.current_url
                user_profile_links_dict['links'].append(link)
                #print(link)
                #user_reviews_dict['유저별 리뷰 정보'].append(review_crawler.review_crawler())
                driver.close() # 현재 탭 닫기
                driver.switch_to.window(driver.window_handles[1])  # 기존 탭으로 전환
            
            print("식당에 존재하는 유저별 리뷰 크롤링 완료")    
            restaurant_review_dict['식당 리뷰 정보'].append(tmp_resaurant_dict)
            driver.close() # 현재 탭 닫기
            driver.switch_to.window(driver.window_handles[0])  # 기존 탭으로 전환

    except Exception as e:
        print(e)
        print('ERROR! on crawler()')
        driver.switch_to.window(driver.window_handles[1])
        driver.close() # 현재 탭 닫기
        driver.switch_to.window(driver.window_handles[0])  # 기존 탭으로 전환
        return       
############################################################################################################################################################
############################################################################################################################################################

def main():
    # css를 찾을때 까지 10초 대기
    css_time_wait(10, 'div.box_searchbar > input.query')

    # (1) 검색창 찾기
    search = driver.find_element(By.CSS_SELECTOR, 'div.box_searchbar > input.query')
    search.send_keys(key_word)  # 검색어 입력
    search.send_keys(Keys.ENTER)  # 엔터버튼 누르기

    sleep(1)

    # (2) 장소 탭 클릭
    place_tab = driver.find_element(By.CSS_SELECTOR, '#info\.main\.options > li.option1 > a')
    place_tab.send_keys(Keys.ENTER)

    sleep(1)

    # 식당 리스트
    restaurant_list = driver.find_elements(By.CSS_SELECTOR, '.placelist > .PlaceItem')


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
            driver.find_element(By.XPATH, f'//*[@id="info.search.page.no{page2}"]').send_keys(Keys.ENTER)

            css_time_wait(10, '.placelist > .PlaceItem')

            # 주차장 리스트 크롤링
            crawler()

           
            # 다음페이지 버튼이 없다면 해당 페이지는 마지막 페이지
            if (not driver.find_element(By.XPATH, '//*[@id="info.search.page.next"]').is_enabled()):
                break      
            
             # (8) 다섯번째 페이지까지 왔다면 다음 버튼을 누르고 page2 = 0으로 초기화
            if page2 % 5 == 0:
                driver.find_element(By.XPATH, '//*[@id="info.search.page.next"]').send_keys(Keys.ENTER)
                page2 = 0

            page += 1

        except Exception as e:
            error_cnt += 1
            print(e)
            print('ERROR! on main()')

        if error_cnt > 15:
            break

    print('[데이터 수집 완료]\n소요 시간 :', time.time() - start)
    driver.quit()  # 작업이 끝나면 창을 닫는다.

    # json 파일로 저장
    with open('data/restaurant.json', 'w', encoding='utf-8') as f:
        json.dump(restaurant_dict, f, indent=4, ensure_ascii=False)

    with open('data/user_dict.json', 'w', encoding='utf-8') as f:
        json.dump(user_dict, f, indent=4, ensure_ascii=False)

    with open('data/restaurant_review_dict.json', 'w', encoding='utf-8') as f:
        json.dump(restaurant_review_dict, f, indent=4, ensure_ascii=False)

    with open('data/user_profile_links_dict.json', 'w', encoding='utf-8') as f:
        json.dump(user_profile_links_dict, f, indent=4, ensure_ascii=False)

main()