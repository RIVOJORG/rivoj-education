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
            ClassLoader classLoader = EducationApplication.class.getClassLoader();
            InputStream serviceAccountStream = classLoader.getResourceAsStream("fireBaseKeySDK.json");

            if (serviceAccountStream == null) {
                    throw new IOException("Firebase service account key not found in classpath");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();
            FirebaseApp.initializeApp(options);
            SpringApplication.run(EducationApplication.class, args);
    }

}
