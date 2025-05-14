package uz.rivoj.education;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class EducationApplication {
    private static final Logger log = LoggerFactory.getLogger(EducationApplication.class);

    public static void main(String[] args) {
        try {
            log.info("Initializing Firebase application");
            InputStream serviceAccount = new ClassPathResource("fireBaseKeySDK.json").getInputStream();

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("Firebase application initialized successfully");

        } catch (IOException e) {
            log.error("Error loading Firebase service account key: {}", e.getMessage(), e);
            System.exit(1);
        }

        log.info("Starting Education Application");
        SpringApplication.run(EducationApplication.class, args);
    }
}
