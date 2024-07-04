import unittest
from unittest.mock import patch, MagicMock
from server.broker import Broker


class TestBroker(unittest.TestCase):

    @patch('server.data_transformer.DBController')
    @patch('server.data_transformer.ApiController')
    @patch('server.rabbit_mq_controller.RabbitMQController')
    def setUp(self, MockRabbitMQController, MockApiController, MockDBController):
        self.mock_mq_con = MockRabbitMQController.return_value
        self.mock_db_con = MockDBController.return_value
        self.mock_api_con = MockApiController.return_value

        self.broker = Broker(config_path='config.json')
        self.broker.mq_con = self.mock_mq_con
        self.broker.db_con = self.mock_db_con
        self.broker.api_con = self.mock_api_con

        self.broker.init()

    def test_send(self):
        # Mocking choose_user to return a specific set of users
        self.broker.transformer.choose_user = MagicMock(return_value={('user3', 3), ('user1', 1), ('user2', 2)})

        self.broker.send()

        # Verify that push_queue is called with the correct messages
        self.mock_mq_con.push_queue.assert_any_call({'user3': 3})
        self.mock_mq_con.push_queue.assert_any_call({'user1': 1})
        self.mock_mq_con.push_queue.assert_any_call({'user2': 2})

    def tearDown(self):
        self.broker.close()


if __name__ == '__main__':
    unittest.main()
