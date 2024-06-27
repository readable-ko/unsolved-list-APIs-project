import datetime
import mysql.connector
from collections import defaultdict
from enum import Enum

from server.utility import Utility


class InsertType(Enum):
    IGNORE = 1
    DUPLICATE = 0


class AggregateType(Enum):
    NONE = "SELECT * FROM {table_name} {query}"
    COLUMN = "SELECT {query} FROM {table_name}"
    COUNT = "SELECT COUNT(*) FROM {table_name} {query}"
    SUM = "SELECT SUM(*) FROM {table_name} {query}"
    MAX = "SELECT MAX({query}) FROM {table_name}"
    MIN = "SELECT MIN({query}) FROM {table_name}"

    def return_query_by_aggregate_type(self, table_name: str, query: str = ""):
        return self.value.format(table_name=table_name, query=query)


class DBController:
    def __init__(self, config_path='config.json'):
        self._cursor = None
        self._connection = None
        self._config = Utility.load_config(config_path)
        # todo redis 사용하여 더 빠르게 쓰는 방법 고민해보자
        self._result = defaultdict()

    def init(self):
        host_name = self._config['database']['hostName']
        port = self._config['database']['port']
        schema = self._config['database']['schemaName']
        user_name = self._config['database']['userName']
        password = self._config['database']['password']

        self._connection = mysql.connector.connect(
            host=host_name,
            user=user_name,
            password=password,
            port=port,
            database=schema
        )
        self._cursor = self._connection.cursor()

    def _refresh(self, table_name: str, query: str = "", types: AggregateType = AggregateType.NONE):
        sql = types.return_query_by_aggregate_type(table_name=table_name, query=query)
        try:
            self._cursor.execute(sql)
            self._result[table_name + query + types.name] = self._cursor.fetchall()
        except mysql.connector.Error as err:
            print("mysql.connector.Error:", err.msg, err)

    def _execute_many(self, table_name: str, values: list[tuple], query: str):
        """
        execute multiple query into the database.
        :param table_name: name of the table to insert into the database schemae.
        :param values: list of dictionaries to insert into the database (key: column name and value: data value)
        :param query: query for "WHERE" clause or etc.
        :return: Boolean type indicating whether the insertion was successful
        """
        try:
            self._cursor.executemany(query, values)
            self._connection.commit()

            self._refresh(table_name)
        except mysql.connector.Error as err:
            # Todo Make a logging for check error.
            print("mysql multi insertion error: ", err.msg, err)
            return False
        return True

    def _insert_many_ignore(self, table_name: str, datas: list[tuple]):
        placeholders = ', '.join(["%s"] * (len(datas[0])))
        query = f"INSERT IGNORE INTO {table_name} VALUES ({placeholders});"

        return self._execute_many(table_name, datas, query)

    def _insert_many_duplicates(self, table_name: str, datas: list[tuple], column_datas: list[str]):
        columns = ', '.join(column_datas)
        update_statements = ', '.join([f"{key} = VALUES({key})" for key in column_datas])
        placeholders = ', '.join(["%s"] * (len(datas[0])))

        query = f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders}) ON DUPLICATE KEY UPDATE {update_statements};"

        return self._execute_many(table_name, datas, query)

    def get_table(self, table_name: str, query: str = "", types: AggregateType = AggregateType.NONE):
        """
        :param query: query for "WHERE" clause or etc.
        :param table_name: name of the table to select from
        :param types: the aggregate function to use on (ex:SUM, COUNT, MAX)
        :return: select all contents from chosen table
        """
        if Utility.is_none_or_empty(self._result.get(table_name + query + types.name)):
            self._refresh(table_name, query, types)
        return self._result[table_name + query + types.name]

    def insert_one(self, table_name: str, data: tuple):
        """
        Insert one data into the database
        :param table_name: name of the table to insert into the database
        :param data: dictionary with the data to be inserted (key: column name and value: data value)
        :return: Boolean type indicating whether the insertion was successful
        """

        try:
            current_time = [datetime.datetime.now()]
            value = tuple(list(data) + current_time)

            placeholders = ', '.join(["%s"] * (len(data)))
            sql = f"INSERT INTO {table_name} VALUES ({placeholders});"

            self._cursor.execute(sql, value)
            self._connection.commit()
            self._refresh(table_name)

        except mysql.connector.Error as err:
            # Todo Make a logging for check error.
            print("mysql insertion error: ", err.msg, err)
            return False

        return True

    def insert_many(self, table_name: str, datas: list[tuple], types: InsertType, column_datas: list[str] = None):
        """
        this funcion inserts a list of data into a @table_name table with InsertType and column
        default insert type is insert ignore many. duplicate insert will be ignored
        :param table_name: table name to insert into.
        :param datas: list of data you want to insert.
        :param types: insert types (ignore, duplicate)
        :param column_datas: default is none which will be using for duplicate insert
        :return: Boolean indicating whether the insert was successful
        """
        if Utility.is_none_or_empty(datas):
            return False

        modified_data = [list(data) + [datetime.datetime.now()] for data in datas]
        values = [tuple(data) for data in modified_data]

        if InsertType.IGNORE.value == types.value:
            return self._insert_many_ignore(table_name, values)
        elif InsertType.DUPLICATE.value == types.value:
            return self._insert_many_duplicates(table_name, values, column_datas)
        else:
            return False

    def close(self):
        self._cursor.close()
        self._connection.close()
