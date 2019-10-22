package com.freework.cvitae.client.feign;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author daihongru
 */
@Component
public class CvitaeClientFallback implements CvitaeClient {

    @Override
    public Map<String, Integer> getUserCvitaeInfo(Integer userId) {
        Map<String, Integer> map = new HashMap<>(16);
        map.put(CVITAE_COUNT_KEY, 0);
        map.put(ENTERPRISE_CV_COUNT_KEY, 0);
        map.put(PASS_CVITAE_COUNT_KEY, 0);
        return map;
    }
}
