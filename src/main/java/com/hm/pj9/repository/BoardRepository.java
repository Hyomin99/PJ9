package com.hm.pj9.repository;

import com.hm.pj9.model.Post;
import com.hm.pj9.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Post, Integer> {

    Post findByPostNum(Integer postNum); //게시물 번호로 게시물 정보 찾아오기
    Post findByPostNumAndAuthor(Integer postNum, User user); //게시글 번호와 유저 이름으로 정보 찾아오기

    List<Post> findByTitleContaining(String title);

    List<Post> findByTitleContainingOrContentContaining(String title, String content);

    List<Post> findAllByAuthor(User user);

    void deleteAllByAuthor(User user);


}