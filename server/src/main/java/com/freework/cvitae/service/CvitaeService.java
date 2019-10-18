package com.freework.cvitae.service;

import com.freework.common.loadon.result.entity.ResultVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author daihongru
 */
@Service
public interface CvitaeService {
    /**
     * 简历上传
     *
     * @param cvitaeFile
     * @param token
     * @return
     */
    ResultVo cvitaeUpload(MultipartFile cvitaeFile, String token);
}
