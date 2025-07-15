package com.example.new2.controller;

import com.example.new2.config.RabbitMQConfig;
import com.example.new2.dto.Post;
import com.example.new2.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

// swagger-ui : localhost:4000/swagger-ui.html

@Tag(name="게시글API", description = "게시글 crud api")
//@CrossOrigin(origins = "http://localhost:3000") // 프론트가 실행되는 주소
@RestController
@RequestMapping("/board")
public class PostController {
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private PostService postService;
  
  // 등록
  @PostMapping("/posts")
  @Operation(summary = "게시글 저장", description = "게시글을 저장합니다.")
  public ResponseEntity<?> publishPost(@RequestBody Post post) {
      post.setCreatedAt(Instant.now().toString());
      rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, post);
      return ResponseEntity.ok("게시글이 juni의 마음을 도달한 jm을 위한 큐에 전송되었습니다.");
  }
  
  @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
  public void handlePostMessage(Post post) {
      try {
          postService.savePost(post);
          System.out.println("✅ RabbitMQ로부터 게시글 수신 및 저장 완료");
      } catch (Exception e) {
          System.err.println("❌ 게시글 저장 중 오류 발생: " + e.getMessage());
          // 필요하다면 로그로 남기고, 오류 내용을 DB나 파일로 기록 가능
      }
  }
  
  // 두 번 저장 됨
//  @PostMapping("/posts")
//  @Operation(summary = "게시글 저장", description = "게시글을 저장 합니다.")
//  public ResponseEntity<?> publishPost(@RequestBody Post post) {
//      post.setCreatedAt(Instant.now().toString());
//      rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, post);
//      System.out.println("✅ 게시글 수신:");
//      System.out.println("제목: " + post.getTitle());
//      System.out.println("내용: " + post.getContent());
//      System.out.println("작성일시: " + post.getCreatedAt());
//      
//      Post saved = postService.savePost(post);
//      
//      return ResponseEntity.ok(saved);
//  }

  
  // 모든 게시글 조회 
  @GetMapping
  @Operation(summary = "전체 게시글 조회", description = "전체 게시글을 조회")
  public ResponseEntity<?> getAllPosts() {
	  try {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
	  }catch(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("게시글 조회 중 오류가 발생했습니다.");
	  }
  }
  

  // 특정 게시글 id 조회 
  @GetMapping("/{id}")
  @Operation(summary = "특정 게시글 조회", description = "특정 게시글을 조회")
  public ResponseEntity<?> getPostById(@PathVariable Long id) {
      try {
          Optional<Post> optionalPost = postService.getPostById(id);
          if (optionalPost.isPresent()) {
              return ResponseEntity.ok(optionalPost.get());
          } else {
              return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                   .body("해당 ID의 게시글을 찾을 수 없습니다.");
          }
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("게시글 조회 중 오류가 발생했습니다.");
      }
  }

//  @GetMapping("/{id}")
//  public ResponseEntity<?> getPostById(@PathVariable Long id) {
//      try {
//          return postService.getPostById(id)
//                  .map(ResponseEntity::ok)
//                  .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
//                          .body("해당 ID의 게시글을 찾을 수 없습니다."));
//      } catch (Exception e) {
//          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                  .body("게시글 조회 중 오류가 발생했습니다.");
//      }
//  }

  // 특정 게시글 삭제
  @DeleteMapping("/{id}")
  @Operation(summary = "게시글 삭제", description = "게시글을 삭제")
  public ResponseEntity<?> deletePost(@PathVariable Long id) {
      try {
          if (postService.getPostById(id).isEmpty()) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND)
                      .body("삭제할 게시글이 존재하지 않습니다.");
          }
          postService.deletePostById(id);
          return ResponseEntity.ok("게시글이 삭제되었습니다.");
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("게시글 삭제 중 오류가 발생했습니다.");
      }
  }

  // 특정 게시물 수정
  @PutMapping("/{id}")
  @Operation(summary = "게시글 수정", description = "게시글을 수정")
  public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
      try {
          return postService.getPostById(id).map(existingPost -> {
              existingPost.setTitle(updatedPost.getTitle());
              existingPost.setContent(updatedPost.getContent());
              existingPost.setCreatedAt(updatedPost.getCreatedAt());
              postService.savePost(existingPost);
              return ResponseEntity.ok("게시글이 수정되었습니다.");
          }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body("수정할 게시글이 존재하지 않습니다."));
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("게시글 수정 중 오류가 발생했습니다.");
      }
  }
  
}