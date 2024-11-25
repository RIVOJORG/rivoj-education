package uz.rivoj.education;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class EducationApplication {
    public static void main(String[] args) throws IOException {
        String firebaseConfigPath = "/app/fireBaseKeySDK.json";

        File file = new File(firebaseConfigPath);
        if (!file.exists()) {
            throw new IOException("Firebase service account key not found at: " + firebaseConfigPath);
        }

        try (InputStream serviceAccountStream = new FileInputStream(file)) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            FirebaseApp.initializeApp(options);
        }

        SpringApplication.run(EducationApplication.class, args);
    }
}
