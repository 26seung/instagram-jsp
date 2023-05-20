package com.project.instagram.handler;

import com.project.instagram.handler.ex.CustomApiException;
import com.project.instagram.handler.ex.CustomException;
import com.project.instagram.handler.ex.CustomValidationApiException;
import com.project.instagram.handler.ex.CustomValidationException;
import com.project.instagram.util.Script;
import com.project.instagram.web.dto.CMRespDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice       //  모든 Exception 을 낚아챔
public class ControllerExceptionHandler {

    @ExceptionHandler(CustomValidationException.class)
    public String validationException(CustomValidationException e){

        // CMrespDto, Script 비교
        // 1. 클라이언트에게 응답할 때는 Script 가 좋음
        // 2. Ajax 통신 - CMRespDto
        // 3. Android 통신 - CMRespDto
        if(e.getErrMap() == null){
            return Script.back(e.getMessage());
        }else {
        return Script.back(e.getErrMap().toString());
        }
    }
    @ExceptionHandler(CustomException.class)
    public String exception(CustomException e){
            return Script.back(e.getMessage());
        }

    @ExceptionHandler(CustomValidationApiException.class)
    public ResponseEntity<CMRespDto<?>> validationApiException(CustomValidationApiException e){
        return new ResponseEntity<>(new CMRespDto<>(-1,e.getMessage(),e.getErrMap()),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<CMRespDto<?>> apiException(CustomApiException e){
        return new ResponseEntity<>(new CMRespDto<>(-1,e.getMessage(),null),HttpStatus.BAD_REQUEST);
    }
}
