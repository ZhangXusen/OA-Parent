package com.atguigu.wechat.controller;


import com.atguigu.model.wechat.Menu;
import com.atguigu.result.Result;
import com.atguigu.vo.wechat.MenuVo;
import com.atguigu.wechat.service.WechatMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-05-13
 */
@Api(tags = "微信菜单")
@RestController
@RequestMapping("/admin/wechat/menu")
public class WechatMenuController {
    @Autowired
    private WechatMenuService wechatMenuService;

    @ApiOperation("获取菜单")
    @GetMapping("get/{id}")
    public Result GetMenu(@PathVariable Long id) {
        Menu menu = wechatMenuService.getById(id);
        return Result.ok(menu);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody Menu menu) {
        wechatMenuService.save(menu);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.menu.update')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody Menu menu) {
        wechatMenuService.updateById(menu);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.menu.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        wechatMenuService.removeById(id);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.menu.list')")
    @ApiOperation(value = "获取全部菜单")
    @GetMapping("findMenuInfo")
    public Result findMenuInfo() {
        List<MenuVo> menuInfo = wechatMenuService.findMenuInfo();
        return Result.ok();
    }

    @ApiOperation(value = "同步菜单")
    @GetMapping("syncMenu")
    public Result createMenu() {
        wechatMenuService.syncMenu();
        return Result.ok();
    }

}

