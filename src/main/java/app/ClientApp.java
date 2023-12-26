package app;

import s3.S3CheckBucket;
import s3.S3CreateBucket;
import s3.S3UploadObject;
import sqs.SQSCheckQueue;
import sqs.SQSCreateQueue;
import sqs.SQSSendMessage;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The Client Application uploads files to Amazon S3 buckets and sends messages to Amazon SQS queues.
 */
public class ClientApp {

    public static final String BUCKET_NAME = App.S3_BUCKET_NAME;
    public static final String QUEUE_NAME = "INBOX";
    public static final String LOCALPATH = "data/client/01-10-2022-store2.csv";
    public static final String S3PATH = "data/01-10-2022-store2.csv";

    public static void run() throws FileNotFoundException {
        Path path = Paths.get(LOCALPATH);
        if (Files.exists(path)) {
            System.out.println("[Client] Found local file: " + path.toUri());

            // Create bucket or queue if they don't exist
            if (!S3CheckBucket.exists(BUCKET_NAME))
                S3CreateBucket.createBucket(BUCKET_NAME);
            if (!SQSCheckQueue.exists(QUEUE_NAME))
                SQSCreateQueue.createQueue(QUEUE_NAME);

            // Uploads file into S3 bucket
            S3UploadObject.uploadObject(BUCKET_NAME, S3PATH, LOCALPATH);

            // Sends two messages msg1=bucketName, msg2=s3FilePath
            System.out.println("[Client] Notifying INBOX queue");
            SQSSendMessage.sendMessages(QUEUE_NAME, BUCKET_NAME, S3PATH);
        } else {
            throw new FileNotFoundException("[Client] Local file not found: " + path.toUri());
        }
    }
}

