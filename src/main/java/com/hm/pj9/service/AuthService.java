package com.hm.pj9.service;

import com.hm.pj9.model.*;
import com.hm.pj9.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;


    /*
     * 계정 관련
     * */

    public void createAccount(String userId, String userPw) { // 계정 생성
        userRepository.save(new User(userId,userPw));
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
}
