# Springboot2.1.x给Activiti7配置单独数据源问题
## 简介

最近基于最新的`Activiti7`配置了`SpringBoot2`。
简单上手使用了一番。发现市面上解决`Activiti7`的教程很少，采坑也比较多，在`Activiti6`配置数据源和`Activiti7`有所区别，基于`Activiti6`在`Activiti7`里是无法正常使用的。接下来让我们看下区别。

## 问题

### `Activiti6`多数据源配置

6的配置比较简单点。

1. 先加入配置：

```
# activiti 数据源
spring.datasource.activiti.driver=com.mysql.jdbc.Driver
spring.datasource.activiti.url=jdbc:mysql://10.1.1.97:3311/test-activiti7-db?useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true
spring.datasource.activiti.username=root
spring.datasource.activiti.password=Rtqw123OpnmER
spring.datasource.activiti.driverClassName=com.mysql.jdbc.Driver
```

2. 用`@ConfigurationProperties`加载以`spring.datasource.activiti`开头的`DataSource`。

3. 创建`ActivitiConfig`继承`AbstractProcessEngineAutoConfiguration`方法注入`SpringProcessEngineConfiguration`bean,调用`AbstractProcessEngineAutoConfiguration#baseSpringProcessEngineConfiguration`方法把创建的数据源注入。

```
@Configuration
public class ActivitiConfig extends AbstractProcessEngineAutoConfiguration {

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.activiti")
  public DataSource activitiDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public SpringProcessEngineConfiguration springProcessEngineConfiguration(
      PlatformTransactionManager transactionManager,
      SpringAsyncExecutor springAsyncExecutor) throws IOException {

    return baseSpringProcessEngineConfiguration(
        activitiDataSource(),
        transactionManager,
        springAsyncExecutor);
  }
}
```

`Activiti6`的数据源已经配置完成，如果是7的话会发现`AbstractProcessEngineAutoConfiguration#baseSpringProcessEngineConfiguration`方法已经不存在了，我们需要产考源码构建方式改造一番。

## 修复

### `Activiti7`多数据源配置

配置还是要上面的。

1. 创建`ActivitiConfig`继承`AbstractProcessEngineAutoConfiguration`方法注入`SpringProcessEngineConfiguration`bean。

2. 用`@ConfigurationProperties`加载以`spring.datasource.activiti`开头的`DataSource`

```
@Configuration
public class ActivitiConfig extends AbstractProcessEngineAutoConfiguration {
  
  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.activiti")
  public DataSource activitiDataSource() {
    return DataSourceBuilder.create().build();
  }
  
  ...略
}
```

3. `SpringProcessEngineConfiguration`注入方式改为下面：

