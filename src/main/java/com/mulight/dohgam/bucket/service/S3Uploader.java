package com.mulight.dohgam.bucket.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Uploader {

	private final AmazonS3 amazonS3;
	private final String bucket;
	
	public S3Uploader(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucket) {
	
		this.amazonS3 = amazonS3;
		this.bucket = bucket;
		
	}
	
	public String upload(MultipartFile multipartFile, String dirName) throws IOException {
		
		// 파일 이름에서 공백을 제거한 새로운 파일 이름 생성
		String originalFileName = multipartFile.getOriginalFilename();
		
		// UUID를 파일명에 추가
		String uuid = UUID.randomUUID().toString();
		String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");
		
		String fileName = dirName + "/" + uniqueFileName;
		File uploadFile = convert(multipartFile);
		
		String uploadImageUrl = putS3(uploadFile, fileName);
		removeNewFile(uploadFile);
		return uploadImageUrl;
		
	}

	// 로컬에 파일 업로드 하기
	// 업로드할 때 파일이 로컬에 없으면 에러 발생
	private File convert(MultipartFile file) throws IOException {
		String originalFileName = file.getOriginalFilename();
		String uuid = UUID.randomUUID().toString();
		String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");
		
		File convertFile = new File(uniqueFileName);
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			} catch (IOException e) {
				throw e;
			}
			return convertFile;
		}
		
		throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", originalFileName));
	}
	
	// S3 업로드
	private String putS3(File uploadFile, String fileName) {
		amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		return amazonS3.getUrl(bucket, fileName).toString();
	}
	
	// 로컬에 저장된 이미지 지우기
	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			System.out.println("파일 삭제");
		} else {
			System.out.println("파일 삭제 실패");
		}
	}
	
	public void deleteFile(String uploadImageUrl) {
		try {
			// fileName 추출
			String fileName = uploadImageUrl.replaceFirst("https://dohgam.s3.ap-northeast-2.amazonaws.com/", "");
			// URL 디코딩을 통해 원래 파일 이름을 가져옴
			String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
			System.out.println("Deleting file from S3 : " + decodedFileName);
			amazonS3.deleteObject(bucket, decodedFileName);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error while decoding the file name: {}" +  e.getMessage());
		}
	}
	
	public String updateFile(MultipartFile newFile, String oldFileName, String dirName) throws IOException {
		// 기존 파일 삭제
		System.out.println("S3 oldFileName : " + oldFileName);
		deleteFile(oldFileName);
		// 새 파일 업로드
		return upload(newFile, dirName);
	}
}
