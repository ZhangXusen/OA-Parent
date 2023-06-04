package com.atguigu.auth.controller;


import com.atguigu.auth.service.SysRoleService;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUserRole;
import com.atguigu.result.Result;
import com.atguigu.vo.system.AssginRoleVo;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("查询所有角色")
    @GetMapping("/findAll")
    public Result getAll() {
        List<SysRole> sysRoleList = sysRoleService.list();
        return Result.ok(sysRoleList);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("分页查询角色")
    @GetMapping("/{pageNum}/{pageSize}")
    public Result getPage(
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize,
            SysRoleQueryVo Query
    ) {
        System.out.println("1111111111111111111111111111111");
        //1.创建page对象
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        //包装like查询
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        String roleName = Query.getRoleName();
        //roleName判空
        if (!StringUtils.isEmpty(roleName)) {
            queryWrapper.like(SysRole::getRoleName, roleName);
        }
        //调用方法
        IPage<SysRole> rolePage = sysRoleService.page(page, queryWrapper);
        return Result.ok(rolePage);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加角色")
    @PostMapping("/save")
    public Result Save(@RequestBody SysRole sysRole) {
        boolean isSuccess = sysRoleService.save(sysRole);
        if (isSuccess) {
            return Result.ok();
        } else return Result.fail();
    }


    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("根据id查询")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable("id") Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        if (sysRole != null) {
            return Result.ok(sysRole);
        } else {
            return Result.fail();
        }
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("修改角色")
    @PutMapping("/update")
    public Result Update(@RequestBody SysRole sysRole) {
        boolean isSuccess = sysRoleService.updateById(sysRole);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }

    }

    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation(value = "删除角色")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean isSuccess = sysRoleService.removeById(id);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean isSuccess = sysRoleService.removeByIds(idList);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("查询用户角色")
    @GetMapping("/assign/{userId}")
    public Result getRoleByUserId(
            @PathVariable("userId") Long userId
    ) {
        Map<String, Object> roleMap = sysRoleService.findRoleByUserId(userId);
        return Result.ok(roleMap);
    }

    @ApiOperation("给用户分配角色")
    @PostMapping("/assign")
    public Result Assign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.assign(assginRoleVo);
        return Result.ok();
    }

}
