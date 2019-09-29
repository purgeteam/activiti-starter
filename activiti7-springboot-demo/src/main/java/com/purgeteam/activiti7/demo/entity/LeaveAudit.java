package com.purgeteam.activiti7.demo.entity;

import java.util.Date;
import lombok.Data;

/**
 * 请假审批
 *
 * @author purgeyao
 * @since 1.0
 */
@Data
public class LeaveAudit {

  /**
   * 审阅id
   */
  private String auditId;

  /**
   * activiti 流程实例id
   */
  private String processInstanceId;

  /**
   * 用户id
   */
  private String userId;

  /**
   * 审批结果 0 不通过 1 通过
   */
  private Integer auditResult;

  /**
   * 审批意见
   */
  private String comment;

  /**
   * 审批日期
   */
  private Date auditTime;

}
