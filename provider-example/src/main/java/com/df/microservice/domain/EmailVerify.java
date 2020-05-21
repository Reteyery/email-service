package com.df.microservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tree
 * @create 2020-05-14 17:38
 */
public class EmailVerify implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 唯一性自增id */
    private Long validId;

    /** 验证码标题 */
    private String validTitle;

    /** 验证码 */
    private String validCode;

    /** 验证码有效期(单位分钟，默认5min) */
    private Long validPeriod;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    /** 验证时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validTime;

    /** 是否有效，1有效，0失效。默认1有效。验证通过后只为0 */
    private String isValid;

    /** 发送验证码app名 */
    private String appName;

    /** 发送验证码app团队名称 */
    private String appTeam;

    /** 接收邮件的用户账号 */

    private String userAccount;

    /** 接收邮件的用户姓名 */
    private String userName;


    public Long getValidId() {
        return validId;
    }

    public void setValidId(Long validId) {
        this.validId = validId;
    }

    public String getValidTitle() {
        return validTitle;
    }

    public void setValidTitle(String validTitle) {
        this.validTitle = validTitle;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }

    public Long getValidPeriod() {
        return validPeriod;
    }

    public void setValidPeriod(Long validPeriod) {
        this.validPeriod = validPeriod;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppTeam() {
        return appTeam;
    }

    public void setAppTeam(String appTeam) {
        this.appTeam = appTeam;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "EmailVerify{" +
                "validId=" + validId +
                ", validTitle='" + validTitle + '\'' +
                ", validCode='" + validCode + '\'' +
                ", validPeriod=" + validPeriod +
                ", sendTime=" + sendTime +
                ", validTime=" + validTime +
                ", isValid='" + isValid + '\'' +
                ", appName='" + appName + '\'' +
                ", appTeam='" + appTeam + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
