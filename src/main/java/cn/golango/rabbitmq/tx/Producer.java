package cn.golango.rabbitmq.tx;

import cn.golango.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = ConnectionUtils.getConnection();

        Channel channel = connection.createChannel();
        channel.queueDeclare("test_simple_queue_tx", false, false, false, null);

        try {
            channel.txSelect();
            channel.basicPublish("", "test_simple_queue_tx", null, "hello".getBytes());

            channel.txCommit();
        }
        catch (Exception ex){

            System.out.println("txRollback");
            ex.printStackTrace();
            channel.txRollback();
        }

        channel.close();
        connection.close();
    }
}
