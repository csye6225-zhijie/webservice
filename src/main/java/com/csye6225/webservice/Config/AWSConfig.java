package com.csye6225.webservice.Config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.context.annotation.Primary;

@Configuration
public class AWSConfig {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3Client amazonS3Client() {

        System.out.println("Starting s3 client");

        return (AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }
}

//@Configuration
//public class AWSConfig {
//    @Value("${cloud.aws.credentials.access-key}")
//    private String awsAccessKey;
//
//    @Value("${cloud.aws.credentials.secret-key}")
//    private String awsSecretKey;
//
//    @Value("${cloud.aws.region.static}")
//    private String region;
//
//    @Primary
//    @Bean
//    public AmazonS3 amazonS3Client() {
//        return AmazonS3ClientBuilder
//                .standard()
//                .withRegion(region)
//                .withCredentials(new AWSStaticCredentialsProvider(
//                        new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
//                .build();
//    }
//}
