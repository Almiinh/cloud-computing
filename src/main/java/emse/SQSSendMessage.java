
package emse;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SqsException;

public class SQSSendMessage {

    /**
     * This method is used to send a batch of messages to an SQS queue. It includes details about the bucket and the file.
     *
     * @param sqsClient The SQS client used for sending messages.
     * @param queueEndpoint The URL of the SQS queue.
     * @param bucketName The name of the S3 bucket.
     * @param fileName The name of the file to be processed.
     * @throws SqsException If any issue occurs while sending messages.
     */
    public static void sendMessages(SqsClient sqsClient, String queueEndpoint, String bucketName, String fileName) {

        System.out.println("\nInitiating the process to send batch messages for bucket and file names.");

        try {
            SendMessageBatchRequest batchRequest = SendMessageBatchRequest.builder()
                    .queueUrl(queueEndpoint)
                    .entries(
                        SendMessageBatchRequestEntry.builder().id("msg1").messageBody(bucketName).build(),
                        SendMessageBatchRequestEntry.builder().id("msg2").messageBody(fileName).delaySeconds(10).build()
                    )
                    .build();
            sqsClient.sendMessageBatch(batchRequest);

            System.out.println("\nMessage batch successfully sent.");

        } catch (SqsException e) {
            System.err.println("Error encountered: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
