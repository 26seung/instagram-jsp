package com.project.instagram.domain.image;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.instagram.domain.comment.Comment;
import com.project.instagram.domain.likes.Likes;
import com.project.instagram.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String caption;              // tag 메시지
    private String postImageUrl;        //  사진을 전송 받아서 그 사진을 서버에 특정 폴더에 저장 -DB 에 그 저장된 경로를 INSERT

    @JsonIgnoreProperties({"images"})
    @JoinColumn(name = "userId")
    @ManyToOne
    private User user;

    // 이미지 좋아요
    @JsonIgnoreProperties({"image"})
    @OneToMany(mappedBy = "image")
    private List<Likes> likes;

    //  댓글
    @OrderBy("id DESC")
    @JsonIgnoreProperties({"image"})
    @OneToMany(mappedBy = "image")
    private List<Comment>comments;

    @Transient  // DB에 컬럼이 만들어지지 않는다.
    private boolean likeState;
    @Transient
    private int likeCount;

    private LocalDateTime createDate;
    @PrePersist // DB 에 INSERT 되기 직전에 실행
    public void createDate() {
        this.createDate = LocalDateTime.now();
    }

}
