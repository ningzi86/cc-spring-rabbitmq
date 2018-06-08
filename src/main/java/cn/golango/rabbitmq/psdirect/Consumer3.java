package cn.golango.rabbitmq.psdirect;

import cn.golango.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer3 {

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = ConnectionUtils.getConnection();
        final Channel channel = connection.createChannel();

        channel.exchangeDeclare("test_simple_direct", "direct");
        channel.queueDeclare("test_simple_durable_direct2", true, false, false, null);

        //已绑定交换机,不需要重新绑定
        //channel.queueBind("test_simple_durable_direct2", "test_simple_direct", "info");
        //channel.queueBind("test_simple_durable_direct2", "test_simple_direct", "warning");
        //channel.queueBind("test_simple_durable_direct2", "test_simple_direct", "error");

        channel.basicQos(1);

        final int[] index = {0};

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                String message = new String(body, "UTF-8");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("[3] [" + index[0] + "] Received '" + message + "'");

                //手动应答
                channel.basicAck(envelope.getDeliveryTag(), false);

                index[0]++;
            }
        };

        channel.basicConsume("test_simple_durable_direct2", false, consumer);

    }
}
