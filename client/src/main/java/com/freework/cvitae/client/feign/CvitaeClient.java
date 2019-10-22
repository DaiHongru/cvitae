package com.freework.cvitae.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author daihongru
 */
@FeignClient(name = "CVITAE", fallback = CvitaeClientFallback.class)
public interface CvitaeClient {
    String CVITAE_COUNT_KEY = "cvitaeCount";
    String ENTERPRISE_CV_COUNT_KEY = "enterpriseCvCount";
    String PASS_CVITAE_COUNT_KEY = "passCvitaeCount";

    /**
     * 根据用户ID获取的简历信息
     *
     * @param userId
     * @return
     */
    @PostMapping("client/getUserCvitaeInfo")
    Map<String, Integer> getUserCvitaeInfo(@RequestBody Integer userId);

}
