package com.freework.cvitae.service.impl;


import com.freework.cvitae.client.feign.CvitaeClient;
import com.freework.cvitae.client.vo.CvitaeVo;
import com.freework.cvitae.client.vo.EnterpriseCvVo;
import com.freework.cvitae.dao.CvitaeDao;
import com.freework.cvitae.dao.EnterpriseCvDao;
import com.freework.cvitae.entity.Cvitae;
import com.freework.cvitae.entity.EnterpriseCv;
import com.freework.cvitae.enums.EnterpriseCvStateEnum;
import com.freework.cvitae.service.ClientService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author daihongru
 */
@Service
public class ClientServiceImpl implements ClientService {
    @Autowired(required = false)
    private CvitaeDao cvitaeDao;
    @Autowired(required = false)
    private EnterpriseCvDao enterpriseCvDao;

    @Override
    public Map<String, Object> getUserCvitaeInfo(Integer userId) {
        Map<String, Object> map = new HashMap<>(16);
        List<CvitaeVo> cvitaeVoList = new ArrayList<>();
        List<EnterpriseCvVo> enterpriseCvVoList = new ArrayList<>();
        Cvitae cvitae = new Cvitae();
        cvitae.setUserId(userId);
        List<Cvitae> cvitaeList = cvitaeDao.queryByRequirement(cvitae);
        if (cvitaeList != null && cvitaeList.size() > 0) {
            cvitaeVoList = cvitaeList.stream().map(e -> {
                CvitaeVo outPut = new CvitaeVo();
                BeanUtils.copyProperties(e, outPut);
                return outPut;
            }).collect(Collectors.toList());
        }
        EnterpriseCv enterpriseCv = new EnterpriseCv();
        enterpriseCv.setUserId(userId);
        Integer passCvitaeCount = 0;
        List<EnterpriseCv> enterpriseCvList = enterpriseCvDao.queryByRequirement(enterpriseCv);
        if (enterpriseCvList != null && enterpriseCvList.size() > 0) {
            enterpriseCvVoList = enterpriseCvList.stream().map(e -> {
                EnterpriseCvVo outPut = new EnterpriseCvVo();
                BeanUtils.copyProperties(e, outPut);
                return outPut;
            }).collect(Collectors.toList());
            enterpriseCv.setStatus(EnterpriseCvStateEnum.PASS.getState());
            enterpriseCvList = enterpriseCvDao.queryByRequirement(enterpriseCv);
            if (enterpriseCvList != null && enterpriseCvList.size() > 0) {
                passCvitaeCount = enterpriseCvList.size();
            }
        }
        map.put(CvitaeClient.CVITAE_VO_LIST_KEY, cvitaeVoList);
        map.put(CvitaeClient.ENTERPRISE_CV_VO_LIST_KEY, enterpriseCvVoList);
        map.put(CvitaeClient.PASS_CVITAE_COUNT_KEY, passCvitaeCount);
        return map;
    }
}
