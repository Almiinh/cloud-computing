package s3;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


public class S3UploadObject {

    public static void uploadObject(String bucketName, String objectKey, String filePath) {
        try (S3Client s3Client = S3Client.builder().build()) {
            ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(bucketName).build();
            List<S3Object> objects = s3Client.listObjects(listObjects).contents();

            // If file does not already exist
            System.out.println("[S3] Uploading object '" + objectKey + "' to bucket '" + bucketName + "'...");
            if (objects.stream().noneMatch((S3Object object) -> object.key().equals(objectKey))) {
                String uploadResult = uploadObjectToS3(bucketName, objectKey, filePath);
                System.out.println("[S3] Upload result - ETag: " + uploadResult);
                System.out.println("[S3] Upload completed");
            } else
                System.out.println("[S3] File already exists");
        }
    }

    /**
     * Uploads an object to an Amazon S3 bucket.
     *
     * @param bucketName The name of the S3 bucket.
     * @param objectKey  The key for the object to upload.
     * @param filePath   The path of the file to upload.
     * @return The ETag of the uploaded object or an empty string if the upload fails.
     */
    private static String uploadObjectToS3(String bucketName, String objectKey, String filePath) {
        try (S3Client s3Client = S3Client.builder().build()) {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            PutObjectResponse putResponse = s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(readFileAsBytes(filePath)));

            return putResponse.eTag();

        } catch (S3Exception e) {
            System.err.println("[S3] Error during object upload: " + e.getMessage());
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

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            fileData = new byte[fileInputStream.available()];
            fileInputStream.read(fileData);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
        return fileData;
    }
}
