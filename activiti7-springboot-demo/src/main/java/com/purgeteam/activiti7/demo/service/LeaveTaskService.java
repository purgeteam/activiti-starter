package com.purgeteam.activiti7.demo.service;

import com.purgeteam.activiti7.demo.entity.LeaveAudit;
import com.purgeteam.activiti7.demo.entity.LeaveFrom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

/**
 * @author purgeyao
 * @since 1.0
 */
@Slf4j
@Service
public class LeaveTaskService {

    private static final String LEAVE_PROCESS = "LeaveProcess";

//  @Resource
//  private IdentityService identityService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    /**
     * 填写请假申请表单，启动流程实例
     *
     * @param leaveFrom 请假申请表单
     */
    public LeaveFrom start(LeaveFrom leaveFrom) {
        // 启动流程实例，字符串 LEAVE_PROCESS 是BPMN模型文件里process元素的id
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(LEAVE_PROCESS);
        log.info("启动流程实例成功 processInstance.id={}", processInstance.getId());
        // 流程实例启动后，流程会跳转到请假申请节点
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        // 设置请假申请任务的执行人
        taskService.setAssignee(task.getId(), leaveFrom.getUserId());
        log.info("执行请假申请成功 vacationApply.Id={}", task.getId());

        // 设置流程参数：请假天数和表单ID
        // 流程引擎会根据请假天数days>3判断流程走向
        // formId是用来将流程数据和表单数据关联起来
        Map<String, Object> args = new HashMap<>(2);
        args.put("days", leaveFrom.getDays());
        args.put("formId", leaveFrom.getFormId());

        // 完成请假申请任务
        taskService.complete(task.getId(), args);
        log.info("完成请假申请任务提交");

        // 设置下个节点权限组
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.addCandidateGroup(task.getId(), "ADMIN");

        // 设置 流程实例id
        leaveFrom.setProcessInstanceId(task.getId());
        return leaveFrom;
    }

    /**
     * 待审批列表 (权限组查询)
     */
    public List<String> findAuditList() {
        // TODO 什么时候设置的权限组
        // 查出当前登录用户所在的用户组
//    List<Group> groups = identityService.createGroupQuery()
//        .groupMember(String.valueOf(userId)).list();
//    List<String> groupNames = groups.stream()
//        .map(group -> group.getName()).collect(Collectors.toList());

        // 权限组
        List<String> groupNames = Arrays.asList("ADMIN");
        int pageNum = 1, pageSize = 5;

        // 查询 用户组 的待审批请假流程列表
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(LEAVE_PROCESS)
                // 权限组查询
                .taskCandidateGroupIn(groupNames)
                .listPage(pageNum - 1, pageSize);

        // 获取 实例ID 集合
        List<String> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());
        // 根据流程 实例ID 查询请假申请表单数据
//    List<VacationApplyBasicPO> vacationApplyList =
//        vacationRepository.getVacationApplyList(taskIds);
        log.info("查询完成");
        return taskIds;
    }

    /**
     * 请假审批功能
     */
    public void vacationAudit(LeaveAudit leaveAudit) throws IllegalAccessException {
        // 查询当前审批节点 (ACT_RU_VARIABLE表ID_字段查询)
        Task task = taskService.createTaskQuery()
                .taskId(leaveAudit.getProcessInstanceId()).singleResult();

        if (task == null) {
            throw new IllegalAccessException("查询任务为空");
        }

        // 审批通过
        if (leaveAudit.getAuditResult() == 1) {
            // 设置流程参数：审批ID
            Map<String, Object> args = new HashMap<>(1);
            args.put("auditId123123", leaveAudit.getAuditId());

            // 设置审批任务的执行人 (签收操作)
            taskService.claim(task.getId(), leaveAudit.getUserId());
            // 完成审批任务
            taskService.complete(task.getId(), args);
            log.info("审批通过");
        } else {
            // 审批不通过，结束流程
            runtimeService
                    .deleteProcessInstance(task.getProcessInstanceId(), leaveAudit.getAuditId());
            log.info("审批不通过");
        }

    }
}
