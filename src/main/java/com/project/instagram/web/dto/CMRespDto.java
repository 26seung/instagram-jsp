package com.project.instagram.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CMRespDto<T> {

    private int code;       // 1은 성공 , -1은 실패
    private String message;
    private T data;
}
