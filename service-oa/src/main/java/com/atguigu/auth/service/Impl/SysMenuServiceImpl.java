package com.atguigu.auth.service.Impl;

import com.atguigu.auth.mapper.SysMenuMapper;
import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysRoleMenuService;
import com.atguigu.auth.util.MenuHelper;
import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysRoleMenu;
import com.atguigu.vo.system.AssginMenuVo;
import com.atguigu.vo.system.MetaVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-03
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    //获取所有菜单
    @Override
    public List<SysMenu> findNodes() {
        //所有菜单数据
        List<SysMenu> menuList = baseMapper.selectList(null);
        //构建成树形结构
        return MenuHelper.buildTree(menuList);
    }

    @Override
    public boolean removeMenuById(Long id) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId, id);
        //获取该id下子菜单个数
        Integer count = baseMapper.selectCount(queryWrapper);
        //有子菜单则不删
        if (count > 1) {
            return false;
        } else {
            baseMapper.deleteById(id);
            return true;
        }
    }

    @Override
    public List<SysMenu> getMenuByRoleId(Long id) {
        //1.查询所有菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        List<SysMenu> menuList = baseMapper.selectList(wrapper);
        //2.根据roleId获取menuId列表
        LambdaQueryWrapper<SysRoleMenu> RoleMenusWrapper = new LambdaQueryWrapper<>();
        RoleMenusWrapper.eq(SysRoleMenu::getRoleId, id);
        List<SysRoleMenu> roleMenuList = sysRoleMenuService.list(RoleMenusWrapper);
        List<Long> menuIdList = new ArrayList<>();
        for (SysRoleMenu roleMenu : roleMenuList) {
            menuIdList.add(roleMenu.getMenuId());
        }
        //List<SysMenu> MenusListByRoleId = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menuIdList.contains(menu.getId())) {
                menu.setSelect(true);
            } else {
                menu.setSelect(false);
            }
        }
        return MenuHelper.buildTree(menuList);
    }

    @Override
    public void assignMenuByRole(AssginMenuVo assginMenuVo) {
        //删除当前角色所有菜单
        Long roleId = assginMenuVo.getRoleId();
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        sysRoleMenuService.remove(queryWrapper);
        //插入数据
        List<Long> menuIdList = assginMenuVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            if (StringUtils.isEmpty(menuId)) continue;
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            sysRoleMenuService.save(roleMenu);
        }
    }

    @Override
    public List<RouterVo> findMenusByUserId(Long userId) {
        List<SysMenu> menus = null;
        //管理员
        if (userId == 1) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            menus = baseMapper.selectList(wrapper);
        } else {
            menus = baseMapper.findMenuByUserId(userId);
        }
        List<SysMenu> menusList = MenuHelper.buildTree(menus);
        return BuildRoute(menusList);
    }

    private List<RouterVo> BuildRoute(List<SysMenu> menusList) {

        List<RouterVo> routerList = new ArrayList<>();
        for (SysMenu menu : menusList) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            List<SysMenu> children = menu.getChildren();
            //子路由
            if (menu.getType().intValue() == 1) {
                List<SysMenu> hideMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
                for (SysMenu HideMenu : hideMenuList) {
                    RouterVo hideRouter = new RouterVo();
                    router.setHidden(true);
                    router.setAlwaysShow(false);
                    router.setPath(getRouterPath(menu));
                    router.setComponent(menu.getComponent());
                    router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
                    routerList.add(hideRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    if (children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    router.setChildren(BuildRoute(children));
                }

            }
            routerList.add(router);
        }
        return routerList;
    }

    @Override
    public List<String> findPerByUserId(Long userId) {
        List<SysMenu> menuList = null;
        if (userId.longValue() == 1) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            menuList = baseMapper.selectList(wrapper);
        }else{
            menuList = baseMapper.findMenuByUserId(userId);

        }
        List<String> PerList = menuList.stream().filter(item -> item.getType() == 2).map(item -> item.getPerms()).collect(Collectors.toList());
        return PerList;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if (menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }
}
