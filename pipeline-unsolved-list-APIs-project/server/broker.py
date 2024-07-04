from server.data_transformer import DataTransformer
from server.rabbit_mq_controller import RabbitMQController


class Broker:
    def __init__(self, config_path='config.json'):
        self.db_users = []
        self.api_users = []
        self.mq_con = RabbitMQController(config_path)
        self.transformer = DataTransformer(config_path)

    def init(self):
        self.mq_con.init()
        self.transformer.init()

    def close(self):
        self.mq_con.end_connection()
        self.transformer.close()

    def send(self):
        update_user = self.transformer.choose_user()
        for user in update_user:
            message = {user[0]: user[1]}
            self.mq_con.push_queue(message)
            print(f"[X] Message sent {message}")
        print(f"TOTAL {len(update_user)} messages sent.")


if __name__ == '__main__':
    broker = Broker()
    broker.init()
    broker.send()
    broker.close()
