import unittest
from unittest.mock import patch, MagicMock
from server.data_transformer import DataTransformer
from server.db_controller import DBController
from server.api_controller import ApiController


class TestDataTransformer(unittest.TestCase):
    @patch('mysql.connector.connect')
    def setUp(self, mock_connect):
        self.transformer = DataTransformer(config_path="config.json")
        self.transformer.init()

    @patch.object(DBController, 'get_table')
    @patch.object(ApiController, 'get_org_api')
    def test_choose_user_and_filtering(self, mock_get_org_api, mock_get_table):
        # Mock data from the database and API
        mock_db_data = [
            ("fpqpsxh", 2, "fpqpsxh@naver.com"),
            ("readable-ko", 3, "readable-ko@example.com"),
            ("forever", 4, "forever@example.test")
        ]

        mock_api_data = [
            {"id": 1, "username": "glory", "email": "gloryko@test.com",
             "password": "password123", "food": "hamburger"},
            {"id": 3, "username": "readable-ko", "email": "test@example.com",
             "password": "helloWorld", "food": "ramens"},
            {"id": 4, "username": "forever", "email": "forever@example.test",
             "password": "motorbike", "food": "chicken"}
        ]

        mock_get_table.return_value = mock_db_data
        mock_get_org_api.return_value = mock_api_data

        # Mock the insert_many method
        self.transformer._db_con.insert_many = MagicMock()

        # Call the method under test
        update_user = self.transformer.choose_user()

        # Assert the insert_many was called with the correct parameters
        expected_data_to_insert = [('glory', 1, 'gloryko@test.com'), ('readable-ko', 3, 'test@example.com')]
        (self.transformer._db_con.insert_many
         .assert_called_once_with(table_name="site_users", datas=expected_data_to_insert))

        # Assert the method returns the correct data
        self.assertEqual(sorted(update_user), expected_data_to_insert)


if __name__ == '__main__':
    unittest.main()
