package com.project.instagram.service;

import com.project.instagram.domain.comment.Comment;

public interface CommentService {

     Comment 댓글쓰기(String content, int imageId, int userId);
     void 댓글삭제(int id);
}
