package emse;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.io.*;
import java.util.List;

import static java.lang.Thread.sleep;

public class ClientApp {

    public static void main(String[] args) {
        try {
            String nameBucket = "mybucket95110";
            String filePathString = "data/01-10-2022-store1.csv";
            String nameSendFile = filePathString;
            String queueURl = "";
            String pathForCopyObject = "";

            // Create a bucket for the Web-Queue-Worker architecture
            S3ControllerCreate.main(new String[]{nameBucket});

            sleep(4000); // We add some delay in order to do not have any error because of the time it takes to create the bucket


            // Check if the csv file exist in the local hard-disk

            System.out.println("\n" + "Checking if the csv file exist in the local hard-disk");

            File file = new File(filePathString);

            file.isFile();
            System.out.println("\n" + "csv file exist");


            sleep(2000); // We add some delay in order to do not have any error because of the time it takes to check if the csv file exist

            //Write the file into a bucket in the Amazon S3

            System.out.println("\n" + "Writing the file into a bucket in the Amazon S3");

            S3ControllerPutObject.main(new String[]{nameBucket, nameSendFile, filePathString});

            //Send a message to the Inbox queue with the bucket and file names

            System.out.println("\n" + "Sending a message to the Inbox queue with the bucket and file names");

            SqsClient sqsClient = SqsClient.builder().build();

            SQSSendMessage.sendMessages(sqsClient, "INBOX", nameBucket,nameSendFile);

            sleep(2000); // We add some delay in order to do not have any error because of the time it takes to send a message to the Inbox queue with the bucket and file names


            boolean running = true;
            Long lastRecordedTime=System.currentTimeMillis();
            while (running) {

                //we check if we receive a message every minute
                Long timer=System.currentTimeMillis();
                if (timer-lastRecordedTime>60000){
                    System.out.println("\n" + "Checking if we receive message");

                    // get the message content
                    List<Message> messages = SQSRetrieveMessage.retrieveMessages(sqsClient,queueURl + "OUTBOX","INBOX");
                    lastRecordedTime=timer;
                    if(messages.size()>1){
                        System.out.println(" messages receive");
                        System.out.println(messages);

                        System.out.println("Number of message(s) :"+messages.size());
                        //extracting the bucket name
                        nameBucket =messages.get(0).body();
                        System.out.println("Bucket name : "+ nameBucket);
                        //extracting the file name
                        String nameReceiveFile = messages.get(1).body();
                        System.out.println("File name : "+nameReceiveFile);

                        // Delete the messages
                        SQSDeleteMessageClient.deleteMessages(sqsClient, queueURl + "OUTBOX",messages);

                        // Uploading the file that contain value calculated by the EC2 Worker
                        S3ControllerGetObject.main( new String[]{ nameBucket, nameReceiveFile, pathForCopyObject} ) ;
                        System.out.println("\n" + "The Client App program is finished ");
                    }

                    else{
                        System.out.println("no messages receive, we are going to do another checking in one minute");
                    }

                }

            }


        } catch (InterruptedException ex) {

            ex.printStackTrace();
            System.exit(1);

        }
    }
}

