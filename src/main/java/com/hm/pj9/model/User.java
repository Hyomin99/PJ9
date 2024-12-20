package com.hm.pj9.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class User {

    @Id
    @Column(name = "user_id")
    private String userId = "gksgyals0222";
    @Column(name = "user_pw")
    private String userPw;

}
