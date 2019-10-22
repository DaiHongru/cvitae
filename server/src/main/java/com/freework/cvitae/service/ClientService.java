package com.freework.cvitae.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author daihongru
 */
@Service
public interface ClientService {
    /**
     * 根据用户ID获取的简历信息
     *
     * @param userId
     * @return
     */
    Map<String, Integer> getUserCvitaeInfo(Integer userId);
}
