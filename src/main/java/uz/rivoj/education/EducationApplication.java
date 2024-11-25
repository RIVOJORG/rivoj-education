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
        // Firebase xizmatini sozlash uchun resurslardan faylni olish
        InputStream serviceAccountStream = EducationApplication.class.getResourceAsStream("/fireBaseKeySDK.json");

        if (serviceAccountStream == null) {
            throw new IOException("Firebase service account key not found in resources.");
        }

        // Firebase sozlamalarini o'rnatish
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .build();

        // Firebase dasturini ishga tushurish
        FirebaseApp.initializeApp(options);

        // Spring Boot dasturini ishga tushurish
        SpringApplication.run(EducationApplication.class, args);
    }
}
