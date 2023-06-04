package com.atguigu.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.custom.LoginUserInfoHelper;
import com.atguigu.model.process.Process;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.system.SysUser;
import com.atguigu.process.service.OaProcessService;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.process.service.PushMessageService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class PushMessageServiceImpl implements PushMessageService {
    @Resource
    private WxMpService wxMpService;
    @Autowired
    private OaProcessService oaProcessService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private OaProcessTemplateService templateService;

    //推送给审批人员
    @Override
    public void pushPendingMsg(Long processId, Long userId, String taskId) throws WxErrorException {
        Process process = oaProcessService.getById(processId);
        //审批人
        SysUser user = userService.getById(userId);
        //提交申请的用户
        SysUser submitUser = userService.getById(process.getUserId());
        ProcessTemplate template = templateService.getById(process.getProcessTemplateId());
        String openid = user.getOpenId();
        //方便测试，给默认值（开发者本人的openId）
        if (StringUtils.isEmpty(openid)) {
            openid = "omwf25izKON9dktgoy0dogqvnGhk";
        }
        //微信给审批人推送信息
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().toUser(openid)
                .templateId("KvOVeW7jz4-DZgQ_WuXjMZO5I4pPA7L7fflVNwC_ZQg")//模板id
                .url("http://oa.atguigu.cn/#/show/" + processId + "/" + taskId)//点击模板消息要访问的网址
                .build();
        //给模板里的变量添加数据
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());//请假的具体信息
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", submitUser.getName() + "提交了" + template.getName() + "审批申请，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));

        //发送信息
        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        System.out.println(msg);
    }

    //将审批结果发送给申请人
    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) {
        Process process = oaProcessService.getById(processId);
        ProcessTemplate processTemplate = templateService.getById(process.getProcessTemplateId());
        SysUser sysUser = userService.getById(userId);
        SysUser currentSysUser = userService.getById(LoginUserInfoHelper.getUserId());
        String openid = sysUser.getOpenId();
        if (StringUtils.isEmpty(openid)) {
            openid = "omwf25izKON9dktgoy0dogqvnGhk";
        }
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("I0kVeto7T0WIDP6tyoHh-hx83wa9_pe7Nx9eT93-6sc")//模板id
                .url("http://oa.atguigu.cn/#/show/" + processId + "/0")//点击模板消息要访问的网址
                .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", "你发起的" + processTemplate.getName() + "审批申请已经被处理了，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", currentSysUser.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword4", status == 1 ? "审批通过" : "审批拒绝", status == 1 ? "#009966" : "#FF0033"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        try {
            String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

}
