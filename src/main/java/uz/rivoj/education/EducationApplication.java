package uz.rivoj.education;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class EducationApplication {
    public static void main(String[] args) throws IOException {
        String firebaseConfigPath = "/app/fireBaseKeySDK.json"; 
        
        try (InputStream serviceAccountStream = new FileInputStream(firebaseConfigPath)) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new IOException("Firebase service account key not found at: " + firebaseConfigPath, e);
        }

        SpringApplication.run(EducationApplication.class, args);
    }
}
