package uz.rivoj.education;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@SpringBootApplication
public class EducationApplication {
	public static void main(String[] args) throws IOException {
        ClassLoader classLoader = EducationApplication.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("fireBaseKeySDK.json").getFile()));
        FileInputStream serviceAccount = new FileInputStream(file.getAbsolutePath());
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        FirebaseApp.initializeApp(options);
        SpringApplication.run(EducationApplication.class, args);
    }

}
