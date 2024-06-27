import json


class Utility:
    @staticmethod
    def load_json(config_file):
        return json.loads(config_file)

    @staticmethod
    def load_config(config_path):
        with open(config_path) as config_file:
            return json.load(config_file)

    @staticmethod
    def is_none_or_empty(value):
        if not value:
            return True
        return False
