package emse;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

public class SQSCreateQueue {

    /**
     * This function creates a new queue in Amazon SQS with the specified name.
     *
     * @param sqsClient The SQS Client to be used for creating the queue.
     * @param queueName Name of the queue to be created.
     * @return The URL of the created queue, or an empty string if creation fails.
     * @throws SqsException If there is an error with the SQS Client.
     */
    public static String createQueue(SqsClient sqsClient, String queueName) {

        try {
            System.out.println("\nInitiating Queue Creation...");

            CreateQueueRequest queueCreationRequest = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();

            sqsClient.createQueue(queueCreationRequest);

            System.out.println("\nRetrieving Queue URL...");

            GetQueueUrlResponse queueUrlResponse =
                    sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
            String queueUrl = queueUrlResponse.queueUrl();
            return queueUrl;

        } catch (SqsException e) {
            System.err.println("Error during queue creation: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /* Additional methods like listing queues can be added here if needed. */
}
