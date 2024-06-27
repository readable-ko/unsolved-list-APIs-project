import datetime
import unittest
from unittest.mock import patch, MagicMock

from server.db_controller import DBController, InsertType, AggregateType


class TestDBController(unittest.TestCase):

    @patch('mysql.connector.connect')
    @patch('datetime.datetime')
    def setUp(self, mock_datetime, mock_connect):
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

        self.datas = [
            {'id': 4, 'username': 'four', 'ranking': '4'},
            {'id': 5, 'username': 'five', 'ranking': 5},
            {'id': '6', 'username': 'six', 'ranking': '6'},
            {'id': '7', 'username': 'seven', 'ranking': 7}
        ]

        self.mock_now = datetime.datetime.now()
        self.mock_datetime = MagicMock()
        self.mock_datetime.now.return_value = self.mock_now

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
        result = self.db.insert_one(table_name='test', data=tuple(test_datas.values()))
        self.assertTrue(result)

        # Verify that cursor.execute and connection.commit were called
        self.assertTrue(self.mock_cursor.execute.called)
        self.assertTrue(self.mock_connection.commit.called)

    def test_execute_many(self):
        # Test execute_many method
        datas = [tuple(d.values()) for d in self.datas]

        result = self.db.insert_many(table_name='test', datas=datas, types=InsertType.IGNORE)
        self.assertTrue(result)

        # Verify that cursor.executemany and connection.commit were called
        self.assertTrue(self.mock_cursor.executemany.called)
        self.assertTrue(self.mock_connection.commit.called)

    def test_insert_many_ignore(self):
        # Test insert_many ignore type method
        self.mock_now = datetime.datetime.now()
        self.mock_datetime.now.return_value = self.mock_now

        datas = [tuple(d.values()) for d in self.datas]

        result = self.db.insert_many(table_name='test', datas=datas, types=InsertType.IGNORE)
        self.assertTrue(result)

        expected_sql = "INSERT IGNORE INTO test VALUES (%s, %s, %s, %s);"

        expected_values = [(4, 'four', '4', self.mock_now), (5, 'five', 5, self.mock_now),
                           ('6', 'six', '6', self.mock_now), ('7', 'seven', 7, self.mock_now)]
        self.mock_cursor.executemany.assert_called_once_with(expected_sql, expected_values)

    def test_insert_many_duplicate(self):
        # Test insert_many duplicate type method
        mock_now = datetime.datetime.now()
        self.mock_datetime.now.return_value = mock_now
        datas = [tuple(d.values()) for d in self.datas]
        columns = list(self.datas[0].keys())

        result = self.db.insert_many(table_name='test', datas=datas, types=InsertType.DUPLICATE, column_datas=columns)

        expected_sql = ("INSERT INTO test (id, username, ranking) VALUES (%s, %s, %s, %s) ON DUPLICATE KEY "
                        "UPDATE id = VALUES(id), username = VALUES(username), ranking = VALUES(ranking);")

        expected_values = [(4, 'four', '4', mock_now), (5, 'five', 5, mock_now),
                           ('6', 'six', '6', mock_now), ('7', 'seven', 7, mock_now)]
        self.mock_cursor.executemany.assert_called_once_with(expected_sql, expected_values)

    @patch('server.utility.Utility.is_none_or_empty')
    def test_get_table(self, mock_is_none_or_empty):
        mock_is_none_or_empty.side_effect = [True, True, False]
        self.db.get_table(table_name="test_table")

        expected_sql = "SELECT * FROM test_table "
        self.mock_cursor.execute.assert_called_once_with(expected_sql)

        result = self.db.get_table(table_name="test_table", types=AggregateType.COUNT)

        expected_sql = "SELECT COUNT(*) FROM test_table "
        self.mock_cursor.execute.assert_called_with(expected_sql)

        expected_sql = "SELECT COUNT(*) FROM test_table"
        self.db.get_table(table_name="test_table")
        self.assertFalse(self.db._result.get("test_table" + "" + AggregateType.NONE.name).called)

    def test_get_table_with_aggregate_type(self):
        for aggregate_type in AggregateType:
            self.db.get_table(table_name="test_table", types=aggregate_type)
            self.mock_cursor.execute.assert_called_with(
                aggregate_type.return_query_by_aggregate_type(table_name="test_table"))

    def test_close(self):
        # Close connections after tests
        self.db.close()

        self.assertEqual(self.mock_cursor.close.call_count, 1)
        self.assertTrue(self.mock_cursor.closed())
        self.assertEqual(self.mock_connection.close.call_count, 1)
        self.assertTrue(self.mock_connection.closed())


if __name__ == '__main__':
    unittest.main()
