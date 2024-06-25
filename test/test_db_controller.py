import unittest
from unittest.mock import patch, MagicMock
from server.db_controller import DBController


class TestDBController(unittest.TestCase):

    @patch('mysql.connector.connect')
    def setUp(self, mock_connect):
        # Mock configuration
        self.mock_config = {
            'database': {
                'hostName': 'localhost',
                'port': 3306,
                'schemaName': 'test_db',
                'userName': 'test_user',
                'password': 'password'
            }
        }

        # Mock cursor and connection
        self.mock_cursor = MagicMock()
        self.mock_connection = MagicMock()
        self.mock_connection.cursor.return_value = self.mock_cursor
        mock_connect.return_value = self.mock_connection

        # Initialize DBController with mock config
        self.db = DBController()
        self.db._config = self.mock_config

        # Call init method to establish mock connection
        self.db.init()

    def test_insert_one(self):
        # Test insert_one method
        test_datas = {'id': 222, 'username': 'kadaif', 'ranking': 123}
        result = self.db.insert_one(table_name='test', data=test_datas)
        self.assertTrue(result)

        # Verify that cursor.execute and connection.commit were called
        self.assertTrue(self.mock_cursor.execute.called)
        self.assertTrue(self.mock_connection.commit.called)

    def test_insert_many(self):
        # Test insert_many method
        datas = [
            {'id': 4, 'username': 'four', 'ranking': '4'},
            {'id': 5, 'username': 'five', 'ranking': 5},
            {'id': '6', 'username': 'six', 'ranking': '6'},
            {'id': '7', 'username': 'seven', 'ranking': 7}
        ]
        result = self.db.insert_many(table_name='test', datas=datas)
        self.assertTrue(result)

        # Verify that cursor.executemany and connection.commit were called
        self.assertTrue(self.mock_cursor.executemany.called)
        self.assertTrue(self.mock_connection.commit.called)

        expected_sql = "INSERT INTO test (id, username, ranking) VALUES (%s, %s, %s);"
        expected_values = [(4, 'four', '4'), (5, 'five', 5), ('6', 'six', '6'), ('7', 'seven', 7)]
        self.mock_cursor.executemany.assert_called_once_with(expected_sql, expected_values)

    def test_get_table(self):
        self.db.get_table(table_name="test_table")

        expected_sql = "SELECT * FROM test_table"
        self.mock_cursor.execute.assert_called_once_with(expected_sql)

    def test_close(self):
        # Close connections after tests
        self.db.close()

        self.assertEqual(self.mock_cursor.close.call_count, 1)
        self.assertTrue(self.mock_cursor.closed())
        self.assertEqual(self.mock_connection.close.call_count, 1)
        self.assertTrue(self.mock_connection.closed())


if __name__ == '__main__':
    unittest.main()
