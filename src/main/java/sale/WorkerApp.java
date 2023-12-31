package sale;

import app.App;
import s3.S3DownloadObject;
import s3.S3UploadObject;
import software.amazon.awssdk.services.sqs.model.Message;
import sqs.*;

import java.io.File;
import java.util.*;

/**
 * The application When receiving an SQS Message in `INBOX` queue, downloads sales files from S3, summarizes sales, and
 * uploads summary.
 *
 * It summarizes the daily sales by store and by product:
 * - By Store : total profit,
 * - By Product: total quantity, total sold, total profit.
 */
public class WorkerApp {

    public static final String INBOX = App.INBOX;
    public static final String OUTBOX = App.OUTBOX;
    public static final String BUCKETNAME = App.S3_BUCKET_NAME;
    public static final String S3PATHFOLDER_SUMMARY = "summary/";
    public static final String LOCALFOLDER_SALES = "data/worker/sales/";
    public static final String LOCALFOLDER_SUMMARY = "data/worker/summary/";
    public static final String LOCAL_STORE_SUMMARY = "summaryByStore.csv";
    public static final String LOCAL_PRODUCT_SUMMARY = "summaryByProduct.csv";

    public static void updateSummary(String file) {
        // Parse dates from file names '01-10-2022-store1.csv' => '01-10-2022'
        String date = file.substring(0, 10);

        // Get summary by date
        SaleSummary summary = SaleSummary.createOrGetSummary(date);
        // Parse sales
        SaleSummary.parseSales(summary, LOCALFOLDER_SALES+file);
        // Update summary by store
        SaleSummary.updateSummaryByStore(summary, LOCALFOLDER_SUMMARY + date + '-' + LOCAL_STORE_SUMMARY);
        // Update summary by products
        SaleSummary.updateSummaryByProduct(summary, LOCALFOLDER_SUMMARY + date + '-' + LOCAL_PRODUCT_SUMMARY);
    }

    public static void run() {

        // Create queue if they do not exist
        if (!SQSCheckQueue.exists(INBOX)) SQSCreateQueue.createQueue(INBOX);
        System.out.println("[Worker] INBOX queue has been set up.");
        if (!SQSCheckQueue.exists(OUTBOX)) SQSCreateQueue.createQueue(OUTBOX);
        System.out.println("[Worker] OUTBOX queue has been set up.");

        // Loop existing messages
        List<Message> messages;
        System.out.println("[Worker] Checking queue for messages");
        while (!(messages = SQSReceiveMessage.receiveMessages(INBOX)).isEmpty()) {

            // For each message, extract filename and download the file
            for (Message message : messages) {
                String bucketName = message.body().split(":")[0];
                String filePath = message.body().split(":")[1];
                String fileName = message.body().split("/")[1];
                S3DownloadObject.downloadObject(bucketName, filePath, LOCALFOLDER_SALES + fileName);
                // Update summary
                updateSummary(fileName);
            }
            SQSDeleteMessage.deleteMessages(INBOX, messages);
        }

        // Notifying OUTBOX queue
        System.out.println("[Worker] Notifying OUTBOX queue");
        String[] files = new File(LOCALFOLDER_SUMMARY).list();
        if (files != null) {
            for (String file : files) {
                S3UploadObject.uploadObject(BUCKETNAME, LOCALFOLDER_SUMMARY+file, S3PATHFOLDER_SUMMARY+file, true);
                SQSSendMessage.sendMessages(OUTBOX, BUCKETNAME+":"+S3PATHFOLDER_SUMMARY+file);
            }
        }
    }
}
