package com.df.microservice.web;

import com.alibaba.fastjson.JSONObject;
import com.df.microservice.domain.EmailVerify;
import com.df.microservice.service.EmailVerifyService;
import com.df.microservice.util.Constants;
import com.df.microservice.util.DateUtils;
import com.df.microservice.util.OaHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Liyang
 * @create 2020-05-19
 */
@RestController
@RequestMapping("/vertify")
public class EmailVerifyController {

    public final String TAG = this.getClass().getSimpleName();
    Logger logger = LoggerFactory.getLogger(EmailVerifyController.class);

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    private EmailVerifyService verifyService;

    @GetMapping("/services")
    public String getServices() {
        List<String> serviceNames = discoveryClient.getServices();

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : serviceNames) {
            stringBuilder.append(s).append("\n");
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(s);
            if (serviceInstances != null && serviceInstances.size() > 0) {
                for (ServiceInstance serviceInstance : serviceInstances) {
                    logger.info("serviceName:" + s + " host:" + serviceInstance.getHost() + " port:" + serviceInstance.getPort());
                }
            }
        }
        return stringBuilder.toString();
    }

    //发送验证码功能
    @PostMapping("/sendMail")
    public JSONObject sendEmail(@RequestBody JSONObject params) throws Exception {
        JSONObject result = new JSONObject();
        String userAccount, userName, appName, appTeam, validPeriod, validTitle, erroMsg;
        userAccount = params.getString("userAccount");
        userName = params.getString("userName");
        appName = params.getString("appName");
        appTeam = params.getString("appTeam");
        validPeriod = params.getString("validPeriod");
        validTitle = params.getString("validTitle");
        if (userAccount.equals("") || appName.equals("")){
            result.put("code", 500);
            result.put("msg", "参数不正确，操作失败");
            return result;
        }
        JSONObject countParams = new JSONObject();
        countParams.put("userAccount", userAccount);
        countParams.put("appName", appName);
        params.put("sendTime", DateUtils.dateTimeNow());
        //查询当天发送验证码次数
        int codeCount = verifyService.queryVerifyCodeCount(countParams);
        logger.debug(TAG, "code count ----- " + codeCount);
        if (codeCount < 10) {
            String adminAccount = "001287";
            String adminPassword = "123qwe";
            String verifyCode = String.format("%06d", (int) (Math.random() * 999999));//需要发送的验证码
            String domainAccount = "whir";//固定不变
            String url = Constants.urlBase + "/defaultroot/Logon!logon.action";
            Map<String, Object> objParams = new HashMap<>();
            objParams.put("userAccount", adminAccount);
            objParams.put("userPassword", adminPassword);
            objParams.put("domainAccount", domainAccount);
            OaHttpClient tj = OaHttpClient.getInstance();
            String str = tj.doPost(url, objParams);//先登录

            url = Constants.urlBase + "/defaultroot/SelectOrgAndUser!publicUserSearchAll.action";//查询收件人OA信息
            objParams = new HashMap<>();
            objParams.put("allowId", "mailtoid");
            objParams.put("single", "no");
            objParams.put("show", "userorggroup");
            objParams.put("range", "*0*");
            objParams.put("key", "id");
            objParams.put("showSidelineorg", "1");
            objParams.put("searchUserAccounts", userAccount);
            objParams.put("searchOrgId", "0");
            objParams.put("pageSize", "15");
            objParams.put("startPage", "1");
            objParams.put("pageCount", "1");
            objParams.put("recordCount", "0");
            str = tj.doPost(url, objParams);
            JSONObject obj = JSONObject.parseObject(str);
            JSONObject obj2 = obj.getJSONObject("data").getJSONArray("data").getJSONObject(0);
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
            String mailSendTime = df.format(new Date());
            url = Constants.urlBase + "/defaultroot/innerMail!sendMail.action";
            objParams = new HashMap<>();
            objParams.put("mailtoid", "$" + obj2.getString("empId") + "$");
            objParams.put("mailto", obj2.getString("empName"));
            objParams.put("mailsubject", validTitle);
            objParams.put("mailRTMessage", "1");
            objParams.put("mailcontenttype", "1");
            //此处固定了，可以考虑做成模板后台维护
            objParams.put("mailcontentText", "<div style=\"background:#fff;border:1px solid #ccc;margin:2%;padding: 30px;font-" +
                    "family: '宋体',arial,sans-serif;font-size:14px;color:#333;\">\n" +
                    "  <div style=\"line-height:80px;font-weight:bold;font-size:16px;\">亲爱的用户"+userName+"：</div>\n" +
                    "  <div style=\"line-height: 200px;text-indent: 2em;\">\n" +
                    "    <div style=\"line-height: 80px;\">您好！感谢您使用"+appName+"，您正在进行邮箱验证，本次请求的验证码为：</div>\n" +
                    "    <div style=\"line-height: 80px;\">\n" +
                    "      <b style=\"font-size:18px;color:#f90\">"+verifyCode+"</b>\n" +
                    "      <span style=\"margin-left:10px;line-height:30px;color:#979797;\">\n" +
                    "        (为了保障您帐号的安全性，请在<b style=\"font-size:16px;color:red;\">"+validPeriod+"</b>分钟内完成验证。)\n" +
                    "      </span>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "  <div style=\"text-align: right;font-weight: bold;\">\n" +
                    "     <div style=\"line-height: 30px;\">"+appTeam+"</div>\n" +
                    "     <div style=\"line-height: 30px;\">"+mailSendTime+"</div>\n");
            objParams.put("savetosended", "1");
            objParams.put("saveType", "0");
            str = tj.doPost(url, objParams);

            params.put("validCode", verifyCode);
            params.put("validPeriod", validPeriod);
            params.put("validTitle", validTitle);
            params.put("isValid", 1);
            params.put("sendTime", DateUtils.dateTimeNow());
            params.put("appTeam", appTeam);
            params.put("userName", userName);
            //插入到一条记录
            verifyService.addVerifyData(params);
            result.put("code", 200);
            result.put("msg", "操作成功");
        } else {
            erroMsg = "验证次数超限，请明天再试";
            result.put("code", 500);
            result.put("msg", erroMsg);
        }
        return result;
    }

    //发送验证码功能
    @PostMapping("/verifyCode")
    public JSONObject verifyCode(@RequestBody JSONObject params) throws Exception {
        JSONObject result = new JSONObject();
        /**
         * 获取表记录
         * params:userAccount/appName/sendTime
         */
        EmailVerify emailVerify = verifyService.queryVerifyData(params);
        if (emailVerify != null){
            int isValid = Integer.parseInt(emailVerify.getIsValid());
            //判断验证码是否有效
            if (isValid == 0){
                result.put("code", 500);
                result.put("msg", "验证码已失效");
                return result;
            }else {
                //判断验证码是否超时
                Date sendTime = emailVerify.getSendTime();
                Date nowDate = new Date();
                long between = nowDate.getTime() - sendTime.getTime();
                long min = between / (24 * 60 * 60);
                if (min > emailVerify.getValidPeriod()){
                    result.put("code", 500);
                    result.put("msg", "验证码已失效");
                }else {
                    emailVerify.setIsValid("0");
                    Date date = new Date();
                    emailVerify.setValidTime(date);
                    //更新isValid，置为0
                    verifyService.updateVerify(emailVerify);
                    result.put("code", 200);
                    result.put("msg", "操作成功");
                }
            }
        }else {
            result.put("code", 500);
            result.put("msg", "参数错误，操作失败");
        }
        return result;
    }
}