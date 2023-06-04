package com.atguigu.auth.service;

import com.atguigu.model.system.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-02
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatusById(Long id, Integer status);

    SysUser loadUserByUsername(String username);

    Map<String, Object> getUerInfo();
}
