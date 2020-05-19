package com.df.microservice.dao;

import com.alibaba.fastjson.JSONObject;
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
    JSONObject queryCodeCount(JSONObject params) throws Exception;

    /**
     * 查询
     * @return 校验验证码是否有效
     */
    JSONObject isVerifyCodeValid(JSONObject params) throws Exception;

    /**
     * 查询
     * @return 收件人是否存在
     */
    JSONObject isUserExist(JSONObject params) throws Exception;

    /**
     * 新增
     * @return 更新验证码记录表
     */
    JSONObject updateOaEmailList(JSONObject params) throws Exception;

    /**
     * 新增 test
     */
    JSONObject addOaEmailData(JSONObject params) throws Exception;
    void testInsert();
}
