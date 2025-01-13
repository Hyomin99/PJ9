package com.hm.pj9.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "posts", indexes = {
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_content", columnList = "title, content"),
        @Index(name = "idx_postNum", columnList = "post_num")
})
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

    public String getFormattedCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        long diffInSeconds = Duration.between(createdAt, now).getSeconds();

        long diffInMinutes = diffInSeconds / 60;
        long diffInHours = diffInMinutes / 60;
        long diffInDays = diffInHours / 24;

        if (diffInDays > 0) {
            return diffInDays + "일 전";
        } else if (diffInHours > 0) {
            return diffInHours + "시간 전";
        } else if (diffInMinutes > 0) {
            return diffInMinutes + "분 전";
        } else {
            return "방금 전";
        }
    }

}
