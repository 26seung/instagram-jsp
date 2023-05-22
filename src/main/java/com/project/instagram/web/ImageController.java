package com.project.instagram.web;

import com.project.instagram.config.auth.PrincipalDetails;
import com.project.instagram.domain.image.Image;
import com.project.instagram.handler.ex.CustomValidationException;
import com.project.instagram.service.ImageService;
import com.project.instagram.web.dto.image.ImageUploadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/")
    public String story(){
        return "image/story";
    }

    @GetMapping("/image/story/{imageId}")
    public String singleStory(@PathVariable int imageId, Model model){
        model.addAttribute("image", imageId);
        return "image/singleStory";
    }
    @GetMapping("/image/popular")
    public String popular(Model model){

        // api 는 데이터를 리턴하는 서버
        List<Image> images = imageService.인기사진();
        model.addAttribute("images", images);
        return "image/popular";
    }
    @GetMapping("/image/upload")
    public String upload(){
        return "image/upload";
    }

    @PostMapping("/image")
    public String imageUpload(ImageUploadDto imageUploadDto, @AuthenticationPrincipal PrincipalDetails principalDetails){

        if(imageUploadDto.getFile().isEmpty()){
            System.out.println("이미지 파일이 없습니다.");
            throw new CustomValidationException("이미지가 첨부되지 않았습니다.",null);
        }

        imageService.사진업로드(imageUploadDto, principalDetails);
        return "redirect:/user/" + principalDetails.getUser().getId();
    }
}
