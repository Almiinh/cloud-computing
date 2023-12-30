package sqs;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public class SQSEmptyQueue {

    public static void main(String[] args) {
        emptyQueue("INBOX.fifo");
        emptyQueue("OUTBOX.fifo");
    }

    public static void emptyQueue(String queueName) {
        try (SqsClient sqsClient = SqsClient.create()) {
            String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
            if (SQSCheckQueue.hasMessages(queueName)) {
                sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(queueUrl).build());
                Thread.sleep(60000);
            }
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(queueUrl).build();
            System.out.println("[SQS] " + sqsClient.receiveMessage(receiveMessageRequest).messages().size() + " messages");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

