package com.atguigu.auth.service.Impl;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.auth.service.SysRoleService;
import com.atguigu.auth.service.SysUserRoleService;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUserRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public Map<String, Object> findRoleByUserId(Long userId) {
        /*前端需要所有角色列表和该用户已经分配的角色*/
        //1.获取角色列表
        List<SysRole> roleList = baseMapper.selectList(null);
        //2.获取当前用户的角色
        //包装条件：=userId
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> existRoleList = sysUserRoleService.list(queryWrapper);
        List<Long> existRoleIdList = new ArrayList<>();//当前用户的RoleId列表
        //放入到RoleId列表中
        for (SysUserRole sysUserRole : existRoleList) {
            existRoleIdList.add(sysUserRole.getRoleId());
        }
        //根据已有的roleId列表，获取Role信息
        List<SysRole> AssignRoleList = new ArrayList<>();//存放Role信息的列表
        //放入
        for (SysRole sysRole : roleList) {
            if (existRoleIdList.contains(sysRole.getId())) {
                AssignRoleList.add(sysRole);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("allRolesList", roleList);
        map.put("assignRoleList", AssignRoleList);
        return map;
    }

    @Override
    public void assign(AssginRoleVo assginRoleVo) {
        Long userId = assginRoleVo.getUserId();
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        //删除改用户所有角色
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleService.remove(queryWrapper);
        //增加角色
        for (Long roleId : roleIdList) {
            if (StringUtils.isEmpty(roleId)) continue;
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            sysUserRoleService.save(userRole);
        }
    }
}
