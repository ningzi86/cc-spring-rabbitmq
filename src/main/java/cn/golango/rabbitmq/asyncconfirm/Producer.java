package cn.golango.rabbitmq.asyncconfirm;

import cn.golango.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());

        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("test_simple_queue_asyncconfirm", false, false, false, null);

        channel.confirmSelect();

        channel.addConfirmListener(new ConfirmListener() {
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    System.out.println("handleAck--multiple:"+deliveryTag);
                    confirmSet.headSet(deliveryTag + 1).clear();
                } else {
                    System.out.println("handleAck--single:"+deliveryTag);
                    confirmSet.remove(deliveryTag);
                }
            }
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("Nack, SeqNo: " + deliveryTag + ", multiple: " + multiple);
                if (multiple) {
                    confirmSet.headSet(deliveryTag + 1).clear();
                } else {
                    confirmSet.remove(deliveryTag);
                }
            }
        });


        while (true) {
            long nextSeqNo = channel.getNextPublishSeqNo();
            channel.basicPublish("", "test_simple_queue_asyncconfirm", null, "hello".getBytes());
            confirmSet.add(nextSeqNo);
        }

//        channel.close();
//        connection.close();

    }
}
