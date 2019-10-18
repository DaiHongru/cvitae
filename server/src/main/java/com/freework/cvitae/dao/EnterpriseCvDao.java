package com.freework.cvitae.dao;

import com.freework.cvitae.entity.EnterpriseCv;

import java.util.List;

/**
 * @author daihongru
 */
public interface EnterpriseCvDao {
    /**
     * 根据用户ID查询简历投递情况
     *
     * @param userId
     * @return
     */
    List<EnterpriseCv> queryByUserId(Integer userId);
}
