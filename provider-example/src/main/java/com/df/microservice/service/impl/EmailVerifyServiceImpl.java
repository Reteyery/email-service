package com.df.microservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.df.microservice.dao.EmailVerifyDao;
import com.df.microservice.service.EmailVerifyService;
import com.df.microservice.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author tree
 * @create 2020-05-13 9:55
 */
@Service
public class EmailVerifyServiceImpl implements EmailVerifyService {

    @Autowired
    EmailVerifyDao emailVerifyDao;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject queryVerifyCodeCount(JSONObject params) throws Exception {
        return CommonUtil.successJson(emailVerifyDao.queryCodeCount(params));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject isVerifyCodeValid(JSONObject params) throws Exception {
        return CommonUtil.successJson(emailVerifyDao.isVerifyCodeValid(params));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject isUserExist(JSONObject params) throws Exception {
        return CommonUtil.successJson(emailVerifyDao.isUserExist(params));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject updateOaEmailList(JSONObject params) throws Exception {
        return CommonUtil.successJson(emailVerifyDao.updateOaEmailList(params));
    }

    @Override
    public JSONObject addOaEmailData(JSONObject params) throws Exception {
        return CommonUtil.successJson(emailVerifyDao.addOaEmailData(params));
    }

    @Override
    public void testInsert() {

    }
}