```
  @Bean
  @Primary
  public SpringProcessEngineConfiguration springProcessEngineConfiguration(
      PlatformTransactionManager transactionManager,
      SpringAsyncExecutor springAsyncExecutor,
      ActivitiProperties activitiProperties,
      ProcessDefinitionResourceFinder processDefinitionResourceFinder,
      @Autowired(required = false) DefaultActivityBehaviorFactoryMappingConfigurer processEngineConfigurationConfigurer,
      @Autowired(required = false) List<ProcessEngineConfigurator> processEngineConfigurators,
      UserGroupManager userGroupManager,
      DataSource dataSource) throws IOException {

    SpringProcessEngineConfiguration conf = new SpringProcessEngineConfiguration();
    conf.setConfigurators(processEngineConfigurators);
    configureProcessDefinitionResources(processDefinitionResourceFinder,
        conf);
    conf.setDataSource(dataSource);
    conf.setTransactionManager(transactionManager);

    if (springAsyncExecutor != null) {
      conf.setAsyncExecutor(springAsyncExecutor);
    }
    conf.setDeploymentName(activitiProperties.getDeploymentName());
    conf.setDatabaseSchema(activitiProperties.getDatabaseSchema());
    conf.setDatabaseSchemaUpdate(activitiProperties.getDatabaseSchemaUpdate());
    conf.setDbHistoryUsed(activitiProperties.isDbHistoryUsed());
    conf.setAsyncExecutorActivate(activitiProperties.isAsyncExecutorActivate());
    if (!activitiProperties.isAsyncExecutorActivate()) {
      ValidatorSet springBootStarterValidatorSet = new ValidatorSet("activiti-spring-boot-starter");
      springBootStarterValidatorSet.addValidator(new AsyncPropertyValidator());
      if (conf.getProcessValidator() == null) {
        ProcessValidatorImpl processValidator = new ProcessValidatorImpl();
        processValidator.addValidatorSet(springBootStarterValidatorSet);
        conf.setProcessValidator(processValidator);
      } else {
        conf.getProcessValidator().getValidatorSets().add(springBootStarterValidatorSet);
      }
    }
    conf.setMailServerHost(activitiProperties.getMailServerHost());
    conf.setMailServerPort(activitiProperties.getMailServerPort());
    conf.setMailServerUsername(activitiProperties.getMailServerUserName());
    conf.setMailServerPassword(activitiProperties.getMailServerPassword());
    conf.setMailServerDefaultFrom(activitiProperties.getMailServerDefaultFrom());
    conf.setMailServerUseSSL(activitiProperties.isMailServerUseSsl());
    conf.setMailServerUseTLS(activitiProperties.isMailServerUseTls());

    if (userGroupManager != null) {
      conf.setUserGroupManager(userGroupManager);
    }

    conf.setHistoryLevel(activitiProperties.getHistoryLevel());
    conf.setCopyVariablesToLocalForTasks(activitiProperties.isCopyVariablesToLocalForTasks());
    conf.setSerializePOJOsInVariablesToJson(activitiProperties.isSerializePOJOsInVariablesToJson());
    conf.setJavaClassFieldForJackson(activitiProperties.getJavaClassFieldForJackson());

    if (activitiProperties.getCustomMybatisMappers() != null) {
      conf.setCustomMybatisMappers(
          getCustomMybatisMapperClasses(activitiProperties.getCustomMybatisMappers()));
    }

    if (activitiProperties.getCustomMybatisXMLMappers() != null) {
      conf.setCustomMybatisXMLMappers(
          new HashSet<>(activitiProperties.getCustomMybatisXMLMappers()));
    }

    if (activitiProperties.getCustomMybatisXMLMappers() != null) {
      conf.setCustomMybatisXMLMappers(
          new HashSet<>(activitiProperties.getCustomMybatisXMLMappers()));
    }

    if (activitiProperties.isUseStrongUuids()) {
      conf.setIdGenerator(new StrongUuidGenerator());
    }

    if (activitiProperties.getDeploymentMode() != null) {
      conf.setDeploymentMode(activitiProperties.getDeploymentMode());
    }

    conf.setActivityBehaviorFactory(new DefaultActivityBehaviorFactory());

    if (processEngineConfigurationConfigurer != null) {
      processEngineConfigurationConfigurer.configure(conf);
    }

    return conf;
  }

  private void configureProcessDefinitionResources(
      ProcessDefinitionResourceFinder processDefinitionResourceFinder,
      SpringProcessEngineConfiguration conf) throws IOException {
    List<Resource> procDefResources = processDefinitionResourceFinder
        .discoverProcessDefinitionResources();
    if (!procDefResources.isEmpty()) {
      conf.setDeploymentResources(procDefResources.toArray(new Resource[0]));
    }
  }
```

## 常见错误

在以上配置中可能会有`jdbcUrl is required with driverClassName`错误

解决办法如下:

```
# activiti 数据源
spring.datasource.activiti.driver=com.mysql.jdbc.Driver
spring.datasource.activiti.url=jdbc:mysql://10.1.1.97:3311/test-activiti7-db?useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true
# url换为jdbc-url 解决jdbcUrl is required with driverClassName错误
# 官方文档的解释是：
# 因为连接池的实际类型没有被公开，所以在您的自定义数据源的元数据中没有生成密钥，而且在IDE中没有完成(因为DataSource接口没有暴露属性)。
# 另外，如果您碰巧在类路径上有Hikari，那么这个基本设置就不起作用了，因为Hikari没有url属性(但是确实有一个jdbcUrl属性)。在这种情况下，您必须重写您的配置如下:
spring.datasource.activiti.jdbc-url=${spring.datasource.activiti.url}
spring.datasource.activiti.username=root
spring.datasource.activiti.password=Rtqw123OpnmER
spring.datasource.activiti.driverClassName=com.mysql.jdbc.Driver
```

## 总结

`Activiti7`国内的教程不是很多，需要自己在社区里或者官方文档，源码去看了解，细心学习。

> 示例代码地址:[activiti-starter](https://github.com/purgeteam/activiti-starter)

> 作者GitHub:
[Purgeyao](https://github.com/purgeyao) 欢迎关注
