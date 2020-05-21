package com.df.microservice.web;

import com.alibaba.fastjson.JSONObject;
import com.df.microservice.domain.EmailVerify;
import com.df.microservice.service.EmailVerifyService;
import com.df.microservice.util.Constants;
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
        String userAccount, appName, dateTime, erroMsg;
        userAccount = params.getString("userAccount");
        appName = params.getString("appName");
        dateTime = params.getString("sendTime");
        if (userAccount.equals("") || appName.equals("") || dateTime.equals("")){
            result.put("info", "获取参数失败");
            result.put("data", false);
            return result;
        }
        JSONObject countParams = new JSONObject();
        countParams.put("userAccount", userAccount);
        countParams.put("appName", appName);
        countParams.put("sendTime", dateTime);
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
            String receiverName = obj2.getString("empName");
            objParams.put("mailtoid", "$" + obj2.getString("empId") + "$");
            objParams.put("mailto", receiverName);
            objParams.put("mailsubject", "东方信客密码修改");
            objParams.put("mailRTMessage", "1");
            objParams.put("mailcontenttype", "1");
            //此处固定了，可以考虑做成模板后台维护
            objParams.put("mailcontentText", "<div style=\"background:#fff;border:1px solid #ccc;margin:2%;padding:0 30px\">\r\n" +
                    "<div style=\"line-height:40px;height:40px\">&nbsp;</div>\r\n" +
                    "<p style=\"margin:0;padding:0;font-size:14px;line-height:30px;color:#333;font-family:arial,sans-serif;font-weight:bold\">亲爱的用户：</p>\r\n" +
                    "<div style=\"line-height:20px;height:20px\">&nbsp;</div>\r\n" +
                    "<p style=\"margin:0;padding:0;line-height:30px;font-size:14px;color:#333;font-family:'宋体',arial,sans-serif\">您好！感谢您使用东方信客服务，您正在进行邮箱验证，本次请求的验证码为：</p>\r\n" +
                    "<p style=\"margin:0;padding:0;line-height:30px;font-size:14px;color:#333;font-family:'宋体',arial,sans-serif\"><b style=\"font-size:18px;color:#f90\">"+verifyCode+"</b>" +
                    "<span style=\"margin:0;padding:0;margin-left:10px;line-height:30px;font-size:14px;color:#979797;font-family:'宋体',arial,sans-serif\">(为了保障您帐号的安全性，请在5分钟内完成验证。)</span></p>\r\n" +
                    "<div style=\"line-height:80px;height:80px\">&nbsp;</div>\r\n" +
                    "<p style=\"margin:0;padding:0;line-height:30px;text-align: right;font-size:14px;color:#333;font-family:'宋体',arial,sans-serif\">东方信客团队</p>\r\n" +
                    "<p style=\"margin:0;padding:0;line-height:30px;text-align: right;font-size:14px;color:#333;font-family:'宋体',arial,sans-serif\">"+mailSendTime+"</p>\r\n" +
                    "<div style=\"line-height:20px;height:20px\">&nbsp;</div>\r\n" +
                    "</div>");
            objParams.put("savetosended", "1");
            objParams.put("saveType", "0");
            str = tj.doPost(url, objParams);

            params.put("validCode", verifyCode);
            params.put("validPeriod", "5");
            params.put("isValid", 1);
            Date sendTime = new Date();
            SimpleDateFormat sendDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params.put("sendTime", sendDf.format(sendTime));
            params.put("appTeam", "东方信客团队");
            params.put("validTitle", "东方信客密码修改");
            params.put("userName", receiverName);
            //插入到一条记录
            verifyService.addVerifyData(params);
        } else {
            erroMsg = "验证次数超限，请明天再试";
            result.put("info", erroMsg);
            result.put("data", false);
        }
        result.put("data", true);
        return result;
    }

    //发送验证码功能
    @PostMapping("/verifyCode")
    public JSONObject verifyCode(@RequestBody JSONObject params) throws Exception {
        JSONObject result = new JSONObject();
        String erroMsg;
        /**
         * 获取表记录
         * params:userAccount/appName/sendTime
         */
        EmailVerify emailVerify = verifyService.queryVerifyData(params);
        if (emailVerify != null){
            int isValid = Integer.parseInt(emailVerify.getIsValid());
            //判断验证码是否有效
            if (isValid == 0){
                erroMsg = "验证码已失效";
                result.put("info", erroMsg);
                result.put("data", false);
                return result;
            }else {
                //判断验证码是否超时
                Date sendTime = emailVerify.getSendTime();
                Date nowDate = new Date();
                long between = nowDate.getTime() - sendTime.getTime();
                long min = between / (24 * 60 * 60);
                if (min > emailVerify.getValidPeriod()){
                    erroMsg = "验证码已失效";
                    result.put("info", erroMsg);
                    result.put("data", false);
                }else {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String nowTime = df.format(nowDate);
                    JSONObject param = new JSONObject();
                    param.put("validTime", nowTime);
                    param.put("validCode", emailVerify.getValidCode());
                    param.put("appName", emailVerify.getAppName());
                    param.put("userAccount", emailVerify.getUserAccount());
                    //更新isValid，置为0
                    param.put("isValid", 0);
                    verifyService.updateVerifyData(param);
                    result.put("info", "success");
                    result.put("data", true);
                }
            }
        }else {
            erroMsg = "验证码错误";
            result.put("info", erroMsg);
            result.put("data", false);
        }
        return result;
    }
}