package com.freework.cvitae.service.impl;

import com.freeowork.user.client.key.UserRedisKey;
import com.freeowork.user.client.vo.UserVo;
import com.freework.common.loadon.cache.JedisUtil;
import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.common.loadon.result.enums.ResultStatusEnum;
import com.freework.common.loadon.result.util.ResultUtil;
import com.freework.common.loadon.util.FileUtil;
import com.freework.common.loadon.util.JsonUtil;
import com.freework.common.loadon.util.PathUtil;
import com.freework.cvitae.client.vo.CvitaeVo;
import com.freework.cvitae.dao.CvitaeDao;
import com.freework.cvitae.entity.Cvitae;
import com.freework.cvitae.exceptions.CvitaeOperationException;
import com.freework.cvitae.service.CvitaeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author daihongru
 */
@Service
public class CvitaeServiceImpl implements CvitaeService {
    private static Logger logger = LoggerFactory.getLogger(CvitaeServiceImpl.class);
    @Autowired(required = false)
    private JedisUtil.Keys jedisKeys;
    @Autowired(required = false)
    private JedisUtil.Strings jedisStrings;
    @Autowired(required = false)
    private CvitaeDao cvitaeDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo cvitaeUpload(MultipartFile cvitaeFile, String token) {
        if (cvitaeFile == null || StringUtils.isEmpty(token)) {
            ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        UserVo userVo = getCurrentUserVo(userKey);
        String fileName = cvitaeFile.getOriginalFilename();
        String targetAddr = PathUtil.getCvitaePath(userVo.getUserId());
        FileUtil.mkdirPath(targetAddr);
        String path = targetAddr + fileName;
        File file = new File(PathUtil.getBasePath() + path);
        Cvitae cvitae = new Cvitae();
        cvitae.setUserId(userVo.getUserId());
        cvitae.setFileName(fileName);
        cvitae.setAddress("/localresources" + path);
        cvitae.setCreateTime(new Date());
        cvitae.setLastEditTime(new Date());
        try {
            int judgeNum = cvitaeDao.insert(cvitae);
            if (judgeNum <= 0) {
                logger.error("上传简历时储存文件路径失败");
                throw new CvitaeOperationException("上传简历时储存文件路径失败");
            }
        } catch (Exception e) {
            logger.error("上传简历时储存文件路径异常:" + e.getMessage());
            throw new CvitaeOperationException("上传简历时储存文件路径异常:" + e.getMessage());
        }
        try {
            cvitaeFile.transferTo(file);
        } catch (IOException e) {
            logger.error("上传简历时将文件存放到目标路径时异常：" + e);
            throw new CvitaeOperationException(e.getMessage());
        }
        return ResultUtil.success();
    }

    @Override
    public ResultVo cvitaeDownload(Integer curriculumVitaeId, String token, HttpServletResponse response,
                                   HttpServletRequest request) {
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        UserVo userVo = getCurrentUserVo(userKey);
        String path = null;
        String fileName = null;
        List<CvitaeVo> cvitaeVoList = userVo.getCvitaeVoList();
        for (CvitaeVo cvitaeVo : cvitaeVoList) {
            if (cvitaeVo.getCurriculumVitaeId().equals(curriculumVitaeId)) {
                path = cvitaeVo.getAddress();
                fileName = cvitaeVo.getFileName();
                break;
            }
        }
        if (path == null) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        StringBuffer stringBuffer = new StringBuffer(path);
        stringBuffer.delete(0, 15);
        path = PathUtil.getBasePath() + stringBuffer.toString();
        File file = new File(path);
        if (!file.exists()) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        String name = null;
        String headerKey = "User-Agent";
        String indexOfKey = "MSIE";
        if (request.getHeader(headerKey).toUpperCase().indexOf(indexOfKey) > 0) {
            try {
                name = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            try {
                name = new String(fileName.getBytes(), "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        response.setContentType("application/force-download; charset=utf-8");
        response.addHeader("Content-disposition", "attachment;fileName=\"" + name + "\"");
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer);
                i = bis.read(buffer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            bis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultUtil.success();
    }

    /**
     * 获取当前登录的用户
     *
     * @param key
     * @return
     */
    private UserVo getCurrentUserVo(String key) {
        String userStr = jedisStrings.get(key);
        UserVo userVo = JsonUtil.jsonToObject(userStr, UserVo.class);
        return userVo;
    }
}
