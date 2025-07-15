package com.example.new2.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.new2.dto.Post;
import com.example.new2.repository.PostRepository;

@Service
public class PostService {
	
    @Autowired
    private PostRepository postRepository;
	
	public PostService(PostRepository postRepository) {
		this.postRepository = postRepository;
	}
	
	// 저장
	public Post savePost(Post post) {
		return postRepository.save(post);
	}
	
	// 조회 
	public List<Post> getAllPosts(){
		return postRepository.findAll();
	}
	
	// 특정 id 게시글 조회 
	public Optional<Post> getPostById(Long id){
		return postRepository.findById(id);
	}
	
	// 삭제 
	public void deletePostById(Long id) {
		postRepository.deleteById(id);
	}
	
	// 수정
	public Optional<Post> updatePost(Long id, Post updatedPost) {
	    return postRepository.findById(id).map(post -> {
	        post.setTitle(updatedPost.getTitle());
	        post.setContent(updatedPost.getContent());
	        post.setCreatedAt(updatedPost.getCreatedAt()); // 현재 시간으로 set
	        return postRepository.save(post);
	    });
	}

	
}
