package uz.rivoj.education.service.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
import uz.rivoj.education.dto.request.ChatCR;
import uz.rivoj.education.dto.response.UserDetailsDTO;

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


}
