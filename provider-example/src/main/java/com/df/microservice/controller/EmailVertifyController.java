package com.df.microservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.df.microservice.service.EmailVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liyang
 * @time 2020-5-15
 */
@RestController
@RequestMapping("/vertify")
public class EmailVertifyController {

    @Autowired
    private EmailVerifyService verifyService;

    //当天已发送验证码次数
    @PostMapping("/queryCodeCount")
    JSONObject queryVerifyCodeCount(@RequestBody JSONObject params) throws Exception{
        return verifyService.queryVerifyCodeCount(params);
    }

    //校验验证码是否有效
    @PostMapping("/queryVerifyCodeValid")
    JSONObject isVerifyCodeValid(@RequestBody JSONObject params) throws Exception{
        return verifyService.isVerifyCodeValid(params);
    }

    @GetMapping("/query")
    public Map testGet() {
        return new HashMap<String, String>(){{
            put("name", "springboot");
        }};
    }

    //查询收件人是否存在
    @PostMapping("/queryUserExist")
    JSONObject isUserExist(@RequestBody JSONObject params) throws Exception{
        return verifyService.isUserExist(params);
    }

    //更新验证码记录表
    @PostMapping("/updateOaEmailList")
    JSONObject updateOaEmailList(@RequestBody JSONObject params) throws Exception{
        return verifyService.updateOaEmailList(params);
    }

    //更新验证码记录表  test
    @PostMapping("/addOaEmailData")
    JSONObject addOaEmailData(@RequestBody JSONObject params) throws Exception{
        return verifyService.addOaEmailData(params);
    }
}
