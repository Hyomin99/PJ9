package com.hm.pj9.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_num")
    private Integer postNum;


    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "user_id")
    private User author; //작성자

    private String title; //제목

    private String content; //내용

    @Column(name = "created_at")
    private LocalDateTime createdAt; //작성 시간

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 현재 시간 설정
    }

    @Column(name = "board_type")
    private String boardType; // 게시판 종류

}
