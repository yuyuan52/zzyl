package com.zzyl.framework.interceptor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.framework.web.service.TokenService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MemberInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // //判断当前请求是否是handler()  controller中的一个方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //获取token
        String token = request.getHeader("authorization");
        if(StringUtils.isEmpty(token)){
            throw new BaseException("认证失败");
        }
        //解析token
        Claims claims = tokenService.parseToken(token);
        if(ObjectUtil.isEmpty(claims)){
            throw new BaseException("认证失败");
        }
        //获取用户数据
        Long userId = MapUtil.get(claims, "userId", Long.class);
        if(ObjectUtil.isEmpty(userId)){
            throw new BaseException("认证失败");
        }
        //存入到TheadLocal中
        UserThreadLocal.set(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}
