package com.zzyl.nursing.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zzyl.nursing.service.WechatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WechatServiceImpl implements WechatService {

    // 登录
    private static final String REQUEST_URL = "https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code";

    // 获取token
    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    // 获取手机号
    private static final String PHONE_REQUEST_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.appSecret}")
    private String appSecret;


    /**
     * 获取openid
     * @param code
     * @return
     */
    @Override
    public String getOpenid(String code) {
        //接口参数
        Map<String,Object> params = getAppConfig();
        params.put("js_code",code);

        //调用请求
        String result = HttpUtil.get(REQUEST_URL, params);
        //转换为JSONObject对象，类似于map
        JSONObject jsonObject = JSONUtil.parseObj(result);
        //判断，接口是否调通了
        if(ObjectUtil.isNotEmpty(jsonObject.getInt("errcode"))){
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }
        //获取openid
        return jsonObject.getStr("openid");
    }

    private Map<String, Object> getAppConfig() {
        Map<String, Object> params = new HashMap<>();
        params.put("appid",appId);
        params.put("secret",appSecret);
        return params;
    }

    /**
     * 获取手机号
     * @param phoneCode
     * @return
     */
    @Override
    public String getPhone(String phoneCode) {

        //获取token
        String token  = getToken();
        String url = PHONE_REQUEST_URL + token;

        //封装参数
        Map<String, Object> params = new HashMap<>();
        params.put("code",phoneCode);

        //发起请求
        String result = HttpUtil.post(url, JSONUtil.toJsonStr(params));
        //转换为JSONObject对象，类似于map
        JSONObject jsonObject = JSONUtil.parseObj(result);
        //判断，接口是否调通了
        if(jsonObject.getInt("errcode") != 0){
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }
        //获取手机号
        return jsonObject.getJSONObject("phone_info").getStr("phoneNumber");
    }

    /**
     * 获取token
     * @return
     */
    private String getToken() {
        //获取参数
        Map<String, Object> appConfig = getAppConfig();
        //发起调用
        String result = HttpUtil.get(TOKEN_URL, appConfig);
        //转换为JSONObject对象，类似于map
        JSONObject jsonObject = JSONUtil.parseObj(result);
        //判断，接口是否调通了
        if(ObjectUtil.isNotEmpty(jsonObject.getInt("errcode"))){
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }
        //获取token
        return jsonObject.getStr("access_token");

    }
}
