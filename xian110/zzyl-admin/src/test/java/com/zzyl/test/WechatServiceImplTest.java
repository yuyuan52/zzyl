package com.zzyl.test;

import com.zzyl.nursing.service.WechatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WechatServiceImplTest {

    @Autowired
    private WechatService wechatService;

    @Test
    public void testGetOpenid(){
        String openid = wechatService.getOpenid("0d3fPOkl2VLGbe4IACol2M0x5Q2fPOk5");
        System.out.println(openid);//o3CsK6_C6f4WP9b0AxXNJOkc6q9Q
    }

    @Test
    public void testGetPhone(){
        String phone = wechatService.getPhone("ff1a2f2e61482537ea19c8e57850919051b43ffadbf78cd4fcd3a1707e0c1e14");
        System.out.println(phone);
    }
}
