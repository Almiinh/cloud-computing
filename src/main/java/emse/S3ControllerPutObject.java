package emse;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class S3ControllerPutObject {

    public static void main(String[] args) {

        System.out.println("\nInitiating the process of uploading an object to the S3 bucket");

        final String INSTRUCTIONS = "\n" +
                "Instructions:\n" +
                "  <s3BucketName> <objectKey> <filePath> \n\n" +
                "Where:\n" +
                "  s3BucketName - the name of the S3 bucket where the object will be uploaded.\n" +
                "  objectKey - the name of the object being uploaded (e.g., example.pdf).\n" +
                "  filePath - the local path to the file to be uploaded (e.g., C:/Documents/example.pdf).\n";

        if (args.length != 3) {
            System.out.println(INSTRUCTIONS);
            System.exit(1);
        }

        String bucketName = args[0];
        String objectKey = args[1];
        String filePath = args[2];
        System.out.printf("Uploading object '%s' to bucket '%s'.\n", objectKey, bucketName);

        S3Client s3Client = S3Client.builder().build();

        String uploadResult = uploadObjectToS3(s3Client, bucketName, objectKey, filePath);
        System.out.println("Upload result - ETag: " + uploadResult);
        s3Client.close();

        System.out.println("Upload process completed.");
    }

    /**
     * Uploads an object to an Amazon S3 bucket.
     *
     * @param s3Client The S3 client to use for the upload.
     * @param bucketName The name of the S3 bucket.
     * @param objectKey The key for the object to upload.
     * @param filePath The path of the file to upload.
     * @return The ETag of the uploaded object or an empty string if the upload fails.
     */
    public static String uploadObjectToS3(S3Client s3Client, String bucketName, String objectKey, String filePath) {

        try {
            Map<String, String> objectMetadata = new HashMap<>();
            objectMetadata.put("csv-file-lab", "lab-info");

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .metadata(objectMetadata)
                    .build();

            PutObjectResponse putResponse = s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(readFileAsBytes(filePath)));

            return putResponse.eTag();

        } catch (S3Exception e) {
            System.err.println("Error during object upload: " + e.getMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Reads a file from a given path and returns it as a byte array.
     *
     * @param filePath The path of the file to read.
     * @return A byte array of the file's contents or null if an error occurs.
     */
    private static byte[] readFileAsBytes(String filePath) {

        byte[] fileData = null;

        try (FileInputStream fileInputStream = new FileInputStream(new File(filePath))) {
            fileData = new byte[fileInputStream.available()];
            fileInputStream.read(fileData);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
        return fileData;
    }
}
