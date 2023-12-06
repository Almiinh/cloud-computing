package emse;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

public class S3ControllerCreate {

    public static void main(String[] args) {

        final String INSTRUCTIONS = "\n" +
                "Instructions:\n" +
                "    <nameOfBucket> \n\n" +
                "Where:\n" +
                "    nameOfBucket - the name for the new bucket to be created. This name must be unique, otherwise an error will occur.\n\n";

        if (args.length != 1) {
            System.out.println(INSTRUCTIONS);
            System.exit(1);
        }

        String bucketName = args[0];

        System.out.printf("Initiating creation of bucket: %s\n", bucketName);

        S3Client s3 = S3Client.builder().build();

        initiateBucketCreation(s3, bucketName);
        s3.close();

        System.out.println("Bucket creation process completed.");
    }

    /**
     * This method creates a new S3 bucket.
     * 
     * @param s3Client The Amazon S3 client.
     * @param bucketName The name of the bucket to be created.
     * @throws S3Exception If any error occurs during bucket creation.
     */
    public static void initiateBucketCreation(S3Client s3Client, String bucketName) {

        try {
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(createBucketRequest);
            HeadBucketRequest waitRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(waitRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println("Bucket " + bucketName + " is successfully created and ready for use.");

        } catch (S3Exception e) {
            System.err.println("Bucket creation error: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
