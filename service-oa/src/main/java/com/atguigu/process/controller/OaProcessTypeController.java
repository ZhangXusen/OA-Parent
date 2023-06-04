package com.atguigu.process.controller;


import com.atguigu.model.process.Process;
import com.atguigu.model.process.ProcessType;
import com.atguigu.model.system.SysUser;
import com.atguigu.process.service.OaProcessTypeService;
import com.atguigu.result.Result;
import com.atguigu.vo.process.ProcessQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-05-06
 */
@Api(tags = "审批类型")
@RestController
@RequestMapping("/admin/process/processType/")
public class OaProcessTypeController {
    @Autowired
    private OaProcessTypeService processTypeService;

    @ApiOperation("获取分页列表")
    @GetMapping("{pageNum}/{pageSize}")
    public Result getPage(
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize
    ) {
        Page<ProcessType> page = new Page<>(pageNum, pageSize);
        IPage<ProcessType> processTypePage = processTypeService.page(page);
        return Result.ok(processTypePage);
    }

    @PreAuthorize("hasAuthority('bnt.processType.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessType processType = processTypeService.getById(id);
        return Result.ok(processType);
    }

    @PreAuthorize("hasAuthority('bnt.processType.add')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessType processType) {
        processTypeService.save(processType);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processType.update')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessType processType) {
        processTypeService.updateById(processType);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processType.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTypeService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("获取全部审批类型")
    @GetMapping("/getAll")
    public Result getAll() {
        List<ProcessType> processType = processTypeService.list();
        return Result.ok(processType);
    }

}

