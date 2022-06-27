package com.prgrms.amabnb.infra.s3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AWSS3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public List<String> upload(MultipartFile[] images, String dirName) throws IOException {
        List<String> s3urlPathList = new ArrayList<>();

        int fileSequence = 1;
        for (MultipartFile file : images) {
            String fileName = dirName + createS3FileName(fileSequence);
            ObjectMetadata objectMetadata = getObjectMetadata(file);

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(
                    new PutObjectRequest(
                        bucket,
                        fileName,
                        inputStream,
                        objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead)
                );

                s3urlPathList.add(amazonS3Client.getUrl(bucket, fileName).toString());
            } catch (IOException e) {
                throw new IOException("image upload to s3 IOException: ", e);
            }

            fileSequence++;
        }

        return s3urlPathList;
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }

    public String createS3FileName(int fileSequence) {
        return File.separator + UUID.randomUUID() + "-" + fileSequence;
    }
}
