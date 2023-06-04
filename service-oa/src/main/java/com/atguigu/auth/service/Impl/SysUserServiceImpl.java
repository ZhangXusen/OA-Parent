package com.atguigu.auth.service.Impl;

import com.atguigu.auth.mapper.SysUserMapper;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.custom.LoginUserInfoHelper;
import com.atguigu.model.system.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-02
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public void updateStatusById(Long id, Integer status) {
        SysUser user = baseMapper.selectById(id);
        user.setStatus(status);
        baseMapper.updateById(user);
    }

    @Override
    public SysUser loadUserByUsername(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public Map<String, Object> getUerInfo() {
        SysUser user = baseMapper.selectById(LoginUserInfoHelper.getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put("phone", user.getPhone());
        map.put("name", user.getName());
        return map;
    }
}
