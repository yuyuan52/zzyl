package com.zzyl.common.ai;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.model.chat.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AIModelInvoker {

    @Autowired
    private BaiduAIProperties baiduAIProperties;

    /**
     * 调用千帆大模型
     * @param prompt
     */
    public String qianfanInvoker(String prompt) {

        System.out.println("prompt:"+prompt);

        // 使用安全认证AK/SK鉴权，替换下列示例中参数，安全认证Access Key替换your_iam_ak，Secret Key替换your_iam_sk
        Qianfan qianfan = new Qianfan(Auth.TYPE_OAUTH,baiduAIProperties.getAccessKey(), baiduAIProperties.getSecretKey());

        // 指定模型
        ChatResponse resp = qianfan.chatCompletion()
                .model(baiduAIProperties.getQianfanModel())
                .addMessage("user", prompt)
//                .temperature(0.7)
                .maxOutputTokens(2000)
                .responseFormat("json_object")
                .execute();
        System.out.println(resp.getResult());
        return resp.getResult();
    }
}
