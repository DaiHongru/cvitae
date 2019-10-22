package com.freework.cvitae.controller;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.cvitae.entity.EnterpriseCv;
import com.freework.cvitae.service.CvitaeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author daihongru
 */
@RestController
public class CvitaeController {
    @Autowired
    private CvitaeService cvitaeService;

    /**
     * 查询当前用户的简历信息
     *
     * @param request
     * @return
     */
    @GetMapping(value = "current/cvitae")
    public ResultVo queryCvitae(HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return cvitaeService.queryCvitae(token);
    }

    /**
     * 查询当前用户的简历投递信息
     *
     * @param request
     * @return
     */
    @GetMapping(value = "current/delivery")
    public ResultVo queryDelivery(HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return cvitaeService.queryDelivery(token);
    }

    /**
     * 上传简历到当前登录的用户
     *
     * @param request
     * @return
     */
    @PostMapping(value = "current/cvitae")
    public ResultVo cvitaeUpload(MultipartHttpServletRequest request) {
        MultipartFile cvitae = request.getFile("cvitae");
        String token = request.getHeader("utoken");
        return cvitaeService.cvitaeUpload(cvitae, token);
    }

    /**
     * 用户简历下载
     *
     * @param response
     * @param request
     * @param token
     * @param curriculumVitaeId
     */
    @GetMapping(value = "current/download/{curriculumVitaeId}/{token}")
    public void cvitaeDownload(HttpServletResponse response, HttpServletRequest request,
                               @PathVariable String token, @PathVariable Integer curriculumVitaeId) {
        cvitaeService.cvitaeDownload(curriculumVitaeId, token, response, request);
    }

    /**
     * 简历投递
     *
     * @param enterpriseCv
     * @param request
     */
    @PostMapping(value = "current/apply")
    public ResultVo applyByVocation(@RequestBody EnterpriseCv enterpriseCv, HttpServletRequest request) {
        String token = request.getHeader("utoken");
        return cvitaeService.applyByVocation(enterpriseCv, token);
    }

    /**
     * 查询当前企业收到的简历
     *
     * @param enterpriseCv
     * @param request
     */
    @PostMapping(value = "enterprise/enterprisecv")
    public ResultVo queryEnterpriseCv(@RequestBody EnterpriseCv enterpriseCv, HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return cvitaeService.queryEnterpriseCv(enterpriseCv, token);
    }

    /**
     * 企业简历下载
     *
     * @param response
     * @param request
     * @param token
     * @param enterpriseCvId
     */
    @GetMapping(value = "enterprise/download/{enterpriseCvId}/{token}")
    public void enterpriseCvitaeDownload(HttpServletResponse response, HttpServletRequest request,
                                         @PathVariable String token, @PathVariable Integer enterpriseCvId) {
        cvitaeService.enterpriseCvitaeDownload(enterpriseCvId, token, response, request);
    }

    @PutMapping(value = "enterprise/enterprisecv")
    public ResultVo updateEnterpriseCv(@RequestBody EnterpriseCv enterpriseCv, HttpServletRequest request) {
        String token = request.getHeader("etoken");
        return cvitaeService.updateEnterpriseCv(enterpriseCv, token);
    }
}
