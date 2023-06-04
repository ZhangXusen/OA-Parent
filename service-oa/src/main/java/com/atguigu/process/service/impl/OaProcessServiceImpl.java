package com.atguigu.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.custom.LoginUserInfoHelper;
import com.atguigu.model.process.Process;
import com.atguigu.model.process.ProcessRecord;
import com.atguigu.model.process.ProcessTemplate;
import com.atguigu.model.system.SysUser;
import com.atguigu.process.mapper.OaProcessMapper;
import com.atguigu.process.service.OaProcessRecordService;
import com.atguigu.process.service.OaProcessService;
import com.atguigu.process.service.OaProcessTemplateService;
import com.atguigu.process.service.PushMessageService;
import com.atguigu.vo.process.ApprovalVo;
import com.atguigu.vo.process.ProcessFormVo;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.chanjar.weixin.common.error.WxErrorException;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-05-07
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private OaProcessTemplateService processTemplateService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private OaProcessRecordService processRecordService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private PushMessageService pushMessageService;

    @Override
    @Transactional
    public IPage<ProcessVo> selectPage(Page<ProcessVo> page, ProcessQueryVo query) {
        IPage<ProcessVo> processVoPage = baseMapper.selectPage(page, query);
        System.out.println("1" + processVoPage);
        return processVoPage;
    }

    //发起申请，启动审批流程
    @Override
    public void startUp(ProcessFormVo query) throws WxErrorException {
        //获取用户信息
        SysUser user = userService.getById(LoginUserInfoHelper.getUserId());
        //根据模板id查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(query.getProcessTemplateId());
        //保存审批信息到oa_process
        Process process = new Process();
        BeanUtils.copyProperties(query, process);
        process.setStatus(1);
        process.setUserId(user.getId());
        process.setTitle(user.getName() + "发起" + processTemplate.getName() + "申请");
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        baseMapper.insert(process);
        //启动流程实例
        //1.流程定义key
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();
        //2.业务key
        String processId = String.valueOf(process.getId());
        //3.流程参数form
        JSONObject Json = JSON.parseObject(query.getFormValues());
        JSONObject formData = Json.getJSONObject("formData");
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        Map<String, Object> var = new HashMap<>();
        var.put("data", map);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, processId, var);

        //查询下一个审批人
        List<Task> taskList = getCurrentTaskList(processInstance.getId()); //获取任务列表
        List<String> nameList = new ArrayList<>();
        for (Task task : taskList) {
            String assignee = task.getAssignee();
            SysUser SysUser = userService.loadUserByUsername(assignee);
            String userName = SysUser.getName();
            nameList.add(userName);
            //微信推送审批消息给下一审批人
            pushMessageService.pushPendingMsg(process.getId(), user.getId(), task.getId());

        }
        //更新oa_process数据
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待" + StringUtils.join(nameList.toArray(), ",") + "审批");
        baseMapper.updateById(process);
        //记录申请信息
        processRecordService.record(process.getId(), 1, "发起申请");
    }

    //查询待处理的列表
    @Override
    public IPage<ProcessVo> findPending(Page<Process> page) {
        //根据当前登录的用户，封装查询条件
        TaskQuery query = taskService.createTaskQuery().taskAssignee(LoginUserInfoHelper.getUsername()).orderByTaskCreateTime().desc();
        //根据条件查询
        /*listPage：1.开始的位置index,2.每页记录数*/
        int begin = (int) ((page.getCurrent() - 1) * page.getSize());
        List<Task> taskList = query.listPage(begin, (int) page.getSize());

        //封装返回的list集合,ProcessVo类型
        List<ProcessVo> list = new ArrayList<>();
        for (Task task : taskList) {
            //根据task获取流程实例id
            String instanceId = task.getProcessInstanceId();
            //根据id获取实例对象
            ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
            //从实例对象中获取业务key==processId
            String businessKey = instance.getBusinessKey();
            //根据业务key获取process对象
            if (businessKey == null) {
                continue;
            }
            Process process = baseMapper.selectById(Long.parseLong(businessKey));
            //process对象转processVo
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(task.getId());
            list.add(processVo);
        }
        Page<ProcessVo> processVoPage = new Page<>(page.getCurrent(), page.getSize(), query.count());
        processVoPage.setRecords(list);
        return processVoPage;
    }

    @Override
    public Map<String, Object> show(Long id) {
        //根据流程id获取流程信息
        Process process = baseMapper.selectById(id);
        //根据id获取流程记录信息
        LambdaQueryWrapper<ProcessRecord> RecordWrapper = new LambdaQueryWrapper<>();
        RecordWrapper.eq(ProcessRecord::getProcessId, id);
        List<ProcessRecord> processRecords = processRecordService.list(RecordWrapper);
        //根据id查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        //计算当前用户是否可以审批，能够查看详情的用户不是都能审批，审批后也不能重复审批
        Boolean isApprove = false;
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        for (Task task : taskList) {
            String username = LoginUserInfoHelper.getUsername();
            //当前登录用户可以审批
            if (task.getAssignee().equals(username)) {
                isApprove = true;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecords);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);
        return map;
    }

    //审批人处理审批
    @Override
    public void approve(ApprovalVo approvalVo) throws WxErrorException {
        //取得任务id,获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        //判断审批状态值：1：通过 2：驳回
        if (approvalVo.getStatus().intValue() == 1) {
            Map<String, Object> variable = new HashMap<>();
            taskService.complete(taskId, variable);//完成流程并传入流程变量
        } else {
            //结束流程
            this.endTask(taskId);
        }
        //流程记录process_record
        String desc = approvalVo.getStatus().intValue() == 1 ? "已通过" : "已驳回";
        processRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(), desc);
        //查询流程中的下一审批人，更新process表
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        List<Task> currentTaskList = getCurrentTaskList(process.getProcessInstanceId());
        if (CollectionUtils.isEmpty(currentTaskList)) {
            //有后续审批人
            List<String> assignList = new ArrayList<>(); //后续审批人的真实姓名列表
            //将下一审批人真实姓名加入列表
            for (Task task : currentTaskList) {
                String assignee = task.getAssignee(); //获取审批人username(assignee)
                SysUser user = userService.loadUserByUsername(assignee);
                assignList.add(user.getName());//添加真实姓名
                //推送消息给下一个审批人
                pushMessageService.pushPendingMsg(process.getId(), user.getId(), task.getId());

            }
            //更新流程process消息
            process.setStatus(1);
            process.setDescription("等待" + StringUtils.join(assignList.toArray(), ",") + "审批");

        } else {
            //无后续审批人
            if (approvalVo.getStatus().intValue() == 1) {
                process.setDescription("审批完成(通过)");
                process.setStatus(2);
            } else {
                process.setDescription("审批完成(被拒绝)");
                process.setStatus(-1);
            }
        }
        //推送消息给申请人
        pushMessageService.pushProcessedMessage(process.getId(), process.getUserId(), approvalVo.getStatus());
        baseMapper.updateById(process);
    }

    //查看历史审批记录
    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        //1.封装查询条件
        HistoricTaskInstanceQuery Query = historyService.createHistoricTaskInstanceQuery().taskAssignee(LoginUserInfoHelper.getUsername()).finished().orderByTaskCreateTime().desc();

        //2.分页查询
        int begin = (int) ((pageParam.getCurrent() - 1) * pageParam.getSize());
        List<HistoricTaskInstance> listPage = Query.listPage(begin, (int) pageParam.getSize());
        Long total = Query.count();
        //3.遍历list，再封装成Vo
        List<ProcessVo> VoList = new ArrayList<>();
        for (HistoricTaskInstance historicTaskInstance : listPage) {
            String definitionId = historicTaskInstance.getProcessDefinitionId();//获取流程实例id
            LambdaQueryWrapper<Process> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Process::getProcessInstanceId, definitionId);
            Process process = baseMapper.selectOne(queryWrapper);
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            VoList.add(processVo);
        }
        //4.封装成IPage
        IPage<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), total);
        page.setRecords(VoList);
        return page;
    }

    //已发起
    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());

        IPage<ProcessVo> page = baseMapper.selectPage(pageParam, processQueryVo);
        return page;
    }

    //结束流程
    private void endTask(String taskId) {
        //  当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //流程定义模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if (CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0); //结束节点
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey()); //当前节点

        //  临时保存当前活动的原始指向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理当前活动的指向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新指向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的指向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(task.getId());
    }

    private List<Task> getCurrentTaskList(String id) {
        return taskService.createTaskQuery().processInstanceId(id).list();
    }

    @Override
    public void deployByZip(String path) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
    }

}
