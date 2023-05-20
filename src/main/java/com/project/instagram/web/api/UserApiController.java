package com.project.instagram.web.api;

import com.project.instagram.config.auth.PrincipalDetails;
import com.project.instagram.domain.user.User;
import com.project.instagram.service.SubscribeService;
import com.project.instagram.service.UserService;
import com.project.instagram.web.dto.CMRespDto;
import com.project.instagram.web.dto.subscribe.SubscribeDto;
import com.project.instagram.web.dto.user.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;
    private final SubscribeService subscribeService;

    @PutMapping("/api/user/{principalId}/profileImageUrl")
    public ResponseEntity<?> profileImageUrlUpdate(@PathVariable int principalId, MultipartFile profileImageFile,
                                                   @AuthenticationPrincipal PrincipalDetails principalDetails){

        User userEntity = userService.회원프로필사진변경(principalId, profileImageFile);
        principalDetails.setUser(userEntity);   //  세션 변경

        return new ResponseEntity<>(new CMRespDto<>(1,"프로필사진변경 성공", null),HttpStatus.OK);
    }

    @GetMapping("/api/user/{pageUserId}/subscribe")
    public ResponseEntity<?> subscribeList(@PathVariable int pageUserId, @AuthenticationPrincipal PrincipalDetails principalDetails){

        List<SubscribeDto> subscribeDto = subscribeService.구독리스트(principalDetails.getUser().getId(),pageUserId);

        return new ResponseEntity<>(new CMRespDto<>(1,"구독자 정보 리스트 가져오기 성공",subscribeDto), HttpStatus.OK);
    }

    @PutMapping("/api/user/{id}")
    public CMRespDto<?> update(
            @PathVariable int id,
            @Valid UserUpdateDto userUpdateDto,
            BindingResult bindingResult,            //  꼭 @Valid 가 적혀있는 다음 파라미터에 적어야 함
            @AuthenticationPrincipal PrincipalDetails principalDetails){

        //  유효성검사 AOP 알아서 처리됨 (BindingResult 만 있으면)
//            System.out.println("userUpdateDto: "+ userUpdateDto);
//            System.out.println("principalDetails : " + principalDetails);         // print 안에 출력하려는 객체가 다른 오브젝트를 포함하고 있어 오류가 발생할 수 있다.
            User userEntity = userService.회원수정(id,userUpdateDto.toEntity());
            // 세션 정보를 업데이트 해주어야 정보 반영이 됨
            principalDetails.setUser(userEntity);
            return new CMRespDto<>(1,"회원수정완료",userEntity);

    }
}
