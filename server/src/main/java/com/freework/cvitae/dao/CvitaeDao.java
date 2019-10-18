package com.freework.cvitae.dao;

import com.freework.cvitae.entity.Cvitae;

import java.util.List;

/**
 * @author daihongru
 */
public interface CvitaeDao {
    /**
     * 添加一条新的纪录
     *
     * @param cvitae
     * @return
     */
    int insert(Cvitae cvitae);

    /**
     * 根据用户ID查询简历
     *
     * @param userId
     * @return
     */
    List<Cvitae> queryByUserId(Integer userId);
}
