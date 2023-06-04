package com.atguigu.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.model.wechat.Menu;
import com.atguigu.vo.wechat.MenuVo;
import com.atguigu.wechat.mapper.WechatMenuMapper;
import com.atguigu.wechat.service.WechatMenuService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-13
 */
@Service
public class WechatMenuServiceImpl extends ServiceImpl<WechatMenuMapper, Menu> implements WechatMenuService {
    @Autowired
    private WxMpService wxMpService;

    @Override
    public List<MenuVo> findMenuInfo() {
        //1.查询所有菜单
        List<Menu> menuList = baseMapper.selectList(null);
        //查询所有一级菜单(parentId=0),放入list
        List<Menu> Level1MenuList = new ArrayList<>();
        List<MenuVo> MenuVoList = new ArrayList<>();
        //获取一级菜单列表
        for (Menu menu : menuList) {
            if (menu.getParentId().longValue() == 0) {
                Level1MenuList.add(menu);
            }
        }
        //遍历一级菜单，获取其二级菜单，放入children
        for (Menu menu : Level1MenuList) {
            //menu->menuVo
            MenuVo menuVo = new MenuVo();
            BeanUtils.copyProperties(menu, menuVo);

            //二级菜单列表
            List<Menu> Level2MenuList = menuList.stream()
                    .filter(item -> item.getParentId().longValue() == menu.getId()).sorted(Comparator.comparing(Menu::getSort)).collect(Collectors.toList());
            //children属性
            List<MenuVo> childrenMenu = new ArrayList<>();
            //将二级菜单Menu转为menuVo并加入children
            for (Menu menu2 : Level2MenuList) {
                MenuVo twoMenuVo = new MenuVo();
                BeanUtils.copyProperties(menu2, twoMenuVo);
                childrenMenu.add(twoMenuVo);
            }
            menuVo.setChildren(childrenMenu);
            MenuVoList.add(menuVo);
        }

        return MenuVoList;

    }

    //将后台设置的微信菜单同步推送到微信
    @Override
    public void syncMenu() {


        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        //将数据封装成微信所需要的格式
        for (MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if (CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://oa.atguigu.cn/#" + oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for (MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if (twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://oa.atguigu.cn#" + twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        //菜单

        JSONObject button = new JSONObject();
        button.put("button", buttonList);
        //调用工具方法，实现推送
        try {
            wxMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
