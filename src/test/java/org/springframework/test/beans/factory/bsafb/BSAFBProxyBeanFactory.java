package org.springframework.test.beans.factory.bsafb;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * BSAFB 是 bean-scope-and-factory-bean 分支名称的首字母
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public class BSAFBProxyBeanFactory implements FactoryBean<BSAFBIUserDao> {

  @Override
  public BSAFBIUserDao getObject() throws Exception {
    InvocationHandler handler =
        new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 添加排除方法
            if ("toString".equals(method.getName())) return this.toString();
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("10001", "小傅哥");
            hashMap.put("10002", "八杯水");
            hashMap.put("10003", "阿毛");
            if (args == null || args.length == 0) {
              return "你被代理了 " + method.getName() + "：" + hashMap.get("10001");
            }
            return "你被代理了 " + method.getName() + "：" + hashMap.get(args[0].toString());
          }
        };
    return (BSAFBIUserDao)
        Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class[] {BSAFBIUserDao.class},
            handler);
  }

  @Override
  public Class<?> getObjectType() {
    return BSAFBIUserDao.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}
