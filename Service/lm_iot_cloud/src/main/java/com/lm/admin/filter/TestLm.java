package com.lm.admin.filter;

import com.lm.admin.common.anno.login.IgnoreToken;
import com.lm.common.redis.adminkey.RedisAndHeaderKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 拦截器测试
 * @author Lm
 * @date 2022/11/22 9:41
 */
@Component
@Slf4j
public class TestLm implements HandlerInterceptor{
    //用于在将请求发送到控制器之前执行操作。此方法应返回true，以将响应返回给客户端。
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //通过自定义注解去跳过token校验
        // handler从object对象转换成具体的目标对象HandlerMethod
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 获取执行的方法
        Method method = handlerMethod.getMethod();
        if (
            method.getAnnotation(IgnoreToken.class) != null ||
            handlerMethod.getBeanType().getAnnotation(IgnoreToken.class) != null
        ) {
            return true;
        }
        // --------------跳过验证的注解--------------


        // 获取请求头的数据
        // 获取jwt
        String token_Jj = request.getHeader(RedisAndHeaderKey.HEADER_TOKEN_JJ);
        // 获取用户id
        String h_user_id = request.getHeader(RedisAndHeaderKey.HEADER_USER_ID);
        // 获取用户角色
        String h_user_role_code = request.getHeader(RedisAndHeaderKey.HEADER_USER_ROLE_CODE);

        log.info("jwt-->{} user_id-->{}  user_role_code-->{}",token_Jj,h_user_id,h_user_role_code);
        return true;
    }



    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //用完记得删除

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}