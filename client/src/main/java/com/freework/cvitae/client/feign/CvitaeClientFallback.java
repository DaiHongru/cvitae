package com.freework.cvitae.client.feign;

import java.util.Map;

/**
 * @author daihongru
 */
public class CvitaeClientFallback implements CvitaeClient {
    @Override
    public Map<String, Object> getUserCvitaeInfo(Integer userId) {
        return null;
    }
}
