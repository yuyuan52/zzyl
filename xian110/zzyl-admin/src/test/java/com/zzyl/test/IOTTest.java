package com.zzyl.test;

import cn.hutool.json.JSONUtil;
import com.aliyun.iot20180120.Client;
import com.aliyun.iot20180120.models.QueryProductListRequest;
import com.aliyun.iot20180120.models.QueryProductListResponse;
import com.zzyl.framework.config.properties.AliIoTConfigProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IOTTest {

    @Autowired
    private Client client;

    @Autowired
    private AliIoTConfigProperties aliIoTConfigProperties;

    @Test
    public void testQueryProductList() throws Exception {
        //设置参数
        QueryProductListRequest request = new QueryProductListRequest();
        request.setCurrentPage(1);
        request.setPageSize(10);
        request.setIotInstanceId(aliIoTConfigProperties.getIotInstanceId());
        //发送请求
        QueryProductListResponse response = client.queryProductList(request);
        //获取结果
        System.out.println(JSONUtil.toJsonStr(response.getBody().getData()));
    }
}
