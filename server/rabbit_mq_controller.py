import json

import pika
from pika.exceptions import AMQPConnectionError

from server.utility import Utility


class RabbitMQController:
    def __init__(self, config_path='config.json'):
        self._connection = None
        self._channel = None
        self._config = Utility.load_config(config_path)

    def init(self):
        try:
            hostname = self._config['rabbitMQ']['hostName']
            self._connection = pika.BlockingConnection(
                pika.ConnectionParameters(hostname)
            )
            self._channel = self._connection.channel()
        except AMQPConnectionError as e:
            print(f"connection error: {e}")
            self._connection = None
            self._channel = None

    def push_queue(self, pure_message=""):
        if Utility.is_none_or_empty(self._channel):
            print("Channel is not initialized")
            return

        queue_name = self._config['rabbitMQ']['queueName']
        exchange = self._config['rabbitMQ']['exchange']

        # check the queue declaration
        self._channel.queue_declare(queue=queue_name, durable=True)

        message = json.dumps(pure_message).encode('utf-8')

        self._channel.basic_publish(
            exchange=exchange,
            routing_key=queue_name,
            body=message,
            properties=pika.BasicProperties(
                delivery_mode=pika.DeliveryMode.Persistent
            )
        )

    def pop_queue(self):
        """
        after receiving a message from the queue, you must call send_ack() to notice that you have received
        :return: method_frame, header_frame, body (None for fail)
        """
        if Utility.is_none_or_empty(self._channel):
            print("Channel is not initialized")
            return None

        queue_name = self._config['rabbitMQ']['queueName']
        exchange = self._config['rabbitMQ']['exchange']

        self._channel.queue_declare(queue=queue_name, durable=True)

        self._channel.basic_qos(prefetch_count=1)

        method_frame, header_frame, body = self._channel.basic_get(queue=queue_name, auto_ack=False)

        if method_frame:
            return method_frame, header_frame, body
        else:
            print("No message received")
            return None, None, None

    def send_ack(self, delivery_tag):
        """
        this method must execute after the pop_queue() method is called
        :param delivery_tag: delivery tag in method_frame
        :return:
        """
        if Utility.is_none_or_empty(self._channel):
            print("Channel is not initialized")
            return

        self._channel.basic_ack(delivery_tag=delivery_tag)

    def end_connection(self):
        if not Utility.is_none_or_empty(self._channel):
            self._channel.close()
        if not Utility.is_none_or_empty(self._connection):
            self._connection.close()
