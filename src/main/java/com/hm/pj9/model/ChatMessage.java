package com.hm.pj9.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@Document(collection = "chat")
public class ChatMessage {
    @Id
    private String id; // MongoDB의 자동 생성 ID
    private String fromUserId; //보낸 사람
    private String toUserId; // 받는 사람
    private String text; // 내용
    private String transferTime; // 전송 시간




}
