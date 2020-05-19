package com.df.microservice.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author tree
 * @create 2020-05-13 9:55
 */
public interface EmailVerifyService {

    //当天已发送验证码次数
    JSONObject queryVerifyCodeCount(JSONObject params) throws Exception;

    //校验验证码是否有效
    JSONObject isVerifyCodeValid(JSONObject params) throws Exception;

    //查询收件人是否存在
    JSONObject isUserExist(JSONObject params) throws Exception;

    //更新验证码记录表
    JSONObject updateOaEmailList(JSONObject params) throws Exception;

    //新增一条记录 test
    JSONObject addOaEmailData(JSONObject params) throws Exception;
    void testInsert();
}
