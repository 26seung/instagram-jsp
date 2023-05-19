package com.project.instagram.service;

import com.project.instagram.config.auth.PrincipalDetails;
import com.project.instagram.domain.image.Image;
import com.project.instagram.web.dto.image.ImageUploadDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ImageService {

    List<Image> 인기사진();
    Page<Image> 이미지스토리(int principalId, Pageable pageable);
    Image 단일스토리(int principalId, int imageId);
    void 사진업로드(ImageUploadDto imageUploadDto, PrincipalDetails principalDetails);
}
