package com.purgeteam.activiti7.demo.controller;

import com.purgeteam.activiti7.demo.Activiti7StarterTest;
import com.purgeteam.activiti7.demo.entity.DemoEntity;
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
public class DemoControllerTest extends Activiti7StarterTest {

  @Resource
  private DemoController demoController;

  @Test
  public void start() {
    String start = demoController.start();
    log.info(start);
  }

  @Test
  public void apply() {
    // 流程定义ID:test:2:67f63d26-e010-11e9-85e2-acde48001122
    // 据流程实例ID查询 [ProcessInstance[811fc224-e023-11e9-83f1-acde48001122]]
    DemoEntity demoEntity = demoController.apply("10000");
    log.info(demoEntity.toString());
  }

}
