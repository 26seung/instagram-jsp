package com.project.instagram.service;

import com.project.instagram.web.dto.subscribe.SubscribeDto;

import java.util.List;

public interface SubscribeService {

    List<SubscribeDto> 구독리스트(int principalId, int pageUserId);
    void 구독하기(int fromUserId, int toUserId);
    void 구독취소하기(int fromUserId, int toUserId);
}
