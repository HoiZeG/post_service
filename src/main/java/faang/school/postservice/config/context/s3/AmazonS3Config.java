package faang.school.postservice.config.context.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secretKey}")
    private String secretKey;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Value("${s3.endpoint}")
    private String endpoint;

    @Bean(name = "clientAmazonS3")
    public AmazonS3 amazonS3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        if (!amazonS3Client.doesBucketExistV2(bucketName)) {
            amazonS3Client.createBucket(bucketName);
        }
        return amazonS3Client;
    }
}