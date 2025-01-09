package com.hm.pj9.service;

import com.hm.pj9.model.Post;
import com.hm.pj9.model.User;
import com.hm.pj9.repository.BoardRepository;
import com.hm.pj9.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    UserRepository userRepository;

    /*
    * 첫 화면
    * */

    public List<Post> getAllPostsSortedByDate() {
        return boardRepository.findAll(Sort.by(Sort.Order.desc("createdAt"))); // createdAt 기준으로 내림차순 정렬
    }


    /*
    * 검색 기능
    * */
    public List<Post> getPostByTitle(String content) { //제목만 검색
        return boardRepository.findByTitleContaining(content);

    }

    public List<Post> getPostByTitleOrContent(String content) { // 제목 또는 내용으로 검색
        return boardRepository.findByTitleContainingOrContentContaining(content, content);
    }


    public List<Post> getPostById(String targetUserId) { // 사용자 ID로 검색
        User user = userRepository.findById(targetUserId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return boardRepository.findAllByAuthor(user);
    }
}
