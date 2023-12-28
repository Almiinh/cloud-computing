package sqs;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

public class SQSCreateQueue {

    /**
     * Creates a new queue in Amazon SQS with the specified name.
     *
     * @param queueName Name of the queue to be created.
     * @return The URL of the created queue, or an empty string if creation fails.
     * @throws SqsException If there is an error with the SQS Client.
     */
    public static void createQueue(String queueName) {

        try (SqsClient sqsClient = SqsClient.builder().build()) {
            System.out.println("[SQS] Creating Queue '" + queueName + "' ...");
            CreateQueueRequest queueCreationRequest = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();
            sqsClient.createQueue(queueCreationRequest);
            System.out.println("[SQS] Queue created");

        } catch (SqsException e) {
            System.err.println("[SQS] Error during queue creation: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
