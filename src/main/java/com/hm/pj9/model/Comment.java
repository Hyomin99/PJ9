package com.hm.pj9.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

@Table(name = "comments", indexes = {
        @Index(name = "idx_commentNum", columnList = "comment_num")
})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_num")
    private Integer commentNum; //댓글 번호

    @ManyToOne
    @JoinColumn(name = "commenter_id", referencedColumnName = "user_id")
    private User commenterId; //댓글 작성자

    @ManyToOne
    @JoinColumn(name = "post_num", referencedColumnName = "post_num")
    private Post postNum; //댓글 작성한 게시물 번호

    private String content; //내용

    @Column(name = "created_at")
    private LocalDateTime createdAt; //작성 시간

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 현재 시간 설정
    }
}
