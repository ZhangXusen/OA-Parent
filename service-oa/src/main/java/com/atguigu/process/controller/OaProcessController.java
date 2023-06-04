package com.atguigu.process.controller;


import com.atguigu.process.service.OaProcessService;
import com.atguigu.result.Result;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-05-07
 */
@Api(tags = "审批管理")
@RestController
@RequestMapping("/admin/process")
public class OaProcessController {
    @Autowired
    private OaProcessService processService;

    @ApiOperation("分页条件查询")
    @GetMapping("/getAll/{pageNum}/{pageSize}")
    public Result getPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, ProcessQueryVo Query) {
        Page<ProcessVo> page = new Page<>(pageNum, pageSize);
        System.out.println("11111111111111111111111111111111");
        IPage<ProcessVo> processPage = processService.selectPage(page, Query);
        return Result.ok(processPage);
    }

}

