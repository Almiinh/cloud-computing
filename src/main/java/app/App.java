package app;

import sale.WorkerApp;

import java.io.File;

public class App {

    public static final String S3_BUCKET_NAME = "thatbucket95110";
    public static final String INBOX = "INBOX";
    public static final String OUTBOX = "OUTBOX";
    public static final String LOCALFOLDER = "data/client/";

    public static void main(String[] args) throws InterruptedException {

        // List all files in the folder
        String[] files =  new File(LOCALFOLDER).list();
        if (files != null)
            for (String fileName : files)
                ClientApp.run(LOCALFOLDER + fileName);

        Thread.sleep(5000);
        WorkerApp.run();
        Thread.sleep(5000);
        ConsolidatorApp.run("01-10-2022");
    }
}
