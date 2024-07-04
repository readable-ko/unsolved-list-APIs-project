from server.api_controller import ApiController
from server.db_controller import DBController, InsertType, AggregateType
from server.utility import Utility


def get_info_from_message_body(data: dict):
    username = list(data.keys())[0]
    solved_count = data[username]
    return username, solved_count


def _filtered_data(json_data: dict, filtering_key):
    """
    Extract specified keys and values from Json data
    :param json_data: List of dictionaries represent from json
    :param filtering_key: Key to be keep from filtered data
    :return: List of dictionaries with keys extracted
    """

    """
    {key: items.get(key) for key in filtering_key}
                         vs
    {key: items[key] for key in filtering_key if key in items}
    """
    filtered_datas = [tuple(items.get(key) for key in filtering_key) for items in json_data]

    return filtered_datas


class DataTransformer:
    def __init__(self, config_path="config.json"):
        self._db_con = DBController()
        self._ap_con = ApiController()
        self._config = Utility.load_config(config_path)

    def init(self):
        self._db_con.init()
        self._ap_con.init()

    def choose_user(self):
        """
        function for get the user list.
        :return: set(tuple) with data of API(refreshed user) - DB(user)
        """
        filtered_column = ', '.join(self._config["transform"]["user_columns"][:-1])
        db_org_data = self._db_con.get_table(self._config["transform"]["user_table"], query=filtered_column,
                                             types=AggregateType.COLUMN)
        filtering_key = self._config["transform"]["user_filtering_key"]
        api_org_data = _filtered_data(self._ap_con.get_org_api(), filtering_key)

        update_user = set(api_org_data) - set(db_org_data)
        columns = self._config["transform"]["user_columns"]

        self._db_con.insert_many(
            table_name=self._config["transform"]["user_table"],
            datas=list(update_user),
            types=InsertType.DUPLICATE,
            column_datas=columns
        )

        return update_user

    def get_db_user_solved(self, table_name: str, query: str):
        """
        function for get the user solved problem from database
        :param table_name: table_name to get the user information from
        :param query: query for more clause like WHERE or GROUP BY or etc.
        :return: list(tuple) of data
        """
        return self._db_con.get_table(table_name=table_name, query=query, types=AggregateType.COUNT)[0][0]

    def get_api_user_solved(self, username):
        """
        function for get the user solved problem from APIs
        :param username: username to search
        :return: list(tuple) of data with username on first column
        """
        problems = self._ap_con.get_user_api(username=username)
        filtering_key = self._config["transform"]["problem_filtering_key"]
        api_user_solved = _filtered_data(problems, filtering_key)

        user_solved = [(username,) + items for items in api_user_solved]
        return user_solved

    def get_api_called_count(self):
        """
        return the number of times you called api
        :return: Integer
        """
        return self._ap_con.get_called_times()

    def close(self):
        self._db_con.close()
