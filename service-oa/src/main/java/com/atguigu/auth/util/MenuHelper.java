package com.atguigu.auth.util;

import com.atguigu.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {


    public static List<SysMenu> buildTree(List<SysMenu> menuList) {
        System.out.println("menuList"+menuList);
        List<SysMenu> trees = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menu.getParentId().longValue() == 0) {
                trees.add(getChildren(menu, menuList));
            }
        }
        return trees;
    }

    private static SysMenu getChildren(SysMenu menu, List<SysMenu> menuList) {
        menu.setChildren(new ArrayList<SysMenu>());
        for (SysMenu item : menuList) {
            if (menu.getId().longValue() == item.getParentId().longValue()) {
                if (menu.getChildren() == null) {
                    menu.setChildren(new ArrayList<>());
                }
                menu.getChildren().add(getChildren(item, menuList));
            }
        }
        return menu;
    }
}
