package cn.golango.rabbitmq.workfair;

import cn.golango.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer1 {

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = ConnectionUtils.getConnection();
        final Channel channel = connection.createChannel();

        channel.basicQos(1);

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                String message = new String(body, "UTF-8");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(" [1] Received '" + message + "'");

                //手动应答
                channel.basicAck(envelope.getDeliveryTag(), false);

            }
        };

        //自动应答改成false
        channel.basicConsume("test_simple_queue", false, consumer);

    }
}
