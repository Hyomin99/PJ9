package com.hm.pj9.service;

import com.hm.pj9.model.*;
import com.hm.pj9.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

@Service
public class UserDeletionService {


    @Value("${file.upload-dir}")
    private String uploadDir; // 파일을 저장할 디렉토리 경로

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private BoardImageRepository boardImageRepository;

    /*
     * 계정 탈퇴
     * 외래키로 인해 실행 순서 중요함
     * */
    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<Post> posts = boardRepository.findAllByAuthor(user);
        List<Comment> comments = commentRepository.findAllByCommenterId(user);
        List<Reply> replies = replyRepository.findAllByReplierId(user);

        deleteNotification(posts, comments, replies, user);
        deleteReply(posts, comments, replies, user);
        deleteComment(posts, user);
        deletePost(posts, user);
        deleteChat(userId);
        deleteAccount(userId);
    }

    /*
     * 탈퇴하는 계정의 흔적 삭제 메서드
     * */

    public void deleteNotification(List<Post> posts, List<Comment> comments, List<Reply> replies, User user) {
        deleteByEntities(posts, notificationRepository::deleteAllByPostNum);
        deleteByEntities(comments, notificationRepository::deleteAllByCommentNum);
        deleteByEntities(replies, notificationRepository::deleteAllByReplyNum);
        notificationRepository.deleteAllByTargetId(user);

        System.out.println("계정과 관련된 알림이 전부 삭제되었습니다");
    }

    public void deleteReply(List<Post> posts, List<Comment> comments, List<Reply> replies, User user) {  //대댓글 삭제

        deleteByEntities(posts, replyRepository::deleteAllByPostNum);
        deleteByEntities(comments, replyRepository::deleteAllByCommentNum);
        deleteByEntities(replies, replyRepository::deleteAllByReplyNum);
        replyRepository.deleteAllByReplierId(user);

        System.out.println("계정과 관련된 대댓글이 전부 삭제되었습니다");
    }

    public void deleteComment(List<Post> posts, User user) { //댓글 삭제
        deleteByEntities(posts, commentRepository::deleteAllByPostNum);
        commentRepository.deleteAllByCommenterId(user);

        System.out.println("계정과 관련된 댓글이 전부 삭제되었습니다");
    }

    public void deletePost(List<Post> posts, User user) { // 게시글 삭제
        deletePostImages(posts);
        boardRepository.deleteAllByAuthor(user);
        System.out.println("계정과 관련된 게시글이 전부 삭제되었습니다");
    }

    public void deletePostImages(List<Post> posts) { // 게시글 사진 삭제
        for (Post post : posts) {

            List<PostImage> images = boardImageRepository.findByPost(post);

            for (PostImage image : images) { // 서버에 저장했던 사진들 먼저 삭제

                String imageName = image.getImageUrl();
                File file = new File(uploadDir + File.separator + imageName);

                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println("이미지 삭제 성공: " + imageName);
                    } else {
                        System.out.println("이미지 삭제 실패: " + imageName);
                    }
                } else {
                    System.out.println("이미지 파일이 존재하지 않습니다: " + imageName);
                }
            }

            boardImageRepository.deleteAllByPost(post); //로컬 사진 삭제 이후 db 삭제
        }

    }

    public void deleteChat(String userId) { //채팅 삭제
        chatRepository.deleteAllByFromUserIdOrToUserId(userId, userId);
        System.out.println("계정과 관련된 채팅이 전부 삭제되었습니다");
    }

    public void deleteAccount(String userId) {  // 계정 삭제
        userRepository.deleteByUserId(userId);
        System.out.println("계정이 삭제되었습니다");
    }


    /**
     * 공통 삭제 메서드
     */

    private <T> void deleteByEntities(List<T> entities, Consumer<T> deleteAction) {
        for (T entity : entities) {
            deleteAction.accept(entity);
        }
    }

}
