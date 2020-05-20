package com.df.microservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.df.microservice.dao.EmailVerifyDao;
import com.df.microservice.domain.EmailVerify;
import com.df.microservice.service.EmailVerifyService;
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
    public int queryVerifyCodeCount(JSONObject params) throws Exception {
        return emailVerifyDao.queryCodeCount(params);
    }

    @Override
    public EmailVerify queryVerifyData(JSONObject params) throws Exception {
        return emailVerifyDao.queryVerifyData(params);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateVerifyData(JSONObject params) throws Exception {
        return emailVerifyDao.updateVerifyData(params);
    }

    @Override
    public void testInsert() {

    }
}
