package sqs;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public class SQSEmptyQueue {

    public static void main(String[] args) {
        emptyQueue("INBOX");
        emptyQueue("OUTBOX");
    }

    public static void emptyQueue(String queueName) {
        try(SqsClient sqsClient = SqsClient.builder().build()) {;
            String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();

            // Receive and delete messages from the queue until it's empty
            while (true) {
                ReceiveMessageResponse receiveResponse = sqsClient.receiveMessage(
                        ReceiveMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .maxNumberOfMessages(10) // Adjust as needed
                                .build()
                );

                receiveResponse.messages().stream().map(message -> DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build()).forEach(sqsClient::deleteMessage);

                // If no more messages are available, exit the loop
                if (receiveResponse.messages().isEmpty()) {
                    break;
                }
            }
        }
    }
}

