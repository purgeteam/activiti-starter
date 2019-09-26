package com.purgeteam.activiti7.demo.controller;

import com.purgeteam.activiti7.demo.entity.DemoEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author purgeyao
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

  private static final String INSTANCE_KEY = "test";
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
   * 启动请假流程
   *
   * @return 流程实例ID
   */
  @GetMapping("start")
  public String start() {
    log.info("开启测试流程...");

    // 设置流程参数，开启流程
    Map<String, Object> map = new HashMap<>();
    DemoEntity demoEntity = new DemoEntity();
    demoEntity.setUserId("10000");
    map.put("demoEntity", demoEntity);

    // 使用流程定义的key启动流程实例，key对应test.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
    ProcessInstance instance = runtimeService.startProcessInstanceByKey(INSTANCE_KEY, map);

    String id = instance.getId();
    String processDefinitionId = instance.getProcessDefinitionId();
    log.info("启动流程实例成功:{}", instance);
    log.info("流程实例ID:{}", id);
    log.info("流程定义ID:{}", processDefinitionId);

    // 验证是否启动成功
    // 通过查询正在运行的流程实例来判断
    ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();

    // 根据流程定义ID来查询
    List<ProcessInstance> runningList = processInstanceQuery
        .processDefinitionId(processDefinitionId).list();
    log.info("据流程ID查询 {}", runningList.toString());
    log.info("根据流程ID查询条数:{}", runningList.size());

    // 根据流程实例ID来查询
    List<ProcessInstance> runningInstanceList = processInstanceQuery
        .processInstanceId(id).list();
    log.info("据流程实例ID查询 {}", runningInstanceList.toString());
    log.info("根据流程实例ID查询条数:{}", runningInstanceList.size());

    // 返回流程实例ID
    return id;
  }

  public void getTask(String taskAssignee) {
    // 注意 这里需要拿007来查询，key-value需要拿value来获取任务
    List<Task> list = taskService.createTaskQuery().taskAssignee(taskAssignee).list();
    for (Task task : list) {
      log.info("任务ID：" + task.getId());
      log.info("任务的办理人：" + task.getAssignee());
      log.info("任务名称：" + task.getName());
      log.info("任务的创建时间：" + task.getCreateTime());
      log.info("流程实例ID：" + task.getProcessInstanceId());
      log.info("##################################");
    }
  }

  /**
   * 提交表单
   *
   * @param taskId 流程实例ID
   */
  @GetMapping("apply")
  public DemoEntity apply(String userId) {

//    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//    if (task != null) {
//      log.info("task {}", task.toString());
//    }
//    log.info("task is null");

    Map<String, Object> vars = new HashMap<>();
    // 获取一个变量并在任务范围中搜索
    DemoEntity demoEntity = (DemoEntity) taskService.getVariable(userId, "demoEntity");
    demoEntity.setName("小明测试");
    demoEntity.setAge(18);
    demoEntity.setHobby("打篮球");
    vars.put("demoEntity", demoEntity);

    // 当任务成功执行时调用，并由最终用户给出所需的任务参数。
    taskService.complete("", vars);
    log.info("提交表单成功...");

    return demoEntity;
  }

  public String find(String userId) {
    taskService.createTaskQuery().taskAssignee("");
    return "test";
  }
}
