package com.project.instagram.service.impl;

import com.project.instagram.domain.comment.Comment;
import com.project.instagram.domain.comment.CommentRepository;
import com.project.instagram.domain.image.Image;
import com.project.instagram.domain.user.User;
import com.project.instagram.domain.user.UserRepository;
import com.project.instagram.handler.ex.CustomApiException;
import com.project.instagram.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Comment 댓글쓰기(String content, int imageId, int userId){

        // 객체를 만들 때 id 값만 담아서 insert 할 수 있다.
        // 대신 return 시에 image 객체와 user 객체는 id 값만 가지고 있는 빈 객체를 리턴받는다.
        Image image = new Image();
        image.setId(imageId);

        User userEntity = userRepository.findById(userId).orElseThrow(()->{
            throw new CustomApiException("유저 아이디를 찾을 수 없습니다.");
        });
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setImage(image);
        comment.setUser(userEntity);

        return commentRepository.save(comment);
    }
    @Override
    @Transactional
    public void 댓글삭제(int id){
        try {
            commentRepository.deleteById(id);
        }catch (Exception e){
            throw new CustomApiException(e.getMessage());
        }
    }
}
