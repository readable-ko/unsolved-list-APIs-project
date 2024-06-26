from server.api_controller import ApiController
from server.db_controller import DBController
from server.utility import Utility


class DataTransformer:
    def __init__(self, config_path="config.json"):
        self._db_con = DBController()
        self._ap_con = ApiController()
        self._config = Utility.load_config(config_path)

    def init(self):
        self._db_con.init()
        self._ap_con.init()

    def _filtered_data(self, json_data: dict):
        """
        Extract specified keys and values from Json data
        :param json_data: List of dictionaries represent from json
        :return: List of dictionaries with keys extracted
        """
        filtering_key = self._config["transform"]["filtering_key"]

        """
        {key: items.get(key) for key in filtering_key}
                             vs
        {key: items[key] for key in filtering_key if key in items}
        """
        filtered_datas = [tuple(items.get(key) for key in filtering_key) for items in json_data]

        return filtered_datas

    def choose_user(self):
        """
        function for get the user list.
        :return: API(refreshed user) - DB(user)
        """
        db_org_data = self._db_con.get_table(self._config["transform"]["user_table"])
        api_org_data = self._filtered_data(self._ap_con.get_org_api())

        update_user = set(api_org_data) - set(db_org_data)
        self._db_con.insert_many(table_name=self._config["transform"]["user_table"], datas=list(update_user))
        return update_user

    def close(self):
        self._db_con.close()
