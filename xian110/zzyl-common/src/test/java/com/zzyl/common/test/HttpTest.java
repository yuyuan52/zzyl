package com.zzyl.common.test;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpTest {


    //发起get请求
    @Test
    public void testGet(){
        //调用百度的网址
        String url = "https://www.baidu.com";
        String result = HttpUtil.get(url);
        System.out.println(result);
    }


    //分页查询护理项目
    @Test
    public void testProjectList(){
        String url = "http://localhost:8080/serve/project/list";
        //参数
        Map<String,Object> params = new HashMap<>();
        params.put("pageNum",1);
        params.put("pageSize",5);

        String result = HttpUtil.get(url,params);
        System.out.println(result);
    }

    @Test
    public void testProjectList2(){
        String url = "http://localhost:8080/serve/project/list";
        //参数
        Map<String,Object> params = new HashMap<>();
        params.put("pageNum",1);
        params.put("pageSize",5);

        //发送请求
        HttpResponse response = HttpUtil.createRequest(Method.GET, url)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6ImU5YTc3ODdiLWZlZmYtNGQ5Ny04ZWUxLWQyODI0Yzc2MGY1ZCJ9.krGy4vDcEsY5cwUxWeGIGOPxu4K5vM09SgeppIy6miv00AIYPlqNNE27ZPDWOe-RfTjxFyiSiwnI7Wd9EHk5Xg")
                .form(params).execute();
        if(response.isOk()){
            System.out.println(response.body());
        }
    }

    @Test
    public void testPost(){
        String url = "http://localhost:8080/serve/project";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "护理项目测试");
        paramMap.put("orderNo", 1);
        paramMap.put("unit", "次");
        paramMap.put("price", 10.00);
        paramMap.put("image", "https://yjy-slwl-oss.oss-cn-hangzhou.aliyuncs.com/ae7cf766-fb7b-49ff-a73c-c86c25f280e1.png");
        paramMap.put("nursingRequirement", "无特殊要求");
        paramMap.put("status", 1);
        //新增护理项目
        String result = HttpUtil.post(url, JSONUtil.toJsonStr(paramMap));
        System.out.println(result);
    }

    @Test
    public void testPost2(){
        String url = "http://localhost:8080/serve/project";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "护理项目测试");
        paramMap.put("orderNo", 1);
        paramMap.put("unit", "次");
        paramMap.put("price", 10.00);
        paramMap.put("image", "https://yjy-slwl-oss.oss-cn-hangzhou.aliyuncs.com/ae7cf766-fb7b-49ff-a73c-c86c25f280e1.png");
        paramMap.put("nursingRequirement", "无特殊要求");
        paramMap.put("status", 1);
        //新增护理项目
        HttpResponse response = HttpUtil.createRequest(Method.POST, url)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjRmNDIzYzZhLTljN2QtNDA3OC1hYzIzLTY2ZWEzMTE2MTk2YiJ9.UJToPudzY7zwa_5LpeAmydtVW2OPngBd0bVETRg7iskBlQ2Yx0hmhVI4iotbi1IVX0E1W3rcmlvMf2RsinS7Xw")
                .body(JSONUtil.toJsonStr(paramMap)).execute();
        if(response.isOk()){
            System.out.println(response.body());
        }
    }
}
