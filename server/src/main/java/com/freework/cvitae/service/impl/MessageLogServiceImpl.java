package com.freework.cvitae.service.impl;

import com.freework.common.loadon.cache.JedisUtil;
import com.freework.common.loadon.util.DateUtil;
import com.freework.common.loadon.util.JsonUtil;
import com.freework.cvitae.dao.MessageLogDao;
import com.freework.cvitae.entity.MessageLog;
import com.freework.cvitae.producer.EmailSender;
import com.freework.cvitae.service.MessageLogService;
import com.freework.notify.client.vo.EmailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author daihongru
 */
@Service
public class MessageLogServiceImpl implements MessageLogService {
    private static Logger logger = LoggerFactory.getLogger(MessageLogServiceImpl.class);
    @Autowired(required = false)
    private MessageLogDao messageLogDao;
    @Autowired(required = false)
    private JedisUtil.Keys jedisKeys;
    @Autowired(required = false)
    private JedisUtil.Strings jedisStrings;
    @Autowired
    private EmailSender emailSender;

    @Override
    public void persistence(String messageLogKey, Integer status) {
        String jsonString = jedisStrings.get(messageLogKey);
        MessageLog messageLog = new MessageLog();
        try {
            messageLog = JsonUtil.jsonToObject(jsonString, MessageLog.class);
        } catch (Exception e) {
            logger.error("将JSON转为MessageLog对象时异常：" + e.getMessage());
        }
        messageLog.setStatus(status);
        messageLogDao.insertMessageLog(messageLog);
        jedisKeys.del(messageLogKey);
    }

    @Override
    public void resendEmail(String messageLogKey, MessageLog messageLog) {
        messageLog.setTryCount(messageLog.getTryCount() + 1);
        messageLog.setLastEditTime(new Date());
        messageLog.setNextRetryTime(DateUtil.getLaterTimeMinute(1));
        String newJsonString = JsonUtil.objectToJson(messageLog);
        jedisStrings.set(messageLogKey, newJsonString);
        EmailVo emailVo = JsonUtil.jsonToObject(messageLog.getMessage(), EmailVo.class);
        emailSender.send(emailVo);
    }
}
