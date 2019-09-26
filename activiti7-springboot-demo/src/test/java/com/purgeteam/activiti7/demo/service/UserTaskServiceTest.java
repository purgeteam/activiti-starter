package com.purgeteam.activiti7.demo.service;

import com.purgeteam.activiti7.demo.Activiti7StarterTest;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.stereotype.Component;

/**
 * @author purgeyao
 * @since 1.0
 */
@Slf4j
@Component
public class UserTaskServiceTest extends Activiti7StarterTest {

  @Resource
  private UserTaskService userTaskService;

  @Test
  public void start() {
    String start = userTaskService.start();
    log.info(start);
  }

  @Test
  public void findUserAssignee() {
    userTaskService.findUserAssignee();
  }

  @Test
  public void complete() {
    userTaskService.complete();
  }

  @Test
  public void findByProcessInstanceId(){
    userTaskService.findByProcessInstanceId("c58f2901-e02b-11e9-acce-acde48001122");
  }

  @Test
  public void findClosed(){
    userTaskService.findClosed();
  }
}
