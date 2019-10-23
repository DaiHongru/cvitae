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
     * 根据条件查询简历
     *
     * @param cvitae
     * @return
     */
    List<Cvitae> queryByRequirement(Cvitae cvitae);

    /**
     * 删除记录
     *
     * @param cvitae
     * @return
     */
    int delete(Cvitae cvitae);
}
