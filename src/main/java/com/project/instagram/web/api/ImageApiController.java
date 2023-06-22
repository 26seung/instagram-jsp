package com.project.instagram.web.api;

import com.project.instagram.config.auth.PrincipalDetails;
import com.project.instagram.domain.image.Image;
import com.project.instagram.service.ImageService;
import com.project.instagram.service.LikesService;
import com.project.instagram.web.dto.CMRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ImageApiController {

    private final ImageService imageService;
    private final LikesService likesService;


    @GetMapping("/api/image")
    public ResponseEntity<?> imageStory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @PageableDefault(size=3, sort = "id") Pageable pageable){
        Page<Image> images = imageService.이미지스토리(principalDetails.getUser().getId(), pageable);
//        System.out.println("imageStory : " + images + "  / principalDetails : " + principalDetails.getUser());
        return new ResponseEntity<>(new CMRespDto<>(1,"성공", images), HttpStatus.OK);
    }
    @GetMapping("/api/image/{imageId}")
    public ResponseEntity<?> oneStoryImage(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable int imageId
                                        ){
        Image images = imageService.단일스토리(principalDetails.getUser().getId(), imageId);
        return new ResponseEntity<>(new CMRespDto<>(1,"성공", images), HttpStatus.OK);
    }
    @GetMapping("/api/image/popular")
    public ResponseEntity<?> popularImage(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                          @PageableDefault(size = 21) Pageable pageable){
        Page<Image> images = imageService.인기사진(principalDetails.getUser().getId(), pageable);
        return new ResponseEntity<>(new CMRespDto<>(1,"성공", images), HttpStatus.OK);
    }


    @PostMapping("/api/image/{imageId}/likes")
    public ResponseEntity<?> likes(@PathVariable int imageId, @AuthenticationPrincipal PrincipalDetails principalDetails){
        likesService.좋아요(imageId, principalDetails.getUser().getId());
        return new ResponseEntity<>(new CMRespDto<>(1,"좋아요성공",null),HttpStatus.CREATED);
    }
    @DeleteMapping("/api/image/{imageId}/likes")
    public ResponseEntity<?> unLikes(@PathVariable int imageId, @AuthenticationPrincipal PrincipalDetails principalDetails){
        likesService.좋아요취소(imageId, principalDetails.getUser().getId());
        return new ResponseEntity<>(new CMRespDto<>(1,"좋아요취소성공",null),HttpStatus.OK);
    }

}
