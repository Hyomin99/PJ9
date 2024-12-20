package com.hm.pj9.service;

import com.hm.pj9.model.*;
import com.hm.pj9.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
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


    public String createAccount(String userId, String userPw) { // 계정 생성
        User user = new User();
        user.setUserId(userId);
        user.setUserPw(userPw);

        userRepository.save(user);
        return "회원가입 완료";
    }

    public int signInCheck(String userId, String userPw) { //로그인
        String result = userRepository.getPwById(userId);
        if (result != null && result.equals(userPw)) {
            return 1;
        } else {
            return 0; //틀렸을때
        }

    }

    public boolean duplicationId(String userId) { //아이디 중복 확인
        return userRepository.existsById(userId);
    }


    public NotificationResult getNotification(String userId) { //아이디에 해당하는 알림 찾기

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<Notification> notifications = notificationRepository.getNotificationByTargetIdAndReadStatus(user, (byte) 0);

        Map<Integer, Map<String, Integer>> postCountMap = new HashMap<>(); //게시물 번호, 게시물 제목, 중복 횟수
        Map<Integer, Map<String, Integer>> commentCountMap = new HashMap<>();

        for (Notification no : notifications) {
            if (no.getNotificationType().equals("post_notice")) { // 게시글에 해당하는 알림
                Map<String, Integer> postMap = new HashMap<>();
                Integer key = no.getPostNum().getPostNum();
                String valueMapKey = no.getPostNum().getTitle(); // 게시물 제목
                postMap.put(valueMapKey, postMap.getOrDefault(valueMapKey, 0) + 1);
                postCountMap.put(key, postMap);
            } else { //댓글에 해당하는 알림
                Map<String, Integer> commentMap = new HashMap<>();
                Integer key = no.getPostNum().getPostNum();
                String valueMapKey = no.getPostNum().getTitle(); // 키값
                commentMap.put(valueMapKey, commentMap.getOrDefault(valueMapKey, 0) + 1);
                commentCountMap.put(key, commentMap);
            }
        }
        return new NotificationResult(notifications, postCountMap, commentCountMap);
    }

    public void readNotification(Integer post_num, String userId, String type) { //알림 읽었을때
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")); //알림 대상자
        Post post = boardRepository.findByPostNum(post_num); //게시글 번호에 대한 게시물 가져오기
        notificationRepository.deleteNotification(user, post, type);
    }

    public Map<String, List<?>> getUserInfo(String userId) { //내정보
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Map<String, List<?>> userInfo = new HashMap<>();
        List<Post> postList = boardRepository.findAllByAuthor(user);
        List<Comment> commentList = commentRepository.findAllByCommenterId(user);
        List<Reply> replyList = replyRepository.findAllByReplierId(user);

        userInfo.put("posts", postList);
        userInfo.put("comments", commentList);
        userInfo.put("replies", replyList);

        return userInfo;
    }

    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<Post> posts = boardRepository.findAllByAuthor(user);
        List<Comment> comments = commentRepository.findAllByCommenterId(user);
        List<Reply> replies = replyRepository.findAllByReplierId(user);

        deleteNotification(posts,comments,replies,user);
        deleteReply(posts,comments,replies,user);
        deleteComment(posts,user);
        deletePost(posts,user);
        deleteChat(userId);
        deleteAccount(userId);
    }

    public void deleteNotification(List<Post> posts, List<Comment> comments, List<Reply> replies, User user){
        for(Post post : posts){
            notificationRepository.deleteAllByPostNum(post);
        }
        for(Comment comment: comments){
            notificationRepository.deleteAllByCommentNum(comment);
        }
        for (Reply reply : replies){
            notificationRepository.deleteAllByReplyNum(reply);
        }

        notificationRepository.deleteAllByTargetId(user);

        System.out.println("계정과 관련된 알림이 전부 삭제되었습니다");
    }

    public void deleteReply(List<Post> posts, List<Comment> comments, List<Reply> replies, User user){
        for(Post post : posts){
            replyRepository.deleteAllByPostNum(post);
        }
        for(Comment comment: comments){
            replyRepository.deleteAllByCommentNum(comment);
        }
        for (Reply reply : replies){
            replyRepository.deleteAllByReplyNum(reply);
        }

        replyRepository.deleteAllByReplierId(user);

        System.out.println("계정과 관련된 대댓글이 전부 삭제되었습니다");
    }

    public void deleteComment(List<Post> posts, User user){
        for(Post post : posts){
            commentRepository.deleteAllByPostNum(post);
        }
        commentRepository.deleteAllByCommenterId(user);

        System.out.println("계정과 관련된 댓글이 전부 삭제되었습니다");
    }

    public void deletePost(List<Post> posts, User user){
        for(Post post : posts){
            boardImageRepository.deleteAllByPost(post);
        }
        boardRepository.deleteAllByAuthor(user);
        System.out.println("계정과 관련된 게시글이 전부 삭제되었습니다");
    }

    public void deleteChat(String userId){
        chatRepository.deleteAllByFromUserIdOrToUserId(userId, userId);
        System.out.println("계정과 관련된 채팅이 전부 삭제되었습니다");

    }

    public void deleteAccount(String userId){
        userRepository.deleteByUserId(userId);
        System.out.println("계정과 관련된 채팅이 전부 삭제되었습니다");
    }

}
