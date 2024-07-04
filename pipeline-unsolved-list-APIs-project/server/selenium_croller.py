import os
import sys
from selenium import webdriver
from selenium.common import NoSuchElementException
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time

login_url = 'https://www.acmicpc.net/login?next=%2F'
control_url = 'https://www.acmicpc.net/setting/view'
target_url = 'https://www.acmicpc.net/problem/{}'

problem_query_form = "INSERT INTO problem VALUES ('{}', '{}', '{}');"
problem_tags_query_form = "INSERT INTO problem_tags VALUES ('{}', '{}');"

# Set the initial value for croller.
user_name = sys.argv[1]#input('Id: ').strip()
password = sys.argv[2]#input('Password: ').strip()
start_point = 30000
end_point = 32010

# 사용자 에이전트 설정
user_agent = ("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 "
              "Safari/537.3")

# Selenium 드라이버 설정 (Chrome 예시)
options = webdriver.ChromeOptions()
options.add_argument(f"user-agent={user_agent}")

# Selenium 드라이버 설정 (Chrome 예시)
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

# 로그인 하기
driver.get(login_url)
time.sleep(1)  # 페이지 로딩 대기

# 로그인 폼 채우기
username_input = driver.find_element(By.NAME, 'login_user_id')
password_input = driver.find_element(By.NAME, 'login_password')
time.sleep(1)
username_input.send_keys(user_name)
password_input.send_keys(password)
password_input.send_keys(Keys.RETURN)
time.sleep(5)


# 문제 페이지 접근 및 데이터 추출
saving_file = open("../problem_list.txt", "at")
err_log = open("../error_log.txt", "wt")

for index in range(start_point, end_point):
    url = target_url.format(index)
    driver.get(url)
    time.sleep(1.5)  # 페이지 로딩 대기

    # 문제 정보 추출
    try:
        problem_name = driver.find_element(By.CSS_SELECTOR, '#problem_title')

        problem_level = driver.find_element(By.CSS_SELECTOR,
                                            'body > div.wrapper > div.container.content > div.row > div:nth-child(3) '
                                            '> div > blockquote > img')

        image_url = problem_level.get_attribute('src')
        image_name = os.path.basename(image_url)

        problem_ul = driver.find_elements(By.CSS_SELECTOR, '#problem_tags > ul')

        problem_query = problem_query_form.format(index, image_name.split('.')[0], problem_name.text)
        problem_tags_query = ""
        problem_tags = []

        if len(problem_ul) > 0:
            problem_tags = [problem_tag.text for problem_tag in problem_ul][0].split("\n")
            problem_tags_query = problem_tags_query_form.format(index, problem_tags)

        print(problem_query, "\n", problem_tags_query)

        saving_file.write(problem_query + "\n")
        for problem_tag in problem_tags:
            saving_file.write(problem_tags_query_form.format(index, problem_tag) + "\n")
        saving_file.write("\n")
        time.sleep(0.5)

    except NoSuchElementException as e:
        err_log.write(f"페이지 {index}는 존재하지 않는다는 사실!!\n")
        print(f"페이지 {index}는 존재하지 않는다는 사실!!{e}\n")
        continue

    except Exception as e:
        saving_file.close()
        err_log.write(f"페이지 {index}에서 문제를 추출하는 동안 오류 발생: {e}\n")
        print(f"페이지 {index}에서 문제를 추출하는 동안 오류 발생: {e}")
        err_log.close()
        driver.quit()
        exit(0)

err_log.close()
saving_file.close()
driver.quit()
