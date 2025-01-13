package com.hm.pj9.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "user", indexes = {
        @Index(name = "idx_id", columnList = "user_id")
})
public class User {

    @Id
    @Column(name = "user_id")
    private String userId = "gksgyals0222";
    @Column(name = "user_pw")
    private String userPw;

    public User(String userId, String userPw) {
        this.userId = userId;
        this.userPw = userPw;
    }

    public User() {
    }
}
