package com.df.microservice.dao;

import com.alibaba.fastjson.JSONObject;
import com.df.microservice.domain.EmailVerify;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liyang
 * @create 2020-05-13 9:56
 */
@Mapper
public interface EmailVerifyDao {

    /**
     * 查询
     * @return 当天已发送验证码次数
     */
    int queryCodeCount(EmailVerify params) throws Exception;

    /**
     * 查询
     * @return 校验验证码是否有效
     */
    EmailVerify queryVerifyData(EmailVerify params) throws Exception;

    /**
     * 新增
     * @return 插入一条记录
     */
    int addVerifyData(EmailVerify params) throws Exception;

    /**
     * 更新记录表
     * @return 1成功 0失败
     */
    int updateVerify(EmailVerify params) throws Exception;
}
