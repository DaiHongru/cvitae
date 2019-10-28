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
     * 查询当前用户的简历信息
     *
     * @param token
     * @return
     */
    ResultVo queryCvitae(String token);

    /**
     * 查询当前用户的简历投递信息
     *
     * @param token
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultVo queryDelivery(String token, Integer pageNum, Integer pageSize);

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

    /**
     * 用户删除简历
     *
     * @param curriculumVitaeId
     * @param token
     * @return
     */
    ResultVo deleteCvitae(Integer curriculumVitaeId, String token);

    /**
     * 查询当前企业收到的简历
     *
     * @param enterpriseCv
     * @param token
     * @return
     */
    ResultVo queryEnterpriseCv(EnterpriseCv enterpriseCv, String token);

    /**
     * 企业简历下载
     *
     * @param enterpriseCvId
     * @param token
     * @param response
     * @param request
     * @return
     */
    ResultVo enterpriseCvitaeDownload(Integer enterpriseCvId, String token, HttpServletResponse response, HttpServletRequest request);

    /**
     * 修改企业收到简历信息的状态
     *
     * @param enterpriseCv
     * @param token
     * @return
     */
    ResultVo updateEnterpriseCv(EnterpriseCv enterpriseCv, String token);
}
