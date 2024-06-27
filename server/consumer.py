import time

from server.data_transformer import DataTransformer, get_info_from_message_body
from server.db_controller import DBController, InsertType
from server.rabbit_mq_controller import RabbitMQController
from server.utility import Utility


class Consumer(object):
    def __init__(self, config_path="config.json"):
        self._mq_con = RabbitMQController(config_path)
        self._transformer = DataTransformer(config_path)
        self._db_con = DBController(config_path)
        self._config = Utility.load_config(config_path)

    def init(self):
        self._mq_con.init()
        self._transformer.init()
        self._db_con.init()

    def _should_skip(self, user_name, given_data):
        table_name = self._config["consumer"]["user_table"]
        query = self._config["consumer"]["query"] % user_name
        db_data = self._transformer.get_db_user_solved(table_name=table_name, query=query)

        if db_data == given_data:
            return True
        return False

    def start(self):
        while self._transformer.get_api_called_count() <= 500:
            method_frame, header_frame, body_en = self._mq_con.pop_queue()
            # if the message queue is empty end the program
            if Utility.is_none_or_empty(body_en):
                print("NOTHING TO CONSUME!")
                break

            body_json = body_en.decode("utf-8")
            body = Utility.load_json(body_json)

            user_name, solved_count = get_info_from_message_body(body)

            # if user data is already refreshed skip the user
            if self._should_skip(user_name, solved_count):
                self._mq_con.send_ack(method_frame.delivery_tag)
                continue

            # save data on database
            saving_table_name = self._config["consumer"]["user_solved_table"]
            api_user_solved = self._transformer.get_api_user_solved(user_name)
            self._db_con.insert_many(
                table_name=saving_table_name,
                datas=api_user_solved,
                types=InsertType.IGNORE
            )

            print(f"#:{self._transformer.get_api_called_count()} {api_user_solved[0][0]} DONE")
            self._mq_con.send_ack(method_frame.delivery_tag)
            time.sleep(1)


if __name__ == "__main__":
    consumer = Consumer()
    consumer.init()
    consumer.start()
