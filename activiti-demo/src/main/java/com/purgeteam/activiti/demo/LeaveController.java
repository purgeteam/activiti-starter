package com.purgeteam.activiti.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author purgeyao
 * @since 1.0
 */
@RestController
@RequestMapping("/level/v1")
public class LeaveController {

  public static final Logger log = LoggerFactory.getLogger(LeaveController.class);

  @Resource
  private RuntimeService runtimeService;

  @Resource
  private TaskService taskService;

  @Resource
  private ProcessEngine processEngine;

  /**
   * 启动流程
   * @param userId
   * @return
   */
  @RequestMapping(value = "/start", method = RequestMethod.GET)
  public Map<String, Object> start(@RequestParam String userId){
    Map<String, Object> vars = new HashMap<>();
    Leave leave = new Leave();
    leave.setUserId(userId);
    vars.put("leave",leave);
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave1",vars);
    Map<String, Object> resultMap = new HashMap<>();
    return resultMap;
  }

  /**
   * 填写请假单
   * @param leave
   * @return
   */
  @RequestMapping(value="/apply", method = RequestMethod.POST)
  public Map<String, Object> apply(@RequestBody Leave leave){
    Task task = taskService.createTaskQuery().taskId(leave.getTaskId()).singleResult();
    Map<String, Object> vars = new HashMap<>();
    Leave origin = (Leave) taskService.getVariable(leave.getTaskId(), "leave");
    origin.setDesc(leave.getDesc());
    origin.setStartDate(leave.getStartDate());
    origin.setEndDate(leave.getEndDate());
    origin.setTotalDay(leave.getTotalDay());
    origin.setApprover1(leave.getApprover1());
    origin.setApprover2(leave.getApprover2());
    origin.setSubmit(leave.getSubmit());
    vars.put("leave", origin);
    taskService.complete(leave.getTaskId(), vars);
//    Map<String, Object> resultMap = ResultMapHelper.getSuccessMap();
    Map<String, Object> resultMap = new HashMap<>();
    return resultMap;
  }

  @RequestMapping(value="/apply1", method = RequestMethod.POST)
  public Map<String, Object> apply(String taskId){
    Leave leave = new Leave();
//    leave.setUserId("3000");
    leave.setSubmit(true);
    leave.setStartDate(new Date());
    leave.setEndDate(new Date());
    leave.setTotalDay(3);
    leave.setDesc("没有理由");
    leave.setTaskId(taskId);
//    leave.setTaskName("");
    leave.setApprover1("3001");
//    leave.setAgree1();
//    leave.setApproveDesc1();
    leave.setApprover2("3002");
//    leave.setAgree2();
//    leave.setApproveDesc2();

    Task task = taskService.createTaskQuery().taskId(leave.getTaskId()).singleResult();
    Map<String, Object> vars = new HashMap<>();
    Leave origin = (Leave) taskService.getVariable(leave.getTaskId(), "leave");
    origin.setDesc(leave.getDesc());
    origin.setStartDate(leave.getStartDate());
    origin.setEndDate(leave.getEndDate());
    origin.setTotalDay(leave.getTotalDay());
    origin.setApprover1(leave.getApprover1());
    origin.setApprover2(leave.getApprover2());
    origin.setSubmit(leave.getSubmit());
    vars.put("leave", origin);
    taskService.complete(leave.getTaskId(), vars);
//    Map<String, Object> resultMap = ResultMapHelper.getSuccessMap();
    Map<String, Object> resultMap = new HashMap<>();
    return resultMap;
  }

  /**
   * 查询用户流程
   * @param userId
   * @return
   */
  @RequestMapping(value = "/find", method = RequestMethod.GET)
  public Map<String, Object> find(@RequestParam("userId")String userId){
    List<Task> taskList = taskService.createTaskQuery().taskAssignee(userId).list();
    List<Leave> resultList = new ArrayList<>();
    if(!CollectionUtils.isEmpty(taskList)){
      for(Task task : taskList){
        Leave leave = (Leave) taskService.getVariable(task.getId(),"leave");
        leave.setTaskId(task.getId());
        leave.setTaskName(task.getName());
        resultList.add(leave);
      }
    }
//    Map<String, Object> resultMap = ResultMapHelper.getSuccessMap();
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("datas", resultList);
    return resultMap;
  }

  /**
   * 直接主管审批
   * @param leave
   * @return
   */
  @RequestMapping(value = "/approve1", method = RequestMethod.POST)
  public Map<String, Object> approve1(@RequestBody Leave leave){
    Task task = taskService.createTaskQuery().taskId(leave.getTaskId()).singleResult();
    Map<String, Object> vars = new HashMap<>();
    Leave origin = (Leave) taskService.getVariable(leave.getTaskId(), "leave");
    origin.setApproveDesc1(leave.getApproveDesc1());
    origin.setAgree1(leave.getAgree1());
    vars.put("leave", origin);
    taskService.complete(leave.getTaskId(),vars);
//    Map<String, Object> resultMap = ResultMapHelper.getSuccessMap();
    Map<String, Object> resultMap = new HashMap<>();
    return resultMap;
  }

  /**
   * 部门主管审批
   * @param leave
   * @return
   */
  @RequestMapping(value = "/approve2", method = RequestMethod.POST)
  public Map<String, Object> approve2(@RequestBody Leave leave){
    Task task = taskService.createTaskQuery().taskId(leave.getTaskId()).singleResult();
    Map<String, Object> vars = new HashMap<>();
    Leave origin = (Leave) taskService.getVariable(leave.getTaskId(), "leave");
    origin.setApproveDesc2(leave.getApproveDesc2());
    origin.setAgree2(leave.getAgree2());
    vars.put("leave", origin);
    taskService.complete(leave.getTaskId(),vars);
//    Map<String, Object> resultMap = ResultMapHelper.getSuccessMap();
    Map<String, Object> resultMap = new HashMap<>();
    return resultMap;
  }

  /**
   * 查看历史记录
   * @param userId
   * @return
   */
  @RequestMapping(value="/findClosed", method = RequestMethod.GET)
  public Map<String, Object> findClosed(String userId){
    HistoryService historyService = processEngine.getHistoryService();

    List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processDefinitionKey("leave1").variableValueEquals("leave.userId",userId).list();
    List<Leave> leaves = new ArrayList<>();
    for(HistoricProcessInstance pi : list){
      leaves.add((Leave) pi.getProcessVariables().get("leave"));
    }
//    Map<String, Object> resultMap = ResultMapHelper.getSuccessMap();
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("datas", leaves);
    return resultMap;
  }

}
