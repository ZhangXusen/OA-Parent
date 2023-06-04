package com.atguigu.wechat.service;

import com.atguigu.model.wechat.Menu;
import com.atguigu.vo.wechat.MenuVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-13
 */
public interface WechatMenuService extends IService<Menu> {

    List<MenuVo> findMenuInfo();

    void syncMenu();
}
