import mysql.connector
import json


def _check_none(value):
    if value is None:
        return True
    return False


class DBController:
    def __init__(self):
        self._config = None
        self._cursor = None
        self._connection = None
        self._result = None

    def init(self):
        with open('config.json') as config_file:
            self._config = json.load(config_file)
        config_file.close()

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
        self._result = self._cursor.fetchall()

    def get_table(self, table_name: str):
        if _check_none(self._result):
            self._refresh(table_name)
        return self._result

    def insert_one(self, table_name: str, data: dict):
        columns = ', '.join(data.keys())
        placeholders = ', '.join(["%s"] * len(data))
        sql = f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders});"

        try:
            self._cursor.execute(sql, tuple(data.values()))
            self._connection.commit()
            self._refresh(table_name)

        except mysql.connector.Error as err:
            # Todo Make a logging for check error.
            print(err.msg, err)
            return False

        return True

    def insert_many(self, table_name: str, datas: list):
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
            print(err.msg, err)
            return False
        return True

    def close(self):
        self._cursor.close()
        self._connection.close()