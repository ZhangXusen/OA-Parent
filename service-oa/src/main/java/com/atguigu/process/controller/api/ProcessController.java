package com.atguigu.process.controller.api;

import com.atguigu.auth.service.SysUserService;
import com.atguigu.model.process.Process;
import com.atguigu.model.process.ProcessType;
import com.atguigu.process.service.OaProcessService;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.process.service.OaProcessTypeService;
import com.atguigu.result.Result;
import com.atguigu.vo.process.ApprovalVo;
import com.atguigu.vo.process.ProcessFormVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "审批流程管理")
@RequestMapping("/admin/process")
@CrossOrigin
public class ProcessController {
    @Autowired
    private OaProcessTypeService processTypeService;

    @Autowired
    private OaProcessService processService;

    @Autowired
    private OaProcessTemplateService processTemplateService;
    @Autowired
    private SysUserService userService;

    @ApiOperation("获取所有分类")
    @GetMapping("findAllType")
    public Result findAllType() {
        List<ProcessType> processTypeList = processTypeService.findProcessTypeAndTemp();
        return Result.ok(processTypeList);
    }

    @ApiOperation("获取模板信息")
    @GetMapping("/getTemp/{tempId}")
    public Result getTempInfo(@PathVariable("tempId") Long tempId) {
        return Result.ok(processTemplateService.getById(tempId));
    }

    @ApiOperation("发起审批流程")
    @PostMapping("startUp")
    public Result StartUp(@RequestBody ProcessFormVo Query) throws WxErrorException {
        processService.startUp(Query);
        return Result.ok();
    }

    @ApiOperation("待处理")
    @GetMapping("/findPending/{pageNum}/{pageSize}")
    public Result findPending(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        Page<Process> page = new Page<>(pageNum, pageSize);
        IPage<ProcessVo> processIPage = processService.findPending(page);
        return Result.ok(processIPage);
    }

    @ApiOperation("查看审批详情")
    @GetMapping("/show/{id}")
    public Result Show(@PathVariable("id") Long id) {
        Map<String, Object> map = processService.show(id);
        return Result.ok(map);

    }

    @ApiOperation("处理审批")
    @PostMapping("/approve")
    public Result Approve(@RequestBody ApprovalVo approvalVo) throws WxErrorException {
        processService.approve(approvalVo);
        return Result.ok();
    }

    @ApiOperation("已处理的审批申请")
    @GetMapping("/findProcessed/{pageNum}/{pageSize}")
    public Result Processed(@ApiParam(name = "pageNum", value = "当前页码", required = true)
                            @PathVariable Long pageNum,
                            @ApiParam(name = "pageSize", value = "每页记录数", required = true)
                            @PathVariable Long pageSize) {

        Page<Process> pageParam = new Page<>(pageNum, pageSize);
        IPage<ProcessVo> processed = processService.findProcessed(pageParam);
        return Result.ok(processed);

    }

    @ApiOperation(value = "已发起的申请")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> iPage = processService.findStarted(pageParam);
        return Result.ok(iPage);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/getUserInfo")
    public Result getCurrUserInfo() {
      Map<String,Object> map=  userService.getUerInfo();
        return Result.ok(map);
    }
}
