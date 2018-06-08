package cn.golango.rabbitmq.confirm;

import cn.golango.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        Connection connection = ConnectionUtils.getConnection();

        Channel channel = connection.createChannel();
        channel.queueDeclare("test_simple_queue_confirm1", false, false, false, null);

        channel.confirmSelect();

        for (int i = 0; i < 10; i++) {
            channel.basicPublish("", "test_simple_queue_confirm1", null, "hello".getBytes());
        }

        if (!channel.waitForConfirms()) {
            System.out.println("failed");
        } else {
            System.out.println("success");
        }

        channel.close();
        connection.close();
    }
}
