package br.com.brognoli.api.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public class S3Service {

	@Autowired
	private AmazonS3 s3Client;
	private Logger LOG = LoggerFactory.getLogger(S3Service.class);

	@Value("${bucketpictureuser}")
	private String bucketpictureuser;
	@Value("${buckettecsegimg}")
	private String buckettecsegimg;
	
	
	

	public URI uploadFilePictureUser(MultipartFile multipartFile, String pasta) {
		try {
			String fileName = multipartFile.getOriginalFilename();
			InputStream is = multipartFile.getInputStream();
			String contentType = multipartFile.getContentType();
			return uploadFilePictureUser(is, fileName, contentType, pasta);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Erro IO: " + e.getMessage());
		}

	}

	public URI uploadFilePictureUser(InputStream is, String fileName, String contentType, String pasta) {
		try {
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentType(contentType);
			LOG.info("Iniciando Upload"); 
			s3Client.putObject(buckettecsegimg + "/" + pasta, fileName, is, meta);
			LOG.info("Upload Finalizado");
			return s3Client.getUrl(buckettecsegimg, fileName).toURI();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Erro converter URI");
		}
	}
	

	public void deleteFilePicture(String fileName, String pasta) {
		try {
			LOG.info("Iniciando Delete");
			s3Client.deleteObject(buckettecsegimg, pasta+"/" + fileName);
			LOG.info("Upload Finalizado");
		} catch (SdkClientException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Erro converter URI");
		}
	}
	
	
	//Salvar Arquivos Diversos
	
	public URI uploadFile(File file) {
		try {
			InputStream is = new FileInputStream(file);
			String contentType = "Content-Type: multipart/form-data; boundary=something";
			return uploadFile(is, file.getName(), contentType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Erro IO: " + e.getMessage());
		}

	}

	public URI uploadFile(InputStream is, String fileName, String contentType) {
		try {
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentType(contentType);
			LOG.info("Iniciando Upload");
			s3Client.putObject(buckettecsegimg, fileName, is, meta);
			LOG.info("Upload Finalizado");
			return s3Client.getUrl(buckettecsegimg, fileName).toURI();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Erro converter URI");
		}
	}
	
	
}
