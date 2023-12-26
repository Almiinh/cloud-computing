package sale;

import s3.S3DownloadObject;
import s3.S3UploadObject;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SqsException;
import sqs.*;

import java.util.List;

import static java.lang.Thread.sleep;

public class WorkerApp {

    public static final String INBOX = "INBOX";
    public static final String OUTBOX = "OUTBOX";
    public static final String S3PATH_STORE_SUMMARY = "summary/summaryByStore.csv";
    public static final String S3PATH_PRODUCT_SUMMARY = "summary/summaryByProduct.csv";
    public static final String LOCALPATH_STORE_SUMMARY = "data/worker/summaryByStore.csv";
    public static final String LOCALPATH_PRODUCT_SUMMARY = "data/worker/summaryByProduct.csv";
    public static final String LOCALPATH_SAlES = "data/worker/sales.csv";

    public static void run() throws InterruptedException {
        if (!SQSCheckQueue.exists(INBOX)) SQSCreateQueue.createQueue(INBOX);
        System.out.println("[Worker] INBOX queue has been set up.");
        if (!SQSCheckQueue.exists(OUTBOX)) SQSCreateQueue.createQueue(OUTBOX);
        System.out.println("[Worker] OUTBOX queue has been set up.");

        // Loop to exists messages every minutes
        while (true) {
            System.out.println("[Worker] Checking queue for messages");
            List<Message> messages = SQSReceiveMessage.receiveMessages(INBOX);

            if (messages.size() == 2) {
                System.out.println("[Worker] Received a message");
                try {
                    String bucketName = messages.get(0).body();
                    System.out.println("[Worker][Message] Bucket: " + bucketName);

                    String fileName = messages.get(1).body();
                    System.out.println("[Worker][Message] File: " + fileName);

                    S3DownloadObject.downloadObject(bucketName, fileName, LOCALPATH_SAlES);

                    writeSummary();

                    SQSDeleteMessage.deleteMessages(INBOX, messages);
                    S3UploadObject.uploadObject(bucketName, S3PATH_STORE_SUMMARY, LOCALPATH_STORE_SUMMARY);
                    S3UploadObject.uploadObject(bucketName, S3PATH_PRODUCT_SUMMARY, LOCALPATH_PRODUCT_SUMMARY);
                    System.out.println("[Worker] Notifying OUTBOX queue");

                    SQSSendMessage.sendMessages(OUTBOX, bucketName, LOCALPATH_STORE_SUMMARY);
                    SQSSendMessage.sendMessages(OUTBOX, bucketName, LOCALPATH_PRODUCT_SUMMARY);

                    break;

                } catch (SqsException e) {
                    System.err.println("Error processing message: " + e.awsErrorDetails().errorMessage());
                    System.exit(1);
                }
            }
            sleep(60000);
        }
    }

    public static void writeSummary() {
        // Parse sales.csv
        SaleSummary summary = SaleHandler.parseSales(LOCALPATH_SAlES);

        // Update stats by store
        SaleHandler.writeSummaryByProduct(summary, LOCALPATH_STORE_SUMMARY);

        // Update stats by products
        SaleHandler.writeSummaryByStore(summary, LOCALPATH_PRODUCT_SUMMARY);
    }
}
