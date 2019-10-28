package com.freework.cvitae.service.impl;


import com.freework.cvitae.client.feign.CvitaeClient;
import com.freework.cvitae.dao.CvitaeDao;
import com.freework.cvitae.dao.EnterpriseCvDao;
import com.freework.cvitae.entity.Cvitae;
import com.freework.cvitae.entity.EnterpriseCv;
import com.freework.cvitae.enums.EnterpriseCvStateEnum;
import com.freework.cvitae.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Integer> getUserCvitaeInfo(Integer userId) {
        Map<String, Integer> map = new HashMap<>(16);
        Integer cvitaeCount = 0;
        Integer enterpriseCvCount = 0;
        Integer passCvitaeCount = 0;
        Cvitae cvitae = new Cvitae();
        cvitae.setUserId(userId);
        List<Cvitae> cvitaeList = cvitaeDao.queryByRequirement(cvitae);
        if (cvitaeList != null && cvitaeList.size() > 0) {
            cvitaeCount = cvitaeList.size();
        }
        EnterpriseCv enterpriseCv = new EnterpriseCv();
        enterpriseCv.setUserId(userId);
        List<EnterpriseCv> enterpriseCvList = enterpriseCvDao.queryByRequirement(enterpriseCv);
        if (enterpriseCvList != null && enterpriseCvList.size() > 0) {
            enterpriseCvCount = enterpriseCvList.size();
            for (EnterpriseCv ec : enterpriseCvList) {
                if (ec.getStatus().equals(EnterpriseCvStateEnum.PASS.getState())
                        || ec.getStatus().equals(EnterpriseCvStateEnum.DELETE.getState())) {
                    passCvitaeCount++;
                }
            }
        }
        map.put(CvitaeClient.CVITAE_COUNT_KEY, cvitaeCount);
        map.put(CvitaeClient.ENTERPRISE_CV_COUNT_KEY, enterpriseCvCount);
        map.put(CvitaeClient.PASS_CVITAE_COUNT_KEY, passCvitaeCount);
        return map;
    }
}
