
package emse;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.List;

public class SQSRetrieveMessage {

    /**
     * This method fetches messages from the specified SQS queue.
     * 
     * @param sqsClient The client object for SQS service.
     * @param queueEndpoint The URL of the SQS queue.
     * @param queueIdentifier The name of the queue for identification purposes.
     * @return A list of messages, or null if an error occurs.
     * @throws SqsException Thrown if an error occurs in message retrieval.
     */
    public static List<Message> retrieveMessages(SqsClient sqsClient, String queueEndpoint, String queueIdentifier) {

        System.out.println("\nBeginning the process of fetching messages.");

        try {
            ReceiveMessageRequest messageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueEndpoint)
                    .maxNumberOfMessages(5)
                    .build();

            List<Message> fetchedMessages = sqsClient.receiveMessage(messageRequest).messages();
            System.out.println("\nReceived messages from " + queueIdentifier + ":");
            for (Message message : fetchedMessages) {
                System.out.println("\n" + message.body()); // Displaying each message
            }
            System.out.println("\nFetching process completed.");
            return fetchedMessages; // Returning the messages for further processing or deletion

        } catch (SqsException e) {
            System.err.println("Error encountered: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
}
