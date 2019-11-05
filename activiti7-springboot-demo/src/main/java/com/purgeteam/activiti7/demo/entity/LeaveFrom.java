package com.purgeteam.activiti7.demo.entity;

import java.util.Date;

import lombok.Data;

/**
 * 请假申请表单
 *
 * @author purgeyao
 * @since 1.0
 */
@Data
public class LeaveFrom {

    /**
     * 表单id
     */
    private String formId;

    /**
     * activiti 流程实例id
     */
    private String processInstanceId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 天数
     */
    private Integer days;

    /**
     * 开始日期
     */
    private String beainDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 请假类型 0 带薪假 1 病假 2 事假
     */
    private Integer vacationType;

    /**
     * 请假事由
     */
    private String reason;

    /**
     * 流程状态 0 申请 1 审批中 2 已完成
     */
    private Integer processStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
