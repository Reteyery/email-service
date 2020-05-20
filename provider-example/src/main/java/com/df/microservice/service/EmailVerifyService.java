package com.df.microservice.service;

import com.alibaba.fastjson.JSONObject;
import com.df.microservice.domain.EmailVerify;

/**
 * @author tree
 * @create 2020-05-13 9:55
 */
public interface EmailVerifyService {

    //当天已发送验证码次数
    int queryVerifyCodeCount(JSONObject params) throws Exception;

    //查询一条验证码记录
    EmailVerify queryVerifyData(JSONObject params) throws Exception;

    //更新验证码记录表
    int updateVerifyData(JSONObject params) throws Exception;

}
