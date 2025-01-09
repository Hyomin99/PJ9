package com.hm.pj9.service;

import com.hm.pj9.model.*;
import com.hm.pj9.repository.BoardRepository;
import com.hm.pj9.repository.NotificationRepository;
import com.hm.pj9.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BoardRepository boardRepository;


    /*
    * 생성 부분
    * */

    public void createCommentNotification(Post post, Comment saveComment, User commenterUser) { // 댓글 알림 생성

        String commenter = commenterUser.getUserId(); // 댓글 작성자
        String postAuthor = post.getAuthor().getUserId(); // 게시물 작성자

        if (!commenter.equals(postAuthor)) {
            notificationRepository.save(new Notification(post,saveComment,post.getAuthor(),"post_notice",(byte) 0));
        }
    }

    public void createReplyNotification(Post post,Reply reply ,Reply savedReply, Comment comment){ // 대 댓글 알림 생성

        String commenter = comment.getCommenterId().getUserId(); //댓글 작성자
        String replier = reply.getReplierId().getUserId(); // 대댓글 작성자
        String boardAuthor = post.getAuthor().getUserId(); // 게시물 작성자

        if (!commenter.equals(replier) && !boardAuthor.equals(replier)) {
            notificationRepository.save(new Notification(post,savedReply,comment.getCommenterId(),"comment_notice",(byte)0));
        }
    }

    /*
    * 삭제 부분
    * */

    public void deleteCommentNotification(Comment comment){// 댓글 알림 삭제
        notificationRepository.deleteAllByCommentNum(comment);
    }


    public void deleteReplyNotification(Reply reply){// 대 댓글 알림 삭제
        notificationRepository.deleteAllByReplyNum(reply);
    }


    /*
    * 알림 횟수 카운트
    * */

    public NotificationResult getNotification(String userId) { //아이디에 해당하는 알림 횟수 카운트

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

    /*
    * 알림 읽음 처리
    * */

    public void readNotification(Integer post_num, String userId, String type) { //알림 읽었을때
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")); //알림 대상자
        Post post = boardRepository.findByPostNum(post_num); //게시글 번호에 대한 게시물 가져오기
        notificationRepository.deleteNotification(user, post, type);
    }

}
