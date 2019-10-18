package com.freework.cvitae.controller;

import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.cvitae.service.CvitaeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;

/**
 * @author daihongru
 */
@RestController
public class CvitaeController {
    @Autowired
    private CvitaeService cvitaeService;

    @PostMapping(value = "current/cvitae")
    public ResultVo logoUpload(MultipartHttpServletRequest request) throws IOException {
        MultipartFile cvitae = request.getFile("cvitae");
        String token = request.getHeader("utoken");
        return cvitaeService.cvitaeUpload(cvitae, token);
    }
}
