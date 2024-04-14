package uz.rivoj.education.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.rivoj.education.dto.request.CommentCreateRequest;
import uz.rivoj.education.dto.response.CommentResponse;
import uz.rivoj.education.entity.CommentEntity;
import uz.rivoj.education.service.CommentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/create-comment")
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentCreateRequest createRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(createRequest));
    }

    @DeleteMapping("/delete-comment{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable UUID commentId){
        return ResponseEntity.status(200).body(commentService.delete(commentId));
    }

    @GetMapping("/get-all")
    public List<CommentResponse> getAll(){
        return commentService.getAll();
    }

    @GetMapping("get-comment/{id}")
    public CommentEntity getCommentById(@PathVariable UUID id) {
        return commentService.getComment(id);
    }

}
