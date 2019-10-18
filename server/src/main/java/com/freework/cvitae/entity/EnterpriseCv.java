package com.freework.cvitae.entity;


import java.util.Date;

/**
 * @author daihongru
 */
public class EnterpriseCv {
    /**
     * 简历投递记录编号，自增主键
     */
    private Integer enterpriseCvId;

    /**
     * 投递的企业ID
     */
    private Integer enterpriseId;

    /**
     * 投递的简历ID
     */
    private Integer curriculumVitaeId;

    /**
     * 投递状态，-1退回，0投递中，1通过，
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date lastEditTime;

    public Integer getEnterpriseCvId() {
        return enterpriseCvId;
    }

    public void setEnterpriseCvId(Integer enterpriseCvId) {
        this.enterpriseCvId = enterpriseCvId;
    }

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Integer getCurriculumVitaeId() {
        return curriculumVitaeId;
    }

    public void setCurriculumVitaeId(Integer curriculumVitaeId) {
        this.curriculumVitaeId = curriculumVitaeId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }
}
