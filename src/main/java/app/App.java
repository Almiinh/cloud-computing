package app;

import sale.WorkerApp;
import sqs.SQSEmptyQueue;

import java.io.FileNotFoundException;

public class App {

    public static final String S3_BUCKET_NAME = "thatbucket95110";

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        SQSEmptyQueue.main(args);
        ClientApp.run();
        Thread.sleep(3000);
        WorkerApp.run();
        Thread.sleep(5000);
        ConsolidatorApp.run();
    }
}
