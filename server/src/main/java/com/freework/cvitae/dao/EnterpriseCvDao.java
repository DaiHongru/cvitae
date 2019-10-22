package com.freework.cvitae.dao;

import com.freework.cvitae.entity.EnterpriseCv;

import java.util.List;

/**
 * @author daihongru
 */
public interface EnterpriseCvDao {
    /**
     * 根据条件查询简历投递情况
     *
     * @param enterpriseCv
     * @return
     */
    List<EnterpriseCv> queryByRequirement(EnterpriseCv enterpriseCv);

    /**
     * 添加一条新的纪录
     *
     * @param enterpriseCv
     * @return
     */
    int insert(EnterpriseCv enterpriseCv);

    /**
     * 更新记录
     *
     * @param enterpriseCv
     * @return
     */
    int update(EnterpriseCv enterpriseCv);

}
