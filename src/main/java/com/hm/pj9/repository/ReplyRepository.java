package com.hm.pj9.repository;

import com.hm.pj9.model.Comment;
import com.hm.pj9.model.Post;
import com.hm.pj9.model.Reply;
import com.hm.pj9.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    List<Reply> findByPostNum(Post postNum); //게시물 번호로 대 댓글 가져오기

    List<Reply> findAllByReplierId(User user);

    void deleteAllByCommentNum(Comment commentNum);

    void deleteAllByPostNum(Post post);

    void deleteAllByReplierId(User user);

    void deleteAllByReplyNum(Reply reply);
}
