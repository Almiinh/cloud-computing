package app;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import s3.S3DownloadObject;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import sqs.SQSDeleteMessage;
import sqs.SQSReceiveMessage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConsolidatorApp {

    public static final String BUCKET_NAME = App.S3_BUCKET_NAME;
    public static final String OUTBOX = "OUTBOX";
    public static final String S3PATH_STORE_SUMMARY = "summary/summaryByStore.csv";
    public static final String S3PATH_PRODUCT_SUMMARY = "summary/summaryByProduct.csv";
    public static final String LOCALPATH_STORE_SUMMARY = "data/consolidator/statsByStore.csv";
    public static final String LOCALTPATH_PRODUCT_SUMMARY = "data/consolidator/statsByProduct.csv";
    public static final String CONSOLIDATOR_ANALYSIS = "data/consolidator/analysisResults.txt";

    public static void run() throws InterruptedException {
        // Loop to check incoming messages every 10 seconds
        System.out.println("[Consolidator] Checking queue for messages every 10 seconds:");
        SqsClient sqsClient = SqsClient.create();
        while (true) {
            System.out.println("[Consolidator] Checking queue for messages");
            List<Message> messages = SQSReceiveMessage.receiveMessages(OUTBOX);
            if (messages.size() >= 2) {
                SQSDeleteMessage.deleteMessages(OUTBOX, messages);
                break;
            }
            Thread.sleep(10000);
        }
        S3DownloadObject.downloadObject(BUCKET_NAME, S3PATH_STORE_SUMMARY, LOCALPATH_STORE_SUMMARY);
        S3DownloadObject.downloadObject(BUCKET_NAME, S3PATH_PRODUCT_SUMMARY, LOCALTPATH_PRODUCT_SUMMARY);
        analyze(LOCALPATH_STORE_SUMMARY, CONSOLIDATOR_ANALYSIS);
    }

    /**
     * Analyzes statistics to extract
     * @param csvFile
     */
    public static void analyze(String csvFile, String outputFile) {
        System.out.println("[Consolidator] Analysing CSV data for minimum, maximum, and sum calculations");

        float maxProfit = 0;
        float minProfit = Float.MAX_VALUE;
        float totalProfit = 0;
        String mostProfitableStore = null, leastProfitableStore = null;

        // Reads CSV
        try (Reader in = new FileReader(csvFile)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setDelimiter(';')
                    .setHeader("Store", "Total Profit")
                    .setSkipHeaderRecord(true)
                    .build();
            Iterable<CSVRecord> records = csvFormat.parse(in);


            //It reads the summary results from the files of that date and computes: the total retailer’s profit,the most and least
            //profitable stores, and the total quantity, total sold, and total profit per product.
            // Process CSV
            for (CSVRecord record : records) {
                String store = record.get("Store");
                String profitRecord = record.get("Total Profit");
                // Remove '$' from "17535.38$"
                float profit = Float.parseFloat(profitRecord.substring(0, profitRecord.length() - 1));

                totalProfit += profit;
                if (profit > maxProfit) {
                    maxProfit = profit;
                    mostProfitableStore = store;
                }
                if (profit < minProfit) {
                    minProfit = profit;
                    leastProfitableStore = store;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Writing analysis results
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("Data Analysis Results\n");
            writer.write("Total Retailer's Profit: " + totalProfit+"\n");
            writer.write("Most Profitable Store: " + mostProfitableStore + "\n");
            writer.write("Least Profitable Store: " + leastProfitableStore + "\n");
            System.out.println("Data analysis results successfully written into " + Path.of(outputFile).toUri());

        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file " + Path.of(outputFile).toUri());
            e.printStackTrace();
        }

        try {
            List<String> lines = Files.readAllLines(Path.of(outputFile));
            for (String fileLine : lines)
                System.out.println(fileLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
