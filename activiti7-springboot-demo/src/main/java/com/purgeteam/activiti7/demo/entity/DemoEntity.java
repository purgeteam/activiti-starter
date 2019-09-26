package com.purgeteam.activiti7.demo.entity;

import lombok.Data;

/**
 * 测试表单
 *
 * @author purgeyao
 * @since 1.0
 */
@Data
public class DemoEntity {

  private String userId;
  /**
   * 姓名
   */
  private String name;

  /**
   * 年龄
   */
  private Integer age;

  /**
   * 爱好
   */
  private String hobby;
}
