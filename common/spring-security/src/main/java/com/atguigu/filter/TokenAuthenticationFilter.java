package com.atguigu.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.atguigu.custom.LoginUserInfoHelper;
import com.atguigu.jwt.JwtHelper;
import com.atguigu.result.ResponseUtil;
import com.atguigu.result.Result;
import com.atguigu.result.ResultCodeEnum;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * 认证token
 * */
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        logger.info("uri:" + request.getRequestURI());
        //如果是登录接口，直接放行
        if ("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        //用户对象存在则通过并放入上下文对象
        if (null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.PERMISSION));
        }
    }

    //获取当前登录的用户对象
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // header是否有token
        String token = request.getHeader("token");
        logger.info("token:" + token);

        //解密token,获取username
        if (!StringUtils.isEmpty(token)) {
            String username = JwtHelper.getUsername(token);
            Long userId = JwtHelper.getUserId(token);
            logger.info("username:" + username);
            //如果username存在
            if (!StringUtils.isEmpty(username)) {
                //将用户信息放入进程中
                LoginUserInfoHelper.setUserId(userId);
                LoginUserInfoHelper.setUsername(username);
                //从redis获取权限列表字符串
                String authoritiesString = (String) redisTemplate.opsForValue().get(username);
                //redis里的权限列表不为空
                if (!StringUtils.isEmpty(authoritiesString)) {
                    List<Map> mapList = JSON.parseArray(authoritiesString, Map.class);
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    for (Map map : mapList) {
                        authorities.add(new SimpleGrantedAuthority((String) map.get("authority")));
                    }
                    System.out.println("authority" + authorities);
                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
                }
                return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            } else {
                return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            }
        }
        return null;
    }
}
