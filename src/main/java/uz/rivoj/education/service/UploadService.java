package uz.rivoj.education.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.entity.LessonEntity;
import uz.rivoj.education.exception.DataNotFoundException;
import uz.rivoj.education.repository.LessonRepository;

import java.io.IOException;
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
    public String uploadFile(MultipartFile file,UUID lessonId) throws IOException {
        String  uniquePath = absFilePath + UUID.randomUUID() + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getInputStream().available());
        if (file.getContentType() != null && !"".equals(file.getContentType())) {
            metadata.setContentType(file.getContentType());
        }
        s3Client.putObject(new PutObjectRequest(doSpaceBucket, uniquePath, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        LessonEntity byId = lessonRepository.findById(lessonId).orElseThrow(
                () -> new DataNotFoundException("Lesson not found with this id:" + lessonId)
        );
        return fileLink+uniquePath;
    }
}
