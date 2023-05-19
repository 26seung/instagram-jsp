package com.project.instagram.service;

import com.project.instagram.domain.user.User;
import com.project.instagram.web.dto.user.UserProfileDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User 회원프로필사진변경(int principalId, MultipartFile profileImageFile);
    UserProfileDto 회원프로필(int pageUserId, int principalId);
    User 회원수정(int id, User user);
}
