package com.atguigu.auth.controller;


import com.atguigu.auth.service.SysUserService;
import com.atguigu.md5.MD5;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUser;
import com.atguigu.result.Result;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.atguigu.vo.system.SysUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-05-02
 */
@RestController
@Api(tags = "用户管理")
@RequestMapping("/admin/system/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("查询所有用户")
    @GetMapping("/findAll")
    public Result getAll() {
        List<SysUser> sysUserList = sysUserService.list();
        return Result.ok(sysUserList);
    }

    @ApiOperation("条件分页查询")
    @GetMapping("/{pageNum}/{pageSize}")
    public Result getPage(@PathVariable("pageNum") Integer pageNum,
                          @PathVariable("pageSize") Integer pageSize,
                          SysUserQueryVo Query) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        String username = Query.getKeyword();
        String createTimeBegin = Query.getCreateTimeBegin();
        String createTimeEnd = Query.getCreateTimeEnd();
        if (!StringUtils.isEmpty(username)) {
            queryWrapper.like(SysUser::getUsername, username);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            queryWrapper.gt(SysUser::getCreateTime, createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            queryWrapper.lt(SysUser::getCreateTime, createTimeEnd);
        }
        IPage<SysUser> userPage = sysUserService.page(page, queryWrapper);
        return Result.ok(userPage);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable("id") Long id) {
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser != null) {
            return Result.ok(sysUser);
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("添加用户")
    @PostMapping("/save")
    public Result Save(@RequestBody SysUser sysUser) {
        String newPassword = MD5.encrypt(sysUser.getPassword());
        sysUser.setPassword(newPassword);
        boolean isSuccess = sysUserService.save(sysUser);
        if (isSuccess) {
            return Result.ok();
        } else return Result.fail();
    }

    @ApiOperation("修改用户")
    @PutMapping("/update")
    public Result Update(@RequestBody SysUser sysUser) {
        boolean isSuccess = sysUserService.updateById(sysUser);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean isSuccess = sysUserService.removeById(id);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean isSuccess = sysUserService.removeByIds(idList);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("更新用户状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        sysUserService.updateStatusById(id, status);
        return Result.ok();
    }
}

