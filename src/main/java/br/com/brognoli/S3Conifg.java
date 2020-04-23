package br.com.brognoli;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


@Configuration
public class S3Conifg {
	
	@Value("${clientRegion}")
	private String clientRegion;
	@Value("${accesskey}")
	private String accesskey;
	@Value("${secretaccesskey}")
	private String secretaccesskey;
	
	@Bean
	public AmazonS3 s3client() {
		
		
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accesskey,
				secretaccesskey);
		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(clientRegion)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
		return s3client;
	}

}
