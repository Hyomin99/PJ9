package com.hm.pj9.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "replies", indexes = {
        @Index(name = "idx_replyNum", columnList = "reply_num")
})
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_num")
    private Integer replyNum; //대댓글 번호

    @ManyToOne
    @JoinColumn(name = "comment_num", referencedColumnName = "comment_num")
    private Comment commentNum; //댓글 번호

    @ManyToOne
    @JoinColumn(name = "replier_id", referencedColumnName = "user_id")
    private User replierId; //대댓글 작성자

    @ManyToOne
    @JoinColumn(name = "post_num", referencedColumnName = "post_num")
    private Post postNum; //게시물 번호

    private String content; //내용

    @Column(name = "created_at")
    private LocalDateTime createdAt; //작성 시간

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 현재 시간 설정
    }


}
