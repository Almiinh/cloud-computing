package sqs;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SqsException;

public class SQSSendMessage {

    /**
     * This method is used to send a batch of messages to an SQS queue. It includes details about the bucket and the file.
     *
     * @param queueName     The name of the SQS queue.
     * @param bucketName    The name of the S3 bucket.
     * @param fileName      The name of the file to be processed.
     * @throws SqsException If any issue occurs while sending messages.
     */
    public static void sendMessages(String queueName, String bucketName, String fileName) {
        System.out.println("[SQS] Sending batch messages for bucket and file names...");

        try (SqsClient sqsClient = SqsClient.builder().build()) {
            String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
            SendMessageBatchRequest batchRequest = SendMessageBatchRequest.builder().queueUrl(queueUrl).entries(
                    SendMessageBatchRequestEntry.builder().id("msg1").messageBody(bucketName).build(),
                    SendMessageBatchRequestEntry.builder().id("msg2").messageBody(fileName).delaySeconds(2).build()).build();
            sqsClient.sendMessageBatch(batchRequest);
            System.out.println("[SQS] Message batch successfully sent.");
            Thread.sleep(1000);
        } catch (SqsException e) {
            System.err.println("[SQS] Error encountered: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
