import unittest
from unittest.mock import MagicMock, patch
from server.consumer import Consumer


class TestConsumer(unittest.TestCase):

    @patch('server.consumer.RabbitMQController')
    @patch('server.consumer.DataTransformer')
    @patch('server.consumer.DBController')
    @patch('server.consumer.Utility')
    def setUp(self, MockUtility, MockDBController, MockDataTransformer, MockRabbitMQController):
        self.mock_config = {
            "consumer": {
                "user_table": "users",
                "user_solved_table": "solved_users",
                "query": "SELECT * FROM users WHERE user_name = %s",
                "user_solved": "solved_count"
            }
        }

        MockUtility.load_config.return_value = self.mock_config
        self.mock_db_controller = MockDBController.return_value
        self.mock_transformer = MockDataTransformer.return_value
        self.mock_mq_controller = MockRabbitMQController.return_value

        self.consumer = Consumer(config_path="mock_config.json")

    def test_init(self):
        self.consumer.init()
        self.assertTrue(self.mock_mq_controller.init.called)
        self.assertTrue(self.mock_transformer.init.called)
        self.assertTrue(self.mock_db_controller.init.called)

    def test_should_skip_true(self):
        user_name = "tester"
        given_data = 10
        self.mock_transformer.get_db_user_solved.return_value = 10
        self.consumer._transformer = self.mock_transformer

        result = self.consumer._should_skip(user_name, given_data)
        self.assertTrue(result)

    def test_should_skip_false(self):
        user_name = "tester"
        given_data = 5
        self.mock_transformer.get_db_user_solved.return_value = 10
        self.consumer._transformer = self.mock_transformer

        result = self.consumer._should_skip(user_name, given_data)
        self.assertFalse(result)

    @patch('server.consumer.get_info_from_message_body')
    @patch('server.consumer.Utility')
    def test_start(self, MockUtility, MockGetInfo):
        MockUtility.is_none_or_empty.side_effect = [False, False]  # For body not empty, then empty
        MockGetInfo.return_value = ("test_user", 10)

        mock_method_frame = MagicMock()
        mock_method_frame.delivery_tag = "tag"
        mock_header_frame = MagicMock()
        mock_body = "message_body".encode("utf-8")
        self.mock_mq_controller.pop_queue.return_value = (mock_method_frame, mock_header_frame, mock_body)

        self.mock_transformer.get_api_called_count.side_effect = [500, 512, 512]
        self.consumer._transformer.get_api_called_count.side_effect = [500, 512, 512]

        self.mock_transformer.get_db_user_solved.return_value = [{"solved_count": 3}]
        self.mock_transformer.get_api_user_solved.return_value = [("glory", 3), ("forever", 4)]

        self.consumer.start()

        self.assertTrue(self.mock_mq_controller.pop_queue.called)
        self.assertTrue(self.mock_transformer.get_db_user_solved.called)
        self.assertTrue(self.mock_transformer.get_api_user_solved.called)
        self.assertTrue(self.mock_db_controller.insert_many.called)
        self.assertTrue(self.mock_mq_controller.send_ack.called)

    def tearDown(self):
        self.consumer = None


if __name__ == '__main__':
    unittest.main()

