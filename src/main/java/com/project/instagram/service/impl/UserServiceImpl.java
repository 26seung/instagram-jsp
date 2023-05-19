package com.project.instagram.service.impl;

import com.project.instagram.domain.subscribe.SubscribeRepository;
import com.project.instagram.domain.user.User;
import com.project.instagram.domain.user.UserRepository;
import com.project.instagram.handler.ex.CustomApiException;
import com.project.instagram.handler.ex.CustomException;
import com.project.instagram.handler.ex.CustomValidationApiException;
import com.project.instagram.service.UserService;
import com.project.instagram.web.dto.user.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${file.path}")
    private String uploadFolder;

    @Override
    @Transactional
    public User 회원프로필사진변경(int principalId, MultipartFile profileImageFile){

        UUID uuid = UUID.randomUUID();
        String imageFileName =uuid + "_" + profileImageFile.getOriginalFilename();
        System.out.println("이미지 파일 이름 : " + imageFileName);

        Path imageFilePath = Paths.get(uploadFolder + imageFileName);

        // 통신 I/O  -> 예외가 발생할 수 있다.
        try{
            Files.write(imageFilePath, profileImageFile.getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }

        User userEntity = userRepository.findById(principalId).orElseThrow(()->{
            throw new CustomApiException("유저를 찾을 수 없습니다.");
        });
        userEntity.setProfileImanageUrl(imageFileName);

        return userEntity;
    }       //  더티체킹으로 업데이트 됨.
    @Override
    @Transactional(readOnly = true)
    public UserProfileDto 회원프로필(int pageUserId, int principalId){
        UserProfileDto dto = new UserProfileDto();

        // SELECT * FROM image WHERE userId = :userId;
        User userEntity = userRepository.findById(pageUserId).orElseThrow(()->{
            throw new CustomException("해당 프로필 페이지는 없는 페이지입니다.");
        });

        dto.setUser(userEntity);
        dto.setPageOwnerState(pageUserId == principalId);  //  1은 페이지 주인 , -1은 주인이 아님
        dto.setImageCount(userEntity.getImages().size());

        int subscribeState = subscribeRepository.mSubscribeState(principalId,pageUserId);
        int subscribeCount = subscribeRepository.mSubscribeCount(pageUserId);

        dto.setSubscribeState(subscribeState == 1);       //  true
        dto.setSubscribeCount(subscribeCount);
        //  profile 페이지에서의 좋아요 개수 확인 .. front 단에서 ${image.likes.size} 로 사용해도 되지만 Back 에서 만들어진 데이터를 넘기는것이 좋음
        userEntity.getImages().forEach(image -> {
            image.setLikeCount(image.getLikes().size());
        });

        return dto;
    }
    @Override
    @Transactional
    public User 회원수정(int id, User user){

        // 1. 영속화
        User userEntity = userRepository.findById(id).orElseThrow(()->{
            return new CustomValidationApiException("찾을 수 없는 id 입니다.");
        });

        // 2. 영속화 된 오브젝트를 수정 - 더티체킹(업데이트 완료)
        userEntity.setName(user.getName());

        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);

        userEntity.setPassword(encPassword);
        userEntity.setBio(user.getBio());
        userEntity.setWebsite(user.getWebsite());
        userEntity.setPhone(user.getPhone());
        userEntity.setGender(user.getGender());

        return userEntity;
    }
}
