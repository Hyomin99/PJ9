package com.hm.pj9.repository;

import com.hm.pj9.model.Comment;
import com.hm.pj9.model.Post;
import com.hm.pj9.model.PostImage;
import com.hm.pj9.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostNum(Post postNum); //게시물 번호로 댓글 가져오기

    List<Comment> findAllByCommenterId(User user);

    void deleteAllByPostNum(Post post);

    void deleteAllByCommenterId(User user);
}
