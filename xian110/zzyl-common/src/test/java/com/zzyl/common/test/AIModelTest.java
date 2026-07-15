package com.zzyl.common.test;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.model.chat.ChatResponse;

public class AIModelTest {


    public static void main(String[] args) {
        // 使用安全认证AK/SK鉴权，替换下列示例中参数，安全认证Access Key替换your_iam_ak，Secret Key替换your_iam_sk
        Qianfan qianfan = new Qianfan(Auth.TYPE_OAUTH,"xEO9h4csw91eUrzWlUiYpkNt", "T67Xuyx9JoAWt1UEACQcFCVkd2HnZuKH");

        // 指定模型
        ChatResponse resp = qianfan.chatCompletion()
                .model("ERNIE-4.0-8K-Preview")
                .addMessage("user", "帮我创建一个user用户的数据，以json格式返回")
                .temperature(0.7)
                .maxOutputTokens(2000)
                .responseFormat("json_object")
                .execute();
        System.out.println(resp.getResult());
    }
}
