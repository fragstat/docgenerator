package ru.ferrotrade.docgenerator.service.awsS3service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Service
public class AwsS3ServiceImpl implements AwsS3Service {

    public static final String BUCKET = "ferro-trade-documents";

    private static final AwsCredentials credentials = AwsBasicCredentials.create("AKIATT4WJANPQTYT5M22",
            "KVMJPWIZRoaifcQQ29z0z/l4eTnUlmCLvAyTrf8d");

    private static final S3Client client = S3Client.builder()
            .credentialsProvider(() -> credentials)
            .region(Region.EU_CENTRAL_1).build();

    @Override
    public void uploadFile(File f) {
        String fileName = "certificate/" + f.getName();
        client.putObject(PutObjectRequest.builder().bucket(BUCKET).key(fileName).build(),
                RequestBody.fromFile(f));
    }

    @Override
    public void uploadZeroCertificate(File f) {
        String fileName = "zeroCertificate/" + f.getName();
        client.putObject(PutObjectRequest.builder().bucket(BUCKET).key(fileName).build(),
                RequestBody.fromFile(f));
    }

    @Override
    public byte[] downloadFile(String qr) {
        String certificate = "certificate/" + qr + ".docx";
        try {
            return client.getObject(GetObjectRequest.builder().bucket(BUCKET).key(certificate).build()).readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public byte[] downloadZeroCertificate(String part) {
        String certificate = "zeroCertificate/" + part + ".docx";
        try {
            return client.getObject(GetObjectRequest.builder().bucket(BUCKET).key(certificate).build()).readAllBytes();
        } catch (IOException e ) {
            e.printStackTrace();
        } catch (NoSuchKeyException ignored) {
        }
        return new byte[0];
    }

    @Override
    public boolean containsFile(String qr) {
        return !Arrays.equals(downloadFile(qr), new byte[0]);
    }

    @Override
    public boolean containsZeroCertificate(String part) {
        return !Arrays.equals(downloadZeroCertificate(part), new byte[0]);
    }
}
