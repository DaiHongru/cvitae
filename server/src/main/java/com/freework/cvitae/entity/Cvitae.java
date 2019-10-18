package com.freework.cvitae.entity;

import java.util.Date;

/**
 * @author daihongru
 */
public class Cvitae {
    /**
     * 简历编号，自增主键
     */
    private Integer curriculumVitaeId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 存放地址路径
     */
    private String address;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date lastEditTime;


    public Integer getCurriculumVitaeId() {
        return curriculumVitaeId;
    }

    public void setCurriculumVitaeId(Integer curriculumVitaeId) {
        this.curriculumVitaeId = curriculumVitaeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
