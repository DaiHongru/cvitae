package com.freework.cvitae.controller;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.cvitae.service.CvitaeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping(value = "current/cvitae")
    public ResultVo cvitaeUpload(MultipartHttpServletRequest request) {
        MultipartFile cvitae = request.getFile("cvitae");
        String token = request.getHeader("utoken");
        return cvitaeService.cvitaeUpload(cvitae, token);
    }

    @GetMapping(value = "current/download/{curriculumVitaeId}/{token}")
    public void cvitaeDownload(HttpServletResponse response, HttpServletRequest request,
                               @PathVariable String token, @PathVariable Integer curriculumVitaeId) {
        cvitaeService.cvitaeDownload(curriculumVitaeId, token, response, request);
    }
}
