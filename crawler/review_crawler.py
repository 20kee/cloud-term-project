import json
import time
import re
from time import sleep
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

def wait_for_element(driver, num, by, code):
    try:
        wait = WebDriverWait(driver, num).until(EC.presence_of_element_located((by, code)))
        return wait
    except Exception as e:
        print(f"{code} 태그를 찾지 못하였습니다. 예외: {str(e)}")
        return None

def user_review_crawler(driver):
    time.sleep(1)

    review_list = driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem')
    names = driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem > .group_tit > .tit_evaluation > .link_txt')
    created_dates = driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem > .rating > .num_date')
    descriptions = driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem > .desc_directory')

    tmp_review_list = []

    for index in range(len(review_list)):
        description = descriptions[index].text.strip()
        if not description:  # 리뷰 설명이 비어 있으면 해당 리뷰는 건너뛰기
            continue

        restaurant_name = names[index].text
        style_attr = driver.find_element(By.CSS_SELECTOR, f'#other\\.review > ul > li:nth-child({index+1}) > div.rating > span.score > span.backgroundStar > span').get_attribute('style')
        width_match = re.search(r'width: (\d+(\.\d+)?)px', style_attr)
        width_percentage = float(width_match.group(1)) if width_match else 0
        review_score = round((width_percentage / 68) * 5)  # width 값에 따라 점수 계산

        created_date = created_dates[index].text

        dict_temp = {
            'restaurantName': restaurant_name,
            'reviewScore': review_score,
            'description': description,
            'createdAt': created_date,
        }

        tmp_review_list.append(dict_temp)

    return tmp_review_list

def review_crawler(driver, user_reviews_dict, user_name):
    wait_for_element(driver, 10, By.CSS_SELECTOR, '#info\\.other > div.header > div > div.FavoriteOtherMethodType')

    review_tab = driver.find_element(By.XPATH, '//*[@id="info.other"]/div[1]/div/div[3]/ul/li[2]/a')
    review_tab.send_keys(Keys.ENTER)

    sleep(1)

    start = time.time()
    print('[크롤링 시작...]')

    page = 1
    page2 = 0
    error_cnt = 0

    all_reviews = []

    while True:
        try:
            page2 += 1
            print("**", page, "**")

            try:
                driver.find_element(By.XPATH, f'//*[@id="other.review.page.no{page2}"]').send_keys(Keys.ENTER)
            except:
                print('단일 페이지')
                break

            tmp_review_list = user_review_crawler(driver)
            all_reviews.extend(tmp_review_list)

            review_list = driver.find_elements(By.CSS_SELECTOR, '.list_body > .FavoriteEvaluationItem')

            if len(review_list) < 15:
                print("마지막 페이지에 도달했습니다 (리뷰가 15개 미만)")
                break
            if not driver.find_element(By.XPATH, '//*[@id="other.review.page.next"]').is_enabled():
                print("다음 버튼이 비활성화됨")
                break

            if page2 % 5 == 0:
                try:
                    driver.find_element(By.XPATH, '//*[@id="other.review.page.next"]').send_keys(Keys.ENTER)
                    page2 = 0
                except:
                    print('다음 페이지로 이동 불가')
                    break

            page += 1

        except Exception as e:
            error_cnt += 1
            print(e)
            print('ERROR!' * 3)
            if error_cnt > 10:
                break

    # 사용자 이름이 이미 있는지 확인
    existing_user = next((user for user in user_reviews_dict['유저별 리뷰 정보'] if user_name in user), None)
    if existing_user:
        print(f"{user_name}의 리뷰는 이미 존재합니다. 건너뜁니다.")
        return

    user_reviews_dict['유저별 리뷰 정보'].append({user_name: all_reviews})

    print(f'[데이터 수집 완료: {user_name}]\n소요 시간 :', time.time() - start)

def save_reviews(user_reviews_dict):
    try:
        with open('data/user_reviews_dict.json', 'w', encoding='utf-8') as f:
            json.dump(user_reviews_dict, f, indent=4, ensure_ascii=False)
        print('데이터가 성공적으로 저장되었습니다.')
    except Exception as e:
        print('Error occurred while saving the data:', str(e))

def main():
    with open('C:.\\data\\user_profile_links_dict.json', 'r') as f:
        json_data = json.load(f)

    links = json_data['links']
    user_reviews_dict = {'유저별 리뷰 정보': []}

    for idx, link in enumerate(links, start=1):
        driver = webdriver.Chrome()
        driver.get(link)

        wait_for_element(driver, 10, By.CSS_SELECTOR, '#info\\.other > div.header > div > div.FavoriteOtherProfile > div.wrap_user > strong')
        try:
            user_name_element = driver.find_element(By.CSS_SELECTOR, '#info\\.other > div.header > div > div.FavoriteOtherProfile > div.wrap_user > strong')
            user_name = user_name_element.text if user_name_element else "Unknown"
        except:
            print("유저 이름을 찾지 못했습니다.")
            driver.quit()
            continue

        review_crawler(driver, user_reviews_dict, user_name)
        driver.quit()

        if idx % 5 == 0:
            save_reviews(user_reviews_dict)

    save_reviews(user_reviews_dict)

if __name__ == "__main__":
    main()
