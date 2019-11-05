package com.purgeteam.activiti7.demo.service;

import java.util.List;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.stereotype.Service;

/**
 * @author purgeyao
 * @since 1.0
 */
@Slf4j
@Service
public class UserTaskService {

    /**
     * 流程引擎
     */
    @Resource
    private ProcessEngine processEngine;

    /**
     * 流程定义和部署相关的存储服务
     */
    @Resource
    private RepositoryService repositoryService;

    /**
     * 流程运行时相关的服务
     */
    @Resource
    private RuntimeService runtimeService;

    /**
     * 节点任务相关操作接口
     */
    @Resource
    private TaskService taskService;

    /**
     * 流程图生成器
     */
    @Resource
    private ProcessDiagramGenerator processDiagramGenerator;

    /**
     * 历史记录相关服务接口
     */
    @Resource
    private HistoryService historyService;

    /**
     * 启动流程
     *
     * @return 流程实例id
     */
    public String start() {
        // 去数据库表名为 act_re_procdef 取出key 启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("userTaskProgect");

        log.info("启动流程实例成功:{}", processInstance);
        log.info("流程实例ID:{}", processInstance.getId());
        log.info("流程定义ID:{}", processInstance.getProcessDefinitionId());

        //去act_run_task表中，根据设计id 查看任务运行状态
        return processInstance.getId();
    }

    /**
     * 查看个人任务
     */
    public void findUserAssignee() {
        TaskQuery query = taskService.createTaskQuery();
        String assignee = "张三";
        query.taskAssignee(assignee);
        List<Task> list = query.list();
        list.forEach(s -> log.info(s.getId() + "-----------\n" + s.getName()));
    }

    /**
     * 办理任务
     */
    public void complete() {
        // act_ru_task 表id
        String taskId = "ac1841fa-e035-11e9-9652-acde48001122";
        processEngine.getTaskService().complete(taskId);
    }

    /**
     * 根据流程实例ID来查询
     */
    public void findByProcessInstanceId(String id) {

        // 验证是否启动成功
        // 通过查询正在运行的流程实例来判断
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();

        // 根据流程实例ID来查询
        List<ProcessInstance> runningInstanceList = processInstanceQuery
                .processInstanceId(id).list();
        log.info("据流程实例ID查询 {}", runningInstanceList.toString());
        log.info("根据流程实例ID查询条数:{}", runningInstanceList.size());
    }

    /**
     * 查询历史流程实例 对应表act_hi_procinst
     */
    public void findClosed() {

        List<HistoricProcessInstance> userTaskProgect = historyService
                .createHistoricProcessInstanceQuery()
                .processDefinitionKey("userTaskProgect")
                .processInstanceId("ac12c3b6-e035-11e9-9652-acde48001122")
                .list();

        for (HistoricProcessInstance hi : userTaskProgect) {
            log.info(hi.getId() + "	  " + hi.getStartTime() + "   " + hi.getEndTime());
        }
    }

}