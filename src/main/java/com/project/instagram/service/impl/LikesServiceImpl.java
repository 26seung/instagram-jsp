package com.project.instagram.service.impl;

import com.project.instagram.domain.likes.LikesRepository;
import com.project.instagram.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikesServiceImpl implements LikesService {

    private final LikesRepository likesRepository;

    @Override
    @Transactional
    public void 좋아요(int imageId, int principalId){
        likesRepository.mLikes(imageId, principalId);
    }
    @Override
    @Transactional
    public void 좋아요취소(int imageId, int principalId){
        likesRepository.mUnLikes(imageId, principalId);
    }
}
