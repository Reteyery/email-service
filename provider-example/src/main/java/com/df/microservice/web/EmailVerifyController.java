package com.df.microservice.web;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSONObject;
import com.df.microservice.service.EmailVerifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by forezp on 2019/5/11.
 */

@RestController
@RequestMapping("/vertify")
public class EmailVerifyController {

    Logger logger= LoggerFactory.getLogger(EmailVerifyController.class);

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    private EmailVerifyService verifyService;

    @GetMapping("/services")
    public String getServices(){
        List<String> serviceNames=discoveryClient.getServices();

        StringBuilder stringBuilder=new StringBuilder();
        for (String s: serviceNames){
            stringBuilder.append(s).append("\n");
            List<ServiceInstance> serviceInstances=discoveryClient.getInstances(s);
            if(serviceInstances!=null&&serviceInstances.size()>0){
                for (ServiceInstance serviceInstance: serviceInstances){
                    logger.info("serviceName:"+s+" host:"+serviceInstance.getHost()+" port:"+serviceInstance.getPort());
                }
            }
        }
        return stringBuilder.toString();
    }

    @PostMapping("/sendEmail")
    public JSONObject sendEmail(@RequestBody JSONObject params) throws Exception{
        String userAccount, app_name, send_time;
        userAccount = params.getString("user_account");
        verifyService.

        return verifyService.queryVerifyCodeCount(params);
    }


//    // @SentinelResource("echo")
//    @RequestMapping(value = "/echo/{string}", method = RequestMethod.GET)
//    public String echo(@PathVariable String string) {
//        return "Hello Nacos Discovery " + string;
//    }
//
//    @SentinelResource(value = "echo1")
//    @RequestMapping(value = "/echo1/{string}", method = RequestMethod.GET)
//    public String echo1(@PathVariable String string) {
//        System.out.println("provider:------------");
//        return "Hello Nacos Discovery1111 " + string;
//    }
//
//    @SentinelResource("echo2")
//    @RequestMapping(value = "/echo2/{name}", method = RequestMethod.GET)
//    public String echo2(@PathVariable("name") String name) {
//        return "Hello Nacos Discovery2222222 " + name;
//    }
//
//    @GetMapping("/testInsert")
//    public void testInsert(@RequestParam(value = "name",defaultValue = "forezp",required = true)String name){
//        verificationService.testInsert();
//    }
}