package uz.rivoj.education;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class EducationApplication {
    public static void main(String[] args) throws IOException {
        // Firebase konfiguratsiyasi faylini resources papkasidan olish
        String firebaseConfigPath = "fireBaseKeySDK.json"; 

        try (InputStream serviceAccountStream = EducationApplication.class.getClassLoader().getResourceAsStream(firebaseConfigPath)) {
            if (serviceAccountStream == null) {
                throw new IOException("Firebase service account key not found in resources directory: " + firebaseConfigPath);
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new IOException("Error initializing Firebase: " + e.getMessage(), e);
        }

        SpringApplication.run(EducationApplication.class, args);
    }
}
