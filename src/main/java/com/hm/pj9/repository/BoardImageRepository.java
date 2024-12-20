package com.hm.pj9.repository;

import com.hm.pj9.model.Post;
import com.hm.pj9.model.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardImageRepository extends JpaRepository<PostImage, Integer> {
    List<PostImage> findByPost(Post post); //게시물 번호로 이미지 가져오기

    void deleteAllByPost(Post post);
}
