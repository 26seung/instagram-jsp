package com.project.instagram.handler.ex;

public class CustomException extends RuntimeException{

    // 객체를 구분할 때 사용
    public static final long serialVersionUID = 1L;

    public CustomException(String message){
        super(message);
    }
}
