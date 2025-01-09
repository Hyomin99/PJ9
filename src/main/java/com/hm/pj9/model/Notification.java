package com.hm.pj9.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "notifications")
public class Notification {



    @Id
    @Column(name = "notification_num")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationNum; // 알림 번호

    @ManyToOne
    @JoinColumn(name = "post_num", referencedColumnName = "post_num")
    private Post postNum; // 알림이 일어난 게시물 번호

    @ManyToOne
    @JoinColumn(name = "comment_num", referencedColumnName = "comment_num")
    private Comment commentNum; // 알림이 일어난 댓글의 번호

    @ManyToOne
    @JoinColumn(name = "reply_num", referencedColumnName = "reply_num")
    private Reply replyNum; // 알림이 일어난 대댓글 번호

    @ManyToOne
    @JoinColumn(name = "target_id", referencedColumnName = "user_id")
    private User targetId; // 알림을 받아야 할 사용자

    @Column(name = "notification_type")
    private String notificationType; // 알림 종류

    @Column(name = "read_status")
    private byte readStatus; // 읽음 상태

    @Column(name = "created_at")
    private LocalDateTime createdAt; //작성 시간


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 현재 시간 설정
    }

    public Notification(){

    }

    public Notification(Post postNum, Comment commentNum, User targetId, String notificationType, byte readStatus) {
        this.postNum = postNum;
        this.commentNum = commentNum;
        this.targetId = targetId;
        this.notificationType = notificationType;
        this.readStatus = readStatus;
    }

    public Notification(Post postNum, Reply replyNum, User targetId, String notificationType, byte readStatus) {
        this.postNum = postNum;
        this.replyNum = replyNum;
        this.targetId = targetId;
        this.notificationType = notificationType;
        this.readStatus = readStatus;
    }
}
