
package emse;
import com.opencsv.exceptions.CsvException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.io.IOException;
import java.util.List;


public class EC2Worker {

    public static String createQueue(SqsClient sqsClient, String queueName) {

        try {
            System.out.println("\nInitiating Queue Creation...");

            CreateQueueRequest requestForQueueCreation = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();

            sqsClient.createQueue(requestForQueueCreation);

            System.out.println("\nRetrieving Queue URL...");

            GetQueueUrlResponse responseForQueueUrl =
                    sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
            String queueUrl = responseForQueueUrl.queueUrl();
            return queueUrl;

        } catch (SqsException e) {
            System.err.println("Error during queue creation: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public static List<Message> receiveMessages(SqsClient sqsClient, String queueUrl) {

        System.out.println("\nFetching messages from queue...");

        try {
            ReceiveMessageRequest requestForReceivingMessages = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .build();
            List<Message> fetchedMessages = sqsClient.receiveMessage(requestForReceivingMessages).messages();
            return fetchedMessages;
        } catch (SqsException e) {
            System.err.println("Error in message retrieval: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public static void deleteMessages(SqsClient sqsClient, String queueUrl, List<Message> messages) {

        System.out.println("\nCommencing deletion of messages...");

        try {
            for (Message message : messages) {
                DeleteMessageRequest requestForDeletion = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();
                sqsClient.deleteMessage(requestForDeletion);

                System.out.println("\nMessage deletion complete.");
            }

        } catch (SqsException e) {
            System.err.println("Error during message deletion: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void main(String args[]) {
        SqsClient sqsClient = SqsClient.builder().build();
        String inboxQueueUrl = createQueue(sqsClient, "INBOX");
        System.out.println("INBOX queue has been set up.");
        String outboxQueueUrl = createQueue(sqsClient, "OUTBOX");
        System.out.println("OUTBOX queue has been set up.");

        Long lastCheckedTime = System.currentTimeMillis();

        while (true) {
            Long currentTime = System.currentTimeMillis();
            if (currentTime - lastCheckedTime > 60000) {
                List<Message> messages = receiveMessages(sqsClient, inboxQueueUrl);

                lastCheckedTime = currentTime;
                if (messages.size() == 2) {
                    System.out.println("Received a message");
                    try {
                        String bucketName = messages.get(0).body();
                        System.out.println("Bucket: " + bucketName);

                        String fileName = messages.get(1).body();
                        System.out.println("File: " + fileName);

                        S3ControllerGetObject.main(new String[]{bucketName, fileName, "ec2sales.csv"});
                        
                        // Uncomment the following lines as per your project needs.
                        // CSVParser.main(new String[]{"ec2sales.csv"});

                        deleteMessages(sqsClient, inboxQueueUrl, messages);
                        System.out.println("\nUploading processed data to S3");

                        S3ControllerPutObject.main(new String[]{bucketName, "data.txt", "data.txt"});
                        System.out.println("\nNotifying OUTBOX queue");
                        SQSSendMessage.sendMessages(sqsClient, outboxQueueUrl, bucketName, "data.txt");

                    } catch (SqsException e) {
                        System.err.println("Error processing message: " + e.awsErrorDetails().errorMessage());
                        System.exit(1);
                    }
                }
            }
        }
    }
}
