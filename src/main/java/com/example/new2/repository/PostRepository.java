package com.example.new2.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.new2.dto.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
