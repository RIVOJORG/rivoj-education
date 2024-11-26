package uz.rivoj.education.service.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.OAuth2Credentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.ChatCR;
import uz.rivoj.education.dto.request.NotificationCR;
import uz.rivoj.education.dto.request.NotificationDto;
import uz.rivoj.education.dto.response.UserDetailsDTO;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Service
public class FirebaseService {
    public void createUser(UserDetailsDTO user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection("UserTable").document(user.getUserId()).set(user);
    }

    public void deleteUser(String userId) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection("UserTable").document(userId).delete();
    }

    public void updateUser(UserDetailsDTO user) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection("UserTable").document(user.getUserId()).set(user);
    }

    public void createChat(ChatCR chatMembers, String chatId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection("ChatRoom").document(chatId).set(chatMembers);

    }
    public void updateChat(ChatCR chatMembers,String chatId){
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection("ChatRoom").document(chatId).set(chatMembers);
    }

    public void deleteChat(String chatId){
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection("ChatRoom").document(chatId).delete();
    }

    public ResponseEntity<String> sendNotification(NotificationDto notificationDto) {
        try {
            for (String destination : notificationDto.getDestinations()) {
                JsonObject payload = new JsonObject();

                JsonObject notification = new JsonObject();
                notification.addProperty("title", notificationDto.getTitle());
                notification.addProperty("body", notificationDto.getBody());

                JsonObject data = new JsonObject();
                if (notificationDto.getData() != null) {
                    for (Map.Entry<String, String> entry : notificationDto.getData().entrySet()) {
                        data.addProperty(entry.getKey(), entry.getValue());
                    }
                }

                JsonObject messageObject = new JsonObject();
                messageObject.add("notification", notification);
                messageObject.add("data", data);

                if (notificationDto.isTopic()) {
                    messageObject.addProperty("topic", destination);
                } else {
                    messageObject.addProperty("token", destination);
                }

                payload.add("message", messageObject);
                sendFCMRequest(payload, getAccessToken());
            }

            return ResponseEntity.ok(notificationDto.isTopic() ? "Topicga muvaffaqiyatli yuborildi." : "Xabar muvaffaqiyatli yuborildi.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Xabarni yuborishda xatolik: " + e.getMessage());
        }
    }


    private void sendFCMRequest(JsonObject payload, String accessToken) throws Exception {
        URL url = new URL("https://fcm.googleapis.com/v1/projects/rivoj-edu/messages:send");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println("FCM Response: " + response.toString());
        }
    }


    public String getAccessToken() throws IOException {
        String serviceAccountFilePath = "src/main/resources/fireBaseKeySDK.json";
        FileInputStream serviceAccountStream = new FileInputStream(serviceAccountFilePath);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                .createScoped(Arrays.asList(
                        "https://www.googleapis.com/auth/userinfo.email",
                        "https://www.googleapis.com/auth/firebase.database",
                        "https://www.googleapis.com/auth/firebase.messaging"
                ));

        if (credentials instanceof OAuth2Credentials) {
            OAuth2Credentials oAuth2Credentials = (OAuth2Credentials) credentials;
            return oAuth2Credentials.refreshAccessToken().getTokenValue();
        } else {
            throw new IOException("Unable to get credentials from the service account.");
        }
    }
}
