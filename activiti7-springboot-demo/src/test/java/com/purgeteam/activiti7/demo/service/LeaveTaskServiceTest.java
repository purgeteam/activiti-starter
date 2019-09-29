package com.purgeteam.activiti7.demo.service;

import com.purgeteam.activiti7.demo.Activiti7StarterTest;
import com.purgeteam.activiti7.demo.entity.LeaveAudit;
import com.purgeteam.activiti7.demo.entity.LeaveFrom;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.stereotype.Component;

/**
 * @author purgeyao
 * @since 1.0
 */
@Slf4j
@Component
public class LeaveTaskServiceTest extends Activiti7StarterTest {

  @Resource
  private LeaveTaskService leaveTaskService;

  @Test
  public void start() {
    LeaveFrom leaveFrom = new LeaveFrom();
    leaveFrom.setFormId("leaveFrom123000");
//    leaveFrom.setProcessInstanceId();
    leaveFrom.setUserId("100000");
    leaveFrom.setDays(3);
    leaveFrom.setBeainDate("2019-09-01");
    leaveFrom.setEndDate("2019-09-30");
    leaveFrom.setVacationType(0);
    leaveFrom.setReason("我要去拉萨");
    leaveFrom.setProcessStatus(0);
    leaveFrom.setCreateTime(new Date());
    leaveFrom.setUpdateTime(new Date());

    LeaveFrom start = leaveTaskService.start(leaveFrom);
    log.info("完成 流程id={}", start.getProcessInstanceId());
  }

  @Test
  public void findAuditList() {
    List<String> auditList = leaveTaskService.findAuditList();
    log.info(auditList.toString());
  }

  @Test
  public void vacationAudit() throws IllegalAccessException {
    LeaveAudit leaveAudit = new LeaveAudit();
    leaveAudit.setAuditId("adsfwec");
    leaveAudit.setProcessInstanceId("feaebf7f-e265-11e9-9ffb-1ad0548a0cd0");
    leaveAudit.setUserId("200000");
    leaveAudit.setAuditResult(1);
    leaveAudit.setComment("同意请假");
    leaveAudit.setAuditTime(new Date());

    leaveTaskService.vacationAudit(leaveAudit);
  }
}
