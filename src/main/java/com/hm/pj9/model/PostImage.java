package com.hm.pj9.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "postimages")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_num")
    private Integer imageNum;

    @ManyToOne
    @JoinColumn(name = "post_num", referencedColumnName = "post_num")
    private Post post;

    @Column(name = "image_url")
    private String imageUrl;

    public PostImage() {
    }

    public PostImage(Post post, String imageUrl) {
        this.post = post;
        this.imageUrl = imageUrl;
    }
}
