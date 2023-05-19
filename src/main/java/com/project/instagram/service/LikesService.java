package com.project.instagram.service;

public interface LikesService {

    void 좋아요(int imageId, int principalId);
    void 좋아요취소(int imageId, int principalId);

}
