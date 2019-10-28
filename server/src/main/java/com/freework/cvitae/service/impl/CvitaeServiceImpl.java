package com.freework.cvitae.service.impl;

import com.freework.common.loadon.cache.JedisUtil;
import com.freework.common.loadon.entity.News;
import com.freework.common.loadon.enums.NewsStateEnum;
import com.freework.common.loadon.result.entity.ResultVo;
import com.freework.common.loadon.result.enums.ResultStatusEnum;
import com.freework.common.loadon.result.util.ResultUtil;
import com.freework.common.loadon.util.DateUtil;
import com.freework.common.loadon.util.FileUtil;
import com.freework.common.loadon.util.JsonUtil;
import com.freework.common.loadon.util.PathUtil;
import com.freework.cvitae.client.vo.CvitaeVo;
import com.freework.cvitae.client.vo.EnterpriseCvVo;
import com.freework.cvitae.dao.CvitaeDao;
import com.freework.cvitae.dao.EnterpriseCvDao;
import com.freework.cvitae.dao.NewsDao;
import com.freework.cvitae.entity.Cvitae;
import com.freework.cvitae.entity.EnterpriseCv;
import com.freework.cvitae.entity.MessageLog;
import com.freework.cvitae.enums.EnterpriseCvStateEnum;
import com.freework.cvitae.enums.MessageLogStateEnum;
import com.freework.cvitae.exceptions.CvitaeOperationException;
import com.freework.cvitae.exceptions.EnterpriseCvOperationException;
import com.freework.cvitae.producer.EmailSender;
import com.freework.cvitae.service.CvitaeService;
import com.freework.cvitae.service.MessageLogService;
import com.freework.enterprise.client.feign.EnterpriseClient;
import com.freework.enterprise.client.key.EnterpriseRedisKey;
import com.freework.enterprise.client.vo.EnterpriseVo;
import com.freework.notify.client.vo.EmailVo;
import com.freework.user.client.feign.UserClient;
import com.freework.user.client.key.UserRedisKey;
import com.freework.user.client.vo.UserVo;
import com.freework.vocation.client.feign.VocationClient;
import com.freework.vocation.client.vo.VocationVo;
import com.github.pagehelper.PageHelper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired(required = false)
    private EnterpriseCvDao enterpriseCvDao;
    @Autowired(required = false)
    private NewsDao newsDao;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private UserClient userClient;
    @Autowired
    private VocationClient vocationClient;
    @Autowired
    private EnterpriseClient enterpriseClient;

    @Override
    public ResultVo queryCvitae(String token) {
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        UserVo userVo = getCurrentUserVo(userKey);
        Cvitae cvitae = new Cvitae();
        cvitae.setUserId(userVo.getUserId());
        List<Cvitae> cvitaeList = cvitaeDao.queryByRequirement(cvitae);
        if (cvitaeList == null || cvitaeList.size() <= 0) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        List<CvitaeVo> cvitaeVoList = cvitaeList.stream().map(e -> {
            CvitaeVo outPut = new CvitaeVo();
            BeanUtils.copyProperties(e, outPut);
            return outPut;
        }).collect(Collectors.toList());
        return ResultUtil.success(cvitaeVoList);
    }

    @Override
    public ResultVo queryDelivery(String token, Integer pageNum, Integer pageSize) {
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        if (pageNum != 0 || pageSize != 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        UserVo userVo = getCurrentUserVo(userKey);
        EnterpriseCv enterpriseCv = new EnterpriseCv();
        enterpriseCv.setUserId(userVo.getUserId());
        List<EnterpriseCv> enterpriseCvList = enterpriseCvDao.queryByRequirement(enterpriseCv);
        if (enterpriseCvList == null || enterpriseCvList.size() <= 0) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        List<EnterpriseCvVo> enterpriseCvVoList = enterpriseCvList.stream().map(e -> {
            EnterpriseCvVo outPut = new EnterpriseCvVo();
            BeanUtils.copyProperties(e, outPut);
            return outPut;
        }).collect(Collectors.toList());
        return ResultUtil.success(enterpriseCvVoList);
    }

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
                logger.error("上传简历时储存文件信息失败");
                throw new CvitaeOperationException("上传简历时储存文件信息失败");
            }
        } catch (Exception e) {
            logger.error("上传简历时储存文件路径异常:" + e.getMessage());
            throw new CvitaeOperationException("上传简历时储存文件路径异常:" + e.getMessage());
        }
        try {
            cvitaeFile.transferTo(file);
        } catch (IOException e) {
            logger.error("上传简历时将文件拷贝到目标路径时异常：" + e);
            throw new CvitaeOperationException(e.getMessage());
        }
        userVo.setCvitaeCount(userVo.getCvitaeCount() + 1);
        setCurrentUserVo(userVo, userKey);
        return ResultUtil.success();
    }

    @Override
    public ResultVo cvitaeDownload(Integer curriculumVitaeId, String token,
                                   HttpServletResponse response, HttpServletRequest request) {
        if (curriculumVitaeId == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        UserVo userVo = getCurrentUserVo(userKey);
        Cvitae cvitae = new Cvitae();
        cvitae.setCurriculumVitaeId(curriculumVitaeId);
        cvitae.setUserId(userVo.getUserId());
        List<Cvitae> cvitaeList = cvitaeDao.queryByRequirement(cvitae);
        if (cvitaeList == null || cvitaeList.size() <= 0) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        cvitae = cvitaeList.get(0);
        String path = cvitae.getAddress();
        String fileName = cvitae.getFileName();
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo applyByVocation(EnterpriseCv enterpriseCv, String token) {
        if (enterpriseCv == null || enterpriseCv.getCurriculumVitaeId() == null ||
                enterpriseCv.getEnterpriseId() == null || enterpriseCv.getVocationId() == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        Cvitae cvitae = new Cvitae();
        cvitae.setCurriculumVitaeId(enterpriseCv.getCurriculumVitaeId());
        cvitae.setUserId(enterpriseCv.getUserId());
        List<Cvitae> cvitaeList = cvitaeDao.queryByRequirement(cvitae);
        if (cvitaeList == null || cvitaeList.size() <= 0) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        String fileName = cvitaeList.get(0).getFileName();
        String srcPath = PathUtil.getCvitaePath(enterpriseCv.getUserId()) + fileName;
        String targetAddr = PathUtil.getEnterpriseCvitaePath(enterpriseCv.getEnterpriseId(),
                enterpriseCv.getVocationId(), enterpriseCv.getUserId());
        FileUtil.mkdirPath(targetAddr);
        String targetPath = targetAddr + fileName;
        enterpriseCv.setStatus(EnterpriseCvStateEnum.DELIVERY.getState());
        enterpriseCv.setAddress("/localresources" + targetPath);
        enterpriseCv.setCreateTime(new Date());
        enterpriseCv.setLastEditTime(new Date());
        try {
            int judgeNum = enterpriseCvDao.insert(enterpriseCv);
            if (judgeNum <= 0) {
                logger.error("投递简历时写数据库失败");
                throw new EnterpriseCvOperationException("投递简历时写数据库失败");
            }
        } catch (Exception e) {
            logger.error("投递简历时写数据库异常:" + e.getMessage());
            throw new EnterpriseCvOperationException("投递简历时写数据库异常:" + e.getMessage());
        }
        try {
            FileUtil.copyFile(srcPath, targetPath);
        } catch (Exception e) {
            throw new EnterpriseCvOperationException("投递简历时拷贝简历文件失败：" + e);
        }
        UserVo userVo = getCurrentUserVo(userKey);
        userVo.setDeliveryCvitaeCount(userVo.getDeliveryCvitaeCount() + 1);
        setCurrentUserVo(userVo, userKey);
        insertApplyNews(userKey, enterpriseCv.getEnterpriseId(), enterpriseCv.getVocationId());
        sendApplyNotifyEmail(enterpriseCv);
        return ResultUtil.success();
    }

    @Override
    public ResultVo deleteCvitae(Integer curriculumVitaeId, String token) {
        if (curriculumVitaeId == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String userKey = UserRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(userKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        UserVo userVo = getCurrentUserVo(userKey);
        Cvitae cvitae = new Cvitae();
        cvitae.setUserId(userVo.getUserId());
        cvitae.setCurriculumVitaeId(curriculumVitaeId);
        List<Cvitae> cvitaeList = cvitaeDao.queryByRequirement(cvitae);
        if (cvitaeList == null || cvitaeList.size() <= 0) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        try {
            int judgeNum = cvitaeDao.delete(cvitae);
            if (judgeNum <= 0) {
                logger.error("用户删除简历失败");
                throw new EnterpriseCvOperationException("用户删除简历失败");
            }
        } catch (Exception e) {
            logger.error("用户删除简历异常:" + e.getMessage());
            throw new EnterpriseCvOperationException("用户删除简历异常:" + e.getMessage());
        }
        String path = cvitaeList.get(0).getAddress();
        StringBuffer stringBuffer = new StringBuffer(path);
        stringBuffer.delete(0, 15);
        FileUtil.deleteFileOrPath(stringBuffer.toString());
        userVo.setCvitaeCount(userVo.getCvitaeCount() - 1);
        setCurrentUserVo(userVo, userKey);
        return ResultUtil.success();
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60")
    })
    public ResultVo queryEnterpriseCv(EnterpriseCv enterpriseCv, String token) {
        if (enterpriseCv == null) {
            ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        List<EnterpriseCv> enterpriseCvList = enterpriseCvDao.queryByRequirement(enterpriseCv);
        if (enterpriseCvList == null || enterpriseCvList.size() == 0) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        List<EnterpriseCvVo> enterpriseCvVoList = enterpriseCvList.stream().map(e -> {
            EnterpriseCvVo outPut = new EnterpriseCvVo();
            BeanUtils.copyProperties(e, outPut);
            UserVo userVo = userClient.getUserInfo(outPut.getUserId());
            VocationVo vocationVo = vocationClient.getVocationInfo(outPut.getVocationId());
            outPut.setSex(userVo.getSex());
            outPut.setUserName(userVo.getUserName());
            outPut.setEducation(userVo.getEducation());
            outPut.setVocationName(vocationVo.getVocationName());
            return outPut;
        }).collect(Collectors.toList());
        return ResultUtil.success(enterpriseCvVoList);
    }

    @Override
    public ResultVo enterpriseCvitaeDownload(Integer enterpriseCvId, String token,
                                             HttpServletResponse response, HttpServletRequest request) {
        if (enterpriseCvId == null) {
            return ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        EnterpriseCv enterpriseCv = new EnterpriseCv();
        enterpriseCv.setEnterpriseCvId(enterpriseCvId);
        enterpriseCv.setEnterpriseId(enterpriseVo.getEnterpriseId());
        List<EnterpriseCv> enterpriseCvList = enterpriseCvDao.queryByRequirement(enterpriseCv);
        if (enterpriseCvList == null || enterpriseCvList.size() <= 0) {
            return ResultUtil.error(ResultStatusEnum.NOT_FOUND);
        }
        enterpriseCv = enterpriseCvList.get(0);
        String path = enterpriseCv.getAddress();
        String fileName = path.substring(path.lastIndexOf("/") + 1);
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

    @Override
    public ResultVo updateEnterpriseCv(EnterpriseCv enterpriseCv, String token) {
        if (enterpriseCv == null || enterpriseCv.getStatus() == null || enterpriseCv.getEnterpriseCvId() == null) {
            ResultUtil.error(ResultStatusEnum.BAD_REQUEST);
        }
        String enterpriseKey = EnterpriseRedisKey.LOGIN_KEY + token;
        if (!jedisKeys.exists(enterpriseKey)) {
            return ResultUtil.error(ResultStatusEnum.UNAUTHORIZED);
        }
        EnterpriseVo enterpriseVo = getCurrentEnterpriseVo(enterpriseKey);
        enterpriseCv.setEnterpriseId(enterpriseVo.getEnterpriseId());
        enterpriseCv.setLastEditTime(new Date());
        try {
            int judgeNum = enterpriseCvDao.update(enterpriseCv);
            if (judgeNum <= 0) {
                logger.error("企业修改简历投递状态失败");
                throw new EnterpriseCvOperationException("企业修改简历投递状态失败");
            }
        } catch (Exception e) {
            logger.error("企业修改简历投递状态异常:" + e.getMessage());
            throw new EnterpriseCvOperationException("企业修改简历投递状态异常:" + e.getMessage());
        }
        if (!enterpriseCv.getStatus().equals(EnterpriseCvStateEnum.DELETE.getState())) {
            insertUpdateNews(enterpriseVo, enterpriseCv);
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

    /**
     * 设置当前登录的用户信息
     *
     * @param userVo
     * @param key
     */
    private void setCurrentUserVo(UserVo userVo, String key) {
        String userStr = JsonUtil.objectToJson(userVo);
        jedisStrings.set(key, userStr);
    }

    /**
     * 获取当前登录企业
     *
     * @param key
     * @return
     */
    private EnterpriseVo getCurrentEnterpriseVo(String key) {
        String enterpriseStr = jedisStrings.get(key);
        EnterpriseVo enterpriseVo = JsonUtil.jsonToObject(enterpriseStr, EnterpriseVo.class);
        return enterpriseVo;
    }

    /**
     * 设置当前登录的企业信息
     *
     * @param enterpriseVo
     * @param key
     */
    private void setCurrentEnterpriseVo(EnterpriseVo enterpriseVo, String key) {
        String enterpriseStr = JsonUtil.objectToJson(enterpriseVo);
        jedisStrings.set(key, enterpriseStr);
    }

    /**
     * 新建投递消息
     */
    @Async
    @HystrixCommand
    public void insertApplyNews(String userKey, Integer enterpriseId, Integer vocationId) {
        EnterpriseVo enterpriseVo = enterpriseClient.getEnterpriseById(enterpriseId);
        VocationVo vocationVo = vocationClient.getVocationInfo(vocationId);
        UserVo userVo = getCurrentUserVo(userKey);
        String content = "投递简历至 ["
                + vocationVo.getVocationName() + "]，["
                + enterpriseVo.getEnterpriseName() + "]";
        insertNews(NewsStateEnum.NEWS_TYPE_USER, userVo.getUserId(), content);
    }

    /**
     * 新建投递状态修改消息
     */
    @Async
    @HystrixCommand
    public void insertUpdateNews(EnterpriseVo enterpriseVo, EnterpriseCv enterpriseCv) {
        VocationVo vocationVo = vocationClient.getVocationInfo(enterpriseCv.getVocationId());
        String content = "恭喜您！您投递的 ["
                + vocationVo.getVocationName() + "]，["
                + enterpriseVo.getEnterpriseName() + "] 通过了，请耐心等待企业通知。（企业联系方式：" + enterpriseVo.getPhone() + "）";
        if (!enterpriseCv.getStatus().equals(EnterpriseCvStateEnum.PASS.getState())) {
            content = "很遗憾！您投递的 ["
                    + vocationVo.getVocationName() + "]，["
                    + enterpriseVo.getEnterpriseName() + "] 没有通过，再试试其他岗位吧。";
        }
        insertNews(NewsStateEnum.NEWS_TYPE_USER, enterpriseCv.getUserId(), content);
    }

    /**
     * 添加新的News
     *
     * @param type
     * @param ownerId
     * @param content
     */
    @Async
    public void insertNews(String type, Integer ownerId, String content) {
        News news = new News();
        news.setOwnerType(type);
        news.setOwnerId(ownerId);
        news.setStatus(NewsStateEnum.UNREAD.getState());
        news.setContent(content);
        news.setCreateTime(new Date());
        news.setLastEditTime(new Date());
        try {
            int judgeNum = newsDao.insert(news);
            if (judgeNum <= 0) {
                logger.error("创建新的News失败！news：" + news.toString());
            }
        } catch (Exception e) {
            logger.error("创建新的News异常");
            e.printStackTrace();
        }
    }

    @Async
    @HystrixCommand
    public void sendApplyNotifyEmail(EnterpriseCv enterpriseCv) {
        EnterpriseVo enterpriseVo = enterpriseClient.getEnterpriseById(enterpriseCv.getEnterpriseId());
        VocationVo vocationVo = vocationClient.getVocationInfo(enterpriseCv.getVocationId());
        EmailVo emailVo = new EmailVo();
        String htmlText = "<html lang=\"zh-CN\">\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<img src=\"http://101.132.152.64/img/logo.png\">\n" +
                "<h1 style=\"color: #2e6da4\">FreeWork<br />\n" +
                "    <hr style=\"width: 50%;margin-left: 0px;\">\n" +
                "    您发布的招聘岗位有了新的投递信息\n" +
                "</h1>\n" +
                "<p>尊敬的用户，您好！</p>\n" +
                "<p>您发布的岗位：<span style=\"color: #137bd6;\">" + vocationVo.getVocationName() + "</span> ，有了新的应聘信息。</p>\n" +
                "<p>前往企业中心即可查看详情</p>\n" +
                "<button style=\"width: 100px;height: 30px;\"\n" +
                "    onclick=\"window.open('http://101.132.152.64/html/enterprise/enterpriselogin.html', '_blank').location;\">企业中心</button>\n" +
                "<p>投递的简历已随本邮件发送，请查看附件。</p>\n" +
                "<p>感谢您的使用，谢谢！</p>\n" +
                "<p>FreeWork团队</p>\n" +
                "</html>";
        emailVo.setAddress(enterpriseVo.getEmail());
        emailVo.setHtmlText(htmlText);
        String urlHeader = "http://101.132.152.64";
        emailVo.setEnclosureUrl(urlHeader + enterpriseCv.getAddress());
        emailVo.autoSetMessageId();
        MessageLog messageLog = new MessageLog();
        messageLog.setTag("CvitaeEmail");
        messageLog.setMessageId(emailVo.getMessageId());
        messageLog.setMessage(JsonUtil.objectToJson(emailVo));
        messageLog.setTryCount(1);
        messageLog.setStatus(MessageLogStateEnum.SENDING.getState());
        messageLog.setNextRetryTime(DateUtil.getLaterTimeMinute(1));
        messageLog.setCreateTime(new Date());
        messageLog.setLastEditTime(new Date());
        String jsonString = JsonUtil.objectToJson(messageLog);
        String messageLogKey = MessageLogService.MESSAGELOG_EMAIL_KEY + "_" + messageLog.getMessageId();
        jedisStrings.set(messageLogKey, jsonString);
        emailSender.send(emailVo);
    }
}
