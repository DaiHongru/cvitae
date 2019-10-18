package com.freework.cvitae.service.impl;


import com.freework.cvitae.client.vo.CvitaeVo;
import com.freework.cvitae.client.vo.EnterpriseCvVo;
import com.freework.cvitae.dao.CvitaeDao;
import com.freework.cvitae.dao.EnterpriseCvDao;
import com.freework.cvitae.entity.Cvitae;
import com.freework.cvitae.entity.EnterpriseCv;
import com.freework.cvitae.service.ClientService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<CvitaeVo> cvitaeVoList = null;
        List<EnterpriseCvVo> enterpriseCvVoList = null;
        List<Cvitae> cvitaeList = cvitaeDao.queryByUserId(userId);
        if (cvitaeList != null && cvitaeList.size() > 0) {
            cvitaeVoList = cvitaeList.stream().map(e -> {
                CvitaeVo outPut = new CvitaeVo();
                BeanUtils.copyProperties(e, outPut);
                return outPut;
            }).collect(Collectors.toList());
        }
        List<EnterpriseCv> enterpriseCvList = enterpriseCvDao.queryByUserId(userId);
        if (enterpriseCvList != null && enterpriseCvList.size() > 0) {
            enterpriseCvVoList = enterpriseCvList.stream().map(e -> {
                EnterpriseCvVo outPut = new EnterpriseCvVo();
                BeanUtils.copyProperties(e, outPut);
                return outPut;
            }).collect(Collectors.toList());
        }
        map.put("cvitaeVoList", cvitaeVoList);
        map.put("enterpriseCvVoList", enterpriseCvVoList);
        return map;
    }
}
