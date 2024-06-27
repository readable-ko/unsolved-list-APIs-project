import unittest
from unittest.mock import patch, MagicMock
from server.data_transformer import DataTransformer
from server.db_controller import DBController, InsertType
from server.api_controller import ApiController


class TestDataTransformer(unittest.TestCase):
    @patch('mysql.connector.connect')
    def setUp(self, mock_connect):
        self.transformer = DataTransformer(config_path="config.json")
        self.transformer.init()

        self.mock_db_data = [
            ("fpqpsxh", 2, "fpqpsxh@naver.com"),
            ("readable-ko", 3, "readable-ko@example.com"),
            ("forever", 4, "forever@example.test")
        ]

        self.mock_api_data = [
            {"id": 1, "username": "glory", "email": "gloryko@test.com",
             "password": "password123", "food": "hamburger"},
            {"id": 3, "username": "readable-ko", "email": "test@example.com",
             "password": "helloWorld", "food": "ramens"},
            {"id": 4, "username": "forever", "email": "forever@example.test",
             "password": "motorbike", "food": "chicken"}
        ]

        self.mock_user_api = [
            {"isSolvable": True, "isPartial": False, "acceptedUserCount": 301299,
             "life": 1000, "titleKo": "A+B"},
            {"isSolvable": False, "isPartial": True, "acceptedUserCount": 23901,
             "life": 1950, "titleKo": "6월25일은"},
            {"isSolvable": True, "isPartial": True, "acceptedUserCount": 7331,
             "life": 1592, "titleKo": "임진왜란"}
        ]

    @patch.object(DBController, 'get_table')
    @patch.object(ApiController, 'get_org_api')
    def test_choose_user_and_filtering(self, mock_get_org_api, mock_get_table):
        # Mock data from the database and API
        mock_get_table.return_value = self.mock_db_data
        mock_get_org_api.return_value = self.mock_api_data

        # Mock the insert_many method
        self.transformer._db_con.insert_many = MagicMock()

        # Call the method under test
        update_user = self.transformer.choose_user()
        columns = ["username", "id", "email"]

        # Assert the insert_many was called with the correct parameters
        expected_data_to_insert = [('glory', 1, 'gloryko@test.com'), ('readable-ko', 3, 'test@example.com')]
        (self.transformer._db_con
            .insert_many
            .assert_called_once_with(
                table_name="site_users",
                datas=expected_data_to_insert,
                types=InsertType.DUPLICATE,
                column_datas=columns
            ))

        # Assert the method returns the correct data
        expected_data_to_insert = sorted(expected_data_to_insert)
        self.assertEqual(sorted(update_user), expected_data_to_insert)

    @patch.object(DBController, 'get_table')
    def test_get_db_user_solved(self, mock_get_table):
        # Mock data from the database
        mock_get_table.return_value = 5

        # Assert the method returns the correct data
        expected_data_to_return = 5
        self.assertEqual(expected_data_to_return,
                         self.transformer.get_db_user_solved(table_name="test",
                                                             query=self.transformer._config["transform"]["query"]))

        # Assert the method was called with the correct parameters
        self.transformer._db_con.get_table.assert_called_once()

    @patch.object(ApiController, 'get_user_api')
    def test_get_api_user_solved(self, mock_get_user_api):
        # Mock data for the api call
        mock_get_user_api.return_value = self.mock_user_api

        # Assert the method returns the correct data
        expected_data_to_insert = [("wannaSleep", 1000), ("wannaSleep", 1950), ("wannaSleep", 1592)]
        self.assertEqual(expected_data_to_insert,
                         self.transformer.get_api_user_solved("wannaSleep"))

        # Assert the method was called with the correct parameters
        self.transformer._ap_con.get_user_api.assert_called_with(username="wannaSleep")

    @patch.object(ApiController, 'get_called_times')
    def test_get_api_called_count(self, mock_get_called_times):
        mock_get_called_times.return_value = 3
        self.assertEqual(3, self.transformer.get_api_called_count())

        self.transformer._ap_con.get_called_times.assert_called_once()


if __name__ == '__main__':
    unittest.main()
