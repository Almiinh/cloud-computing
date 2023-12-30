package sale;

import app.App;
import s3.S3DownloadObject;
import s3.S3UploadObject;
import software.amazon.awssdk.services.sqs.model.Message;
import sqs.*;

import java.util.List;

public class WorkerApp {

    public static final String INBOX = App.INBOX;
    public static final String OUTBOX = App.OUTBOX;
    public static final String BUCKETNAME = App.S3_BUCKET_NAME;
    public static final String S3PATH_STORE_SUMMARY = "summary/summaryByStore.csv";
    public static final String S3PATH_PRODUCT_SUMMARY = "summary/summaryByProduct.csv";
    public static final String LOCALPATHFOLDER = "data/worker/";
    public static final String LOCALPATH_STORE_SUMMARY = "data/worker/summaryByStore.csv";
    public static final String LOCALPATH_PRODUCT_SUMMARY = "data/worker/summaryByProduct.csv";

    public static void writeSummary(String salesPath) {
        // Parse sales.csv
        SaleSummary summary = SaleSummary.parseSales(salesPath);

        // Update stats by store
        SaleSummary.writeSummaryByStore(summary, LOCALPATH_STORE_SUMMARY);

        // Update stats by products
        SaleSummary.writeSummaryByProduct(summary, LOCALPATH_PRODUCT_SUMMARY);
    }

    public static void run() throws InterruptedException {

        // Create queue if they do not exist
        if (!SQSCheckQueue.exists(INBOX)) SQSCreateQueue.createQueue(INBOX);
        System.out.println("[Worker] INBOX queue has been set up.");
        if (!SQSCheckQueue.exists(OUTBOX)) SQSCreateQueue.createQueue(OUTBOX);
        System.out.println("[Worker] OUTBOX queue has been set up.");

        // Loop existing messages every minutes
        List<Message> messages;
        System.out.println("[Worker] Checking queue for messages");
        while (!(messages = SQSReceiveMessage.receiveMessages(INBOX)).isEmpty()) {

            for (Message message : messages) {
                String bucketName = message.body().split(":")[0];
                String filePath = message.body().split(":")[1];
                String fileName = message.body().split("/")[1];
                S3DownloadObject.downloadObject(bucketName, filePath, LOCALPATHFOLDER + fileName);
            }
            SQSDeleteMessage.deleteMessages(INBOX, messages);
        }

        writeSummary(LOCALPATHFOLDER);

        S3UploadObject.uploadObject(BUCKETNAME, S3PATH_STORE_SUMMARY, LOCALPATH_STORE_SUMMARY, true);
        S3UploadObject.uploadObject(BUCKETNAME, S3PATH_PRODUCT_SUMMARY, LOCALPATH_PRODUCT_SUMMARY, true);
        System.out.println("[Worker] Notifying OUTBOX queue");

        SQSSendMessage.sendMessages(OUTBOX, BUCKETNAME);
        SQSSendMessage.sendMessages(OUTBOX, BUCKETNAME);
    }
}
