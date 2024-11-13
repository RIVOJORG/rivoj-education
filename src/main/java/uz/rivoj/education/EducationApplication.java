package uz.rivoj.education;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.FileNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class EducationApplication {
	public static void main(String[] args) throws IOException {
        try (FileInputStream serviceAccount = new FileInputStream("/app/fireBaseKeySDK.json")) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException e) {
            System.err.println("fireBaseKeySDK.json fayli topilmadi. Iltimos, fayl joylashuvi va nomini tekshiring.");
            e.printStackTrace();
            return;
        }
        
        SpringApplication.run(EducationApplication.class, args);
    }

}
