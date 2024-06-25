import mysql.connector
import json
from collections import defaultdict
from server.utility import Utility


class DBController:
    def __init__(self, config_path='config.json'):
        self._cursor = None
        self._connection = None
        self._result = defaultdict()
        self._config = Utility.load_config(config_path)

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

    def _refresh(self, table_name):
        sql = f"SELECT * FROM {table_name}"
        self._cursor.execute(sql)
        self._result[table_name] = self._cursor.fetchall()

    def get_table(self, table_name: str):
        """
        :param table_name: name of the table to get
        :return: select all contents from chosen table
        """
        if Utility.check_none(self._result.get(table_name)):
            self._refresh(table_name)
        return self._result[table_name]

    def insert_one(self, table_name: str, data: dict):
        """
        Insert one data into the database
        :param table_name: name of the table to insert into the database
        :param data: dictionary with the data to be inserted (key: column name and value: data value)
        :return:
        """
        columns = ', '.join(data.keys())
        placeholders = ', '.join(["%s"] * len(data))
        sql = f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders});"

        try:
            self._cursor.execute(sql, tuple(data.values()))
            self._connection.commit()
            self._refresh(table_name)

        except mysql.connector.Error as err:
            # Todo Make a logging for check error.
            print("mysql insertion error: ", err.msg, err)
            return False

        return True

    def insert_many(self, table_name: str, datas: list):
        """
        Insert multiple data into the database
        :param table_name: name of the table to insert into the database schema
        :param datas: list of dictionaries to insert into the database (key: column name and value: data value)
        :return: Boolean type indicating whether the insertion was successful
        """
        if not datas:
            return False

        columns = ', '.join(datas[0].keys())
        placeholders = ', '.join(["%s"] * len(datas[0]))
        sql = f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders});"

        try:
            values = [tuple(data.values()) for data in datas]
            self._cursor.executemany(sql, values)
            self._connection.commit()

            self._refresh(table_name)
        except mysql.connector.Error as err:
            # Todo Make a logging for check error.
            print("mysql multi insertion error: ", err.msg, err)
            return False
        return True

    def close(self):
        self._cursor.close()
        self._connection.close()
