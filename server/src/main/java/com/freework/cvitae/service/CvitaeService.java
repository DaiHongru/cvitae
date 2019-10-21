package com.freework.cvitae.service;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.cvitae.entity.EnterpriseCv;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * 简历下载
     *
     * @param curriculumVitaeId
     * @param token
     * @param response
     * @param request
     * @return
     */
    ResultVo cvitaeDownload(Integer curriculumVitaeId, String token, HttpServletResponse response, HttpServletRequest request);

    /**
     * 简历投递
     *
     * @param enterpriseCv
     * @param token
     * @return
     */
    ResultVo applyByVocation(EnterpriseCv enterpriseCv, String token);
}
