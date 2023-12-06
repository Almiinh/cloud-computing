package emse;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class S3ControllerGetObject {


    /**
     * Downloads an object from S3 and writes it to a specified file path.
     *
     * @param s3Client The S3 client used for the operation.
     * @param bucketName The name of the S3 bucket.
     * @param objectKey The key of the object in the bucket.
     * @param outputPath The path to write the object to.
     */
    public static void downloadObject(S3Client s3Client, String bucketName, String objectKey, String outputPath) {

        System.out.println("Retrieving the object from Amazon S3 and saving to the local disk...");

        try {
            GetObjectRequest request = GetObjectRequest.builder().key(objectKey).bucket(bucketName).build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(request);
            byte[] fileData = objectBytes.asByteArray();

            File outputFile = new File(outputPath);
            try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(fileData);
                System.out.println("File download completed.");
            }

        } catch (IOException ex) {
            System.err.println("Error writing file: " + ex.getMessage());
            ex.printStackTrace();
        } catch (S3Exception e) {
            System.err.println("S3 error: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {

        final String INSTRUCTIONS = "\n" +
                "Instructions:\n" +
                "    <s3BucketName> <objectKey> <outputFilePath>\n\n" +
                "Where:\n" +
                "    s3BucketName - the name of the S3 bucket. \n" +
                "    objectKey - the object key in S3. \n" +
                "    outputFilePath - the local file path to save the object. \n";

        if (args.length != 3) {
            System.out.println(INSTRUCTIONS);
            System.exit(1);
        }

        String bucketName = args[0];
        String objectKey = args[1];
        String localFilePath = args[2];

        S3Client s3Client = S3Client.builder().build();

        downloadObject(s3Client, bucketName, objectKey, localFilePath);
        s3Client.close();
    }
}
