import unittest
from unittest.mock import patch, MagicMock

import requests

from server.api_controller import ApiController


class TestApiController(unittest.TestCase):
    @patch('server.utility.Utility.load_config')
    def setUp(self, mock_load_config):
        self.mock_config = {
            "api": {
                "ORG_API_URL": "https://example.com/org",
                "USER_API_URL": "https://example.com/user",
                "TARGET": "lists",
                "COUNT": "total_count"
            }
        }

        mock_load_config.return_value = self.mock_config

        # Mocked response from requests.get
        self.api = ApiController()
        self.api.init()

    @patch('requests.get')
    def test_get_org_api(self, mock_requests_get):
        self.mock_response = MagicMock()
        self.mock_response.status_code = 200
        self.mock_response.json.return_value = {
            'lists': [{"id": 1, 'name': "eric"}],
            'total_count': 50
        }
        mock_requests_get.return_value = self.mock_response
        result = self.api.get_org_api()
        self.assertEqual(result, [{'id': 1, 'name': "eric"}])

    @patch('requests.get')
    def test_get_user_api_fail_with_requests(self, mock_requests_get):
        self.mock_response = MagicMock()
        self.mock_response.status_code = 500
        self.mock_response.json.return_value = {
            'lists': [{"id": 1, 'name': "eric"}],
            'total_count': 50
        }
        mock_requests_get.return_value = self.mock_response
        with self.assertRaises(requests.exceptions.ConnectionError) as e:
            result = self.api.get_user_api("test")

    @patch('server.api_controller._send_request')
    def test_get_user_api_with_send_request(self, mock_send_request):

        self.mock_response = {
            'lists': [{"id": 1, 'name': "eric"}],
            'total_count': 50
        }
        mock_send_request.return_value = self.mock_response
        result = self.api.get_user_api("test")
        self.assertEqual(result, [{'id': 1, 'name': "eric"}])
        mock_send_request.assert_called_once_with('https://example.com/user', 1, 'test')

    def test_get_called_count(self):
        result = self.api.get_called_times()
        self.assertEqual(result, 0)

        self.test_get_user_api_with_send_request()
        result = self.api.get_called_times()
        self.assertEqual(result, 1)


if __name__ == '__main__':
    unittest.main()
