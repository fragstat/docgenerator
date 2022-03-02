package ru.ferrotrade.docgenerator.service.awsS3service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface AwsS3Service {

    void uploadFile(File f);

    void uploadZeroCertificate(File f);

    byte[] downloadFile(String id);

    byte[] downloadZeroCertificate(String part);

    boolean containsFile(String qr);

    boolean containsZeroCertificate(String part);
}
