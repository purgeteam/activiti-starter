package com.purgeteam.activiti7.demo.user;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * @author purgeyao
 * @since 1.0
 */
@Service
public class UserService {

//  @Resource
//  private IdentityService identityService;

    public void test() {
//    //项目中每创建一个新用户，对应的要创建一个Activiti用户
//    //两者的userId和userName一致
//    User admin=identityService.newUser("1");
//    admin.setLastName("admin");
//    identityService.saveUser(admin);
//
//    //项目中每创建一个角色，对应的要创建一个Activiti用户组
//    Group adminGroup=identityService.newGroup("1");
//    adminGroup.setName("admin");
//    identityService.saveGroup(adminGroup);
//
//    //用户与用户组关系绑定
//    identityService.createMembership("1","1");
    }
}
