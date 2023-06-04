package com.atguigu.auth.service.Impl;

import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.custom.CustomUser;
import com.atguigu.custom.UserDetailService;
import com.atguigu.model.system.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailService {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser User = sysUserService.loadUserByUsername(username);
        if (null == User) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if (User.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }
        //权限列表
        List<String> PerList = sysMenuService.findPerByUserId(User.getId());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String per : PerList) {
            authorities.add(new SimpleGrantedAuthority(per.trim()));
        }
        return new CustomUser(User, authorities);
    }
}
