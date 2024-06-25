import json


class Utility:
    @staticmethod
    def load_config(config_path):
        with open(config_path) as config_file:
            return json.load(config_file)

    @staticmethod
    def check_none(value):
        if not value:
            return True
        return False
