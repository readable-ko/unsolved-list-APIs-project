import math
import requests

from server.utility import Utility


def _send_request(url: str, page: int, query: str = ""):
    """
    Send request to url
    :param url: api url address
    :param page: Integer value for page number
    :param query: String value for username (initialized empty string)
    :return: request result json for Success (None for fail)
    """
    api_url = url + query + '&page=' + str(page)
    result = requests.get(api_url)

    if result.status_code == 200:
        return result.json()
    else:
        print(f"Failed to get {api_url} " + str(result.status_code))
        raise requests.exceptions.ConnectionError()


class ApiController:
    def __init__(self, config_path='config.json'):
        self._org_url = None
        self._user_url = None
        self._config = Utility.load_config(config_path)
        self._called = 0

    def init(self):
        self._org_url = self._config['api']['ORG_API_URL']
        self._user_url = self._config['api']['USER_API_URL']

    def _get_paginated_data(self, url, query=""):
        target = self._config['api']["TARGET"]
        count = self._config['api']["COUNT"]

        first_page = _send_request(url, 1, query)

        if Utility.is_none_or_empty(first_page):
            return []

        data = first_page[target]
        total_pages = math.floor(first_page[count] // 50)
        self._called += total_pages

        for page in range(2, total_pages + 1):
            page_result = _send_request(url, page, query)
            data.extend(page_result[target])

        return data

    def get_called_times(self):
        return self._called

    def get_org_api(self):
        return self._get_paginated_data(self._org_url)

    def get_user_api(self, username):
        return self._get_paginated_data(self._user_url, query=username)
