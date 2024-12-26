package uz.rivoj.education;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class EducationApplication {
    public static void main(String[] args) {
        try {
            String firebaseKeyPath = "/app/fireBaseKeySDK.json";
            FileInputStream serviceAccount = new FileInputStream(firebaseKeyPath);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (IOException e) {
            System.err.println("Firebase service account keyni yuklashda xatolik: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        SpringApplication.run(EducationApplication.class, args);
    }
}
