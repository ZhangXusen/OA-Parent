package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysRoleService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.jwt.JwtHelper;
import com.atguigu.md5.MD5;
import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysUser;
import com.atguigu.result.Result;
import com.atguigu.vo.system.LoginVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "首页管理")
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("密码登录")
    @PostMapping("/login")
    public Result Login(@RequestBody LoginVo loginVo) {
        String username = loginVo.getUsername();
        String password = loginVo.getPassword();
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserService.getOne(queryWrapper);
        if (user == null) {
            return Result.fail(null).message("用户不存在");
        }
        String userPassword = user.getPassword();
        if (!userPassword.equals(MD5.encrypt(password))) {

            return Result.fail(null).message("用户名或密码错误");
        }
        if (user.getStatus().intValue() == 0) {
            return Result.fail(null).message("用户被封禁");
        }
        String token = JwtHelper.createToken(user.getId(), user.getUsername());
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("token", token);
        return Result.ok(userInfo);

    }

    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader("header") String header) {
        String username = JwtHelper.getUsername(header);
        Long userId = JwtHelper.getUserId(header);
        //菜单
        List<RouterVo> menus = sysMenuService.findMenusByUserId(userId);
        //权限列表
        List<String> PerList = sysMenuService.findPerByUserId(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("roles", "[admin]");
        map.put("name", username);
        map.put("routes", menus);
        map.put("button", PerList);
        return Result.ok(map);
    }


    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result Logout() {
        return Result.ok();
    }
}
