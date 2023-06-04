package com.atguigu.auth.service;

import com.atguigu.model.system.SysMenu;
import com.atguigu.vo.system.AssginMenuVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-03
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    boolean removeMenuById(Long id);

    List<SysMenu> getMenuByRoleId(Long id);

    void assignMenuByRole(AssginMenuVo assginMenuVo);

    List<RouterVo> findMenusByUserId(Long userId);

    List<String> findPerByUserId(Long userId);
}
