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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final AmazonS3 s3Client;
    private final LessonRepository lessonRepository;
    private final String fileLink = "https://rivojmediabucket.blr1.digitaloceanspaces.com/";
    private final String absFilePath = "meta-data/";

    @Value("${do.spaces.bucket}")
    private String doSpaceBucket;

    public String uploadFile(MultipartFile file, UUID lessonId) throws IOException {
        String uniquePath = absFilePath + UUID.randomUUID() + file.getOriginalFilename();

        // Initiate multipart upload
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(doSpaceBucket, uniquePath);
        InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

        List<PartETag> partETags = new ArrayList<>();
        long contentLength = file.getSize();
        long partSize = 100 * 1024 * 1024; // Set part size to 100 MB

        try {
            // Upload parts
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

            // Complete multipart upload
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
                    doSpaceBucket, uniquePath, initResponse.getUploadId(), partETags);

            s3Client.completeMultipartUpload(compRequest);

            // Set the object to public
            s3Client.setObjectAcl(doSpaceBucket, uniquePath, CannedAccessControlList.PublicRead);

            // Save file link to database
            LessonEntity lesson = lessonRepository.findById(lessonId).orElseThrow(
                    () -> new DataNotFoundException("Lesson not found with this id:" + lessonId)
            );
            lesson.setSource(fileLink + uniquePath);
            lessonRepository.save(lesson);

            return fileLink + uniquePath;
        } catch (Exception e) {
            s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    doSpaceBucket, uniquePath, initResponse.getUploadId()));
            throw new IOException("Failed to upload file", e);
        }
    }
}