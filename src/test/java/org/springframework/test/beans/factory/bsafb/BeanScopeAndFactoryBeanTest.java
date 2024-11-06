package org.springframework.test.beans.factory.bsafb;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.beans.context.support.ClassPathXmlApplicationContext;

/**
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public class BeanScopeAndFactoryBeanTest {
  @Test
  public void test_factory_bean() {
    // 1.初始化 BeanFactory
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-bean-scope-and-factory-bean.xml");
    applicationContext.registerShutdownHook();
    // 2. 调用代理方法
    BSAFBUserService userService =
        applicationContext.getBean("userService", BSAFBUserService.class);
    System.out.println("userService.getUId() = " + userService.getUId());
    System.out.println("测试结果：" + userService.queryUserInfo());
  }

  @Test
  public void test_prototype() {
    // 1.初始化 BeanFactory
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-bean-scope-and-factory-bean.xml");
    applicationContext.registerShutdownHook();

    // 2. 获取Bean对象调用方法
    BSAFBUserService userService01 =
        applicationContext.getBean("userService", BSAFBUserService.class);
    BSAFBUserService userService02 =
        applicationContext.getBean("userService", BSAFBUserService.class);

    // 3. 配置 scope="prototype/singleton"
    System.out.println(userService01);
    System.out.println(userService02);

    // 4. 打印十六进制哈希
    System.out.println(userService01 + " 十六进制哈希：" + Integer.toHexString(userService01.hashCode()));
    System.out.println(ClassLayout.parseInstance(userService01).toPrintable());

    // 4. 打印十六进制哈希
    System.out.println(userService02 + " 十六进制哈希：" + Integer.toHexString(userService02.hashCode()));
    System.out.println(ClassLayout.parseInstance(userService02).toPrintable());
  }
}
