package uz.rivoj.education.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.LessonRepository;

import javax.print.DocFlavor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final AmazonS3 s3Client;
    private final LessonRepository lessonRepository;
    @Value("${do.spaces.endpoint2}")
    private String fileLink;

    @Value("${do.spaces.bucket}")
    private String doSpaceBucket;

    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        String absFilePath = "meta-data/";
        String contentType = Objects.requireNonNull(file.getContentType()).split("/")[1];
        System.out.println(contentType);
        String uniquePath = absFilePath + UUID.randomUUID() + "_" + fileName + "." + contentType;

        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(doSpaceBucket, uniquePath);
        InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

        List<PartETag> partETags = new ArrayList<>();
        long contentLength = file.getSize();
        long partSize = 100 * 1024 * 1024; // Set part size to 100 MB

        try {
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                partSize = Math.min(partSize, (contentLength - filePosition));
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(doSpaceBucket)
                        .withKey(uniquePath)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withInputStream(file.getInputStream())
                        .withPartSize(partSize);

                UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
                partETags.add(uploadResult.getPartETag());
                filePosition += partSize;
            }
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
                    doSpaceBucket, uniquePath, initResponse.getUploadId(), partETags);

            s3Client.completeMultipartUpload(compRequest);
            s3Client.setObjectAcl(doSpaceBucket, uniquePath, CannedAccessControlList.PublicRead);

            return fileLink + "/" + uniquePath;
        } catch (Exception e) {
            s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    doSpaceBucket, uniquePath, initResponse.getUploadId()));
            throw new IOException("Failed to upload file", e);
        }
    }

}