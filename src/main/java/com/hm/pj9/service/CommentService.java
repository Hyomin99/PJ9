package com.hm.pj9.service;

import com.hm.pj9.model.*;
import com.hm.pj9.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private NotificationService notificationService;


    /*
    * 저장 부분
    * */
    public void saveComment(Comment comment, String commenter_id, Integer postNum) { // 댓글 저장

        User commenterUser = userRepository.findById(commenter_id).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Post post = boardRepository.findById(postNum).orElseThrow(() -> new RuntimeException("게시판 번호를 찾을 수 없습니다."));

        comment.setCommenterId(commenterUser);
        comment.setPostNum(post);
        Comment savedComment = commentRepository.save(comment);

        notificationService.createCommentNotification(post, savedComment, commenterUser);


    }

    public void saveReply(Reply reply, String replier_id, Integer postNum, Integer commentNum) { // 대 댓글 저장

        User user = userRepository.findById(replier_id).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Post post = boardRepository.findById(postNum).orElseThrow(() -> new RuntimeException("게시판 번호를 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentNum).orElseThrow(() -> new RuntimeException("댓글 번호를 찾을 수 없습니다."));

        reply.setReplierId(user);
        reply.setPostNum(post);
        reply.setCommentNum(comment);
        Reply savedReply = replyRepository.save(reply);

        notificationService.createReplyNotification( post, reply , savedReply,  comment);

    }


    /*
     * 삭제 부분
     * */

    @Transactional
    public void deleteComment(Integer commentNum) { //댓글 삭제
        Comment comment = commentRepository.findById(commentNum).orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        notificationService.deleteCommentNotification(comment); // 댓글 알림 먼저 삭제
        replyRepository.deleteAllByCommentNum(comment); // 댓글에 달린 대댓글 삭제
        commentRepository.deleteById(commentNum); //댓글 삭제
    }

    @Transactional
    public void deleteReply(Integer replyNum) { //대댓글 삭제
        Reply reply = replyRepository.findById(replyNum).orElseThrow(() -> new RuntimeException("대댓글을 찾을 수 없습니다."));
        notificationService.deleteReplyNotification(reply); //대댓글에 달린 알림 삭제
        replyRepository.deleteById(replyNum); //대댓글 삭제
    }


    /*
    * 열람 부분
    * */

    public List<Comment> getComments(Post broughtPost) { //게시물 댓글 가져오기
        return commentRepository.findByPostNum(broughtPost);
    }

    public List<Reply> getReplies(Post broughtPost) { //게시물  대댓글 가져오기
        return replyRepository.findByPostNum(broughtPost);
    }
}
