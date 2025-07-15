package com.example.new2.postListener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.new2.config.RabbitMQConfig;
import com.example.new2.dto.Post;
import com.example.new2.repository.PostRepository;

@Component
public class PostListener {
	
	@Autowired
	private PostRepository postRepository;
	
	// 메시지를 받을 큐와 메서드를 매핑
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receivePost(Post post) {
        System.out.println("받은 메시지: " + post.getTitle());

        // DB에 저장
        Post postEntity = new Post();
        postEntity.setTitle(post.getTitle());
        postEntity.setContent(post.getContent());
        postEntity.setCreatedAt(post.getCreatedAt());

        postRepository.save(postEntity);
        System.out.println("DB에 게시글 저장 완료");
    }
}
