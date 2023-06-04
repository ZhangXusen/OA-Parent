package com.atguigu.auth.controller;


import com.atguigu.auth.service.SysMenuService;
import com.atguigu.model.system.SysMenu;
import com.atguigu.result.Result;
import com.atguigu.vo.system.AssginMenuVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-05-03
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    @ApiOperation("获取菜单")
    @GetMapping("/findAll")
    public Result getAll() {
        List<SysMenu> menus = sysMenuService.findNodes();
        return Result.ok(menus);
    }

    @ApiOperation("新增菜单")
    @PostMapping("/save")
    public Result Save(@RequestBody SysMenu sysMenu) {
        boolean isSuccess = sysMenuService.save(sysMenu);
        if (isSuccess) {
            return Result.ok();
        } else return Result.fail();
    }

    @ApiOperation("修改菜单")
    @PutMapping("/update")
    public Result Update(@RequestBody SysMenu sysMenu) {
        boolean isSuccess = sysMenuService.updateById(sysMenu);
        if (isSuccess) {
            return Result.ok();
        } else return Result.fail();
    }

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean isSuccess = sysMenuService.removeMenuById(id);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("获取角色菜单")
    @GetMapping("/assign/{roleId}")
    public Result getMenuByUserId(@PathVariable("roleId") Long id) {
        List<SysMenu> menus = sysMenuService.getMenuByRoleId(id);
        System.out.println(menus);
        return Result.ok(menus);
    }

    @ApiOperation("给角色分配菜单")
    @PostMapping("/assign")
    public Result AssignMenu(@RequestBody AssginMenuVo assginMenuVo){
        sysMenuService.assignMenuByRole(assginMenuVo);
        return Result.ok();
    }
}

