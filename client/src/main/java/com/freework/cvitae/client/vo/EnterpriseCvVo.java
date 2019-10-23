package com.freework.cvitae.client.vo;


import java.util.Date;

/**
 * @author daihongru
 */
public class EnterpriseCvVo {
    /**
     * 简历投递记录编号，自增主键
     */
    private Integer enterpriseCvId;

    /**
     * 投递的企业ID
     */
    private Integer enterpriseId;

    /**
     * 存放地址
     */
    private String address;

    /**
     * 投递的简历ID
     */
    private Integer curriculumVitaeId;

    /**
     * 岗位ID
     */
    private Integer vocationId;

    /**
     * 岗位名称
     */
    private String vocationName;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户性别
     */
    private String userName;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户学历
     */
    private String education;

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

    public Integer getVocationId() {
        return vocationId;
    }

    public void setVocationId(Integer vocationId) {
        this.vocationId = vocationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVocationName() {
        return vocationName;
    }

    public void setVocationName(String vocationName) {
        this.vocationName = vocationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
