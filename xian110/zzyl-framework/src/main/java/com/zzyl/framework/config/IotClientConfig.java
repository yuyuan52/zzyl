package com.zzyl.framework.config;

import com.aliyun.iot20180120.Client;
import com.aliyun.teaopenapi.models.Config;
import com.zzyl.framework.config.properties.AliIoTConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IotClientConfig {

    @Autowired
    private AliIoTConfigProperties aliIoTConfigProperties;

    @Bean
    public Client instance() throws Exception {
        //参数
        Config config = new Config();
        config.setAccessKeyId(aliIoTConfigProperties.getAccessKeyId());
        config.setAccessKeySecret(aliIoTConfigProperties.getAccessKeySecret());
        //区域
        config.setRegionId(aliIoTConfigProperties.getRegionId());

        return new Client(config);
    }
}
