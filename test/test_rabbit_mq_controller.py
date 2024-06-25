import pika
import unittest
from unittest.mock import patch, MagicMock

from server.rabbit_mq_controller import RabbitMQController


class TestRabbitMQController(unittest.TestCase):
    @patch('pika.BlockingConnection')
    def setUp(self, mock_connection):
        # Mock configuration
        self.mock_config = {
            'rabbitMQ': {
                'hostName': 'localhost',
                'queueName': 'test_queue',
                'exchange': 'test_exchange'
            }
        }

        # Mock channel and connection
        self.mock_connection = MagicMock()
        self.mock_channel = MagicMock()
        self.mock_connection.channel.return_value = self.mock_channel
        mock_connection.return_value = self.mock_connection

        # Initialize RabbitMQController with mock config
        self.mq = RabbitMQController()
        self.mq._config = self.mock_config

        # Call init method to establish mock connection
        self.mq.init()

    def test_push_queue(self):
        message = "test message"
        self.mq.push_queue(message)

        self.assertEqual(self.mock_channel.basic_publish.call_count, 1)
        self.mock_channel.basic_publish.assert_called_with(
            exchange='test_exchange',
            routing_key='test_queue',
            body=message,
            properties=pika.BasicProperties(
                delivery_mode=pika.DeliveryMode.Persistent
            )
        )

    def test_pop_queue(self):
        mock_method_frame = MagicMock()
        mock_header_frame = MagicMock()
        mock_body = "test message"
        self.mock_channel.basic_get.return_value = (mock_method_frame, mock_header_frame, mock_body)

        result = self.mq.pop_queue(callback=lambda x: print("received message"))

        self.assertEqual(result, (mock_method_frame, mock_header_frame, mock_body))
        self.mock_channel.basic_get.assert_called_with(queue='test_queue', auto_ack=False)

    def test_send_ack(self):
        mock_method = MagicMock()
        self.mq.send_ack(mock_method.delivery_tag)

        self.mock_channel.basic_ack.assert_called_with(delivery_tag=mock_method.delivery_tag)

    def test_end_connection(self):
        self.mq.end_connection()

        self.assertEqual(self.mock_channel.close.call_count, 1)
        self.assertTrue(self.mock_channel.closed())

        self.assertEqual(self.mock_connection.close.call_count, 1)
        self.assertTrue(self.mock_connection.closed())

if __name__ == '__main__':
    unittest.main()