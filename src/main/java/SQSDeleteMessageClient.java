
package emse;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.List;

public class SQSDeleteMessageClient {

    /**
     * This method is responsible for deleting a batch of messages from a specified SQS queue.
     * 
     * @param sqsClient The client object for interacting with SQS.
     * @param queueEndpoint The URL of the SQS queue from which messages are to be deleted.
     * @param messageBatch A list of messages that are to be deleted.
     * @throws SqsException If any error occurs during the deletion process.
     */
    public static void deleteMessages(SqsClient sqsClient, String queueEndpoint, List<Message> messageBatch) {

        System.out.println("\nInitiating message deletion process...");

        try {
            for (Message msg : messageBatch) {
                DeleteMessageRequest messageDeletionRequest = DeleteMessageRequest.builder()
                        .queueUrl(queueEndpoint)
                        .receiptHandle(msg.receiptHandle())
                        .build();
                sqsClient.deleteMessage(messageDeletionRequest);

                System.out.println("\nMessage deleted successfully.");
            }

        } catch (SqsException e) {
            System.err.println("Error during message deletion: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
