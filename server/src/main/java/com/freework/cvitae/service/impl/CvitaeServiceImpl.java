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

import java.io.File;
import java.io.IOException;
import java.util.Date;

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

    /**
     * 获取当前登录企业
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
