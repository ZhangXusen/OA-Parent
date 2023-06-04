package com.atguigu.auth;

import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDemo1 {
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Test
    public void getAll() {
        List<SysRole> sysRoleList = sysRoleMapper.selectList(null);
        System.out.println(sysRoleList);

    }
}
