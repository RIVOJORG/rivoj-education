package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.rivoj.education.dto.request.CommentCR;
import uz.rivoj.education.dto.response.*;
import uz.rivoj.education.service.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final CommentService commentService;
    private final UserService userService;


    @PostMapping("/create-comment")
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody CommentCR createRequest,
            Principal principal
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(createRequest, UUID.fromString(principal.getName())));
    }

    @GetMapping("/get-user-details")
    public ResponseEntity<?> getUserDetails(Principal principal){
        return ResponseEntity.ok(userService.getUserDetails(UUID.fromString(principal.getName())));
    }


    @GetMapping("/get-comments")
    public  ResponseEntity<Map<String, Object>>  getCommentsOfLesson(
            @RequestParam UUID lessonId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize){
        return ResponseEntity.ok(commentService.getCommentsOfLesson(lessonId,pageNumber,pageSize));
    }

    @PutMapping("/edit-comment")
    public void editComment(Principal principal,@RequestParam UUID commentId, @RequestParam String text){
        commentService.editComment(UUID.fromString(principal.getName()), commentId,text);
    }
    @DeleteMapping("/delete-comment")
    public void editComment(Principal principal,@RequestParam UUID commentId){
        commentService.deleteComment(UUID.fromString(principal.getName()), commentId);
    }

    @PutMapping(value = "/update-profile-picture",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<String> updateProfilePicture(
            Principal principal,
            @RequestParam("picture") MultipartFile picture
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.updateProfilePicture(picture, UUID.fromString(principal.getName())));
    }

}
