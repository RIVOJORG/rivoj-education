package uz.rivoj.education;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class EducationApplication {
	public static void main(String[] args) {
		SpringApplication.run(EducationApplication.class, args);
//		System.out.println(UUID.randomUUID());
	}

}
