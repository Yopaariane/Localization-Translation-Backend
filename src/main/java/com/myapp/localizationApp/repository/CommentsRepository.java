package com.myapp.localizationApp.repository;

import com.myapp.localizationApp.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long>  {
    List<Comments> findByTermId(Long termId);

    List<Comments> findCommentByTermIdAndUserId(Long termId, Long userId);
}
