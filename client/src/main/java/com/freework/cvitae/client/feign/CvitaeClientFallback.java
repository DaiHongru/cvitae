package com.freework.cvitae.client.feign;

import com.freework.cvitae.client.vo.CvitaeVo;
import com.freework.cvitae.client.vo.EnterpriseCvVo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author daihongru
 */
@Component
public class CvitaeClientFallback implements CvitaeClient {

    @Override
    public Map<String, Object> getUserCvitaeInfo(Integer userId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<CvitaeVo> cvitaeVoList = new ArrayList<>();
        List<EnterpriseCvVo> enterpriseCvVoList = new ArrayList<>();
        Integer passCvitaeCount = 0;
        map.put(CVITAE_VO_LIST_KEY, cvitaeVoList);
        map.put(ENTERPRISE_CV_VO_LIST_KEY, enterpriseCvVoList);
        map.put(PASS_CVITAE_COUNT_KEY, passCvitaeCount);
        return map;
    }
}
