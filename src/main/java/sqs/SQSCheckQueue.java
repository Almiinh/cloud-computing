package sqs;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

public class SQSCheckQueue {

    /**
     * Checks if a queue exists
     * @param queueName The name of the SQS queue to check
     * @return a boolean whether it exists
     */
    public static boolean exists(String queueName) {
        try (SqsClient sqsClient = SqsClient.builder().build()) {
            sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
