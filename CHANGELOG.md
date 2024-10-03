> **每节内容我希望是以 STAR 法则进行阐述的，不仅如此，还要可能提供 UML 类图，以及相关的测试代码。**


## 简单的 bean 容器实现
> 代码分支: [simple-bean-container-impl](https://github.com/DoubleW2w/sbs-small-spring/tree/simple-bean-container-impl)

### S
我相信讲到 Spring，第一反应就是容器。

### T
定义一个简单的 Spring 容器，用于存放和获取 Bean 对象。

### A
我们日常熟悉的容器有类似列表，数组之类的东西，可以存放多个元素。在 Spring 中，bean 容器可以理解是一种存放 bean 对象的数据结构。它可以通过对象名称来进行索引。所以最简单的 bean 容器，本质上就是一个 HashMap。
- 注册：相当于把数据存储到 HashMap 中
- 获取：Bean 对象的名字就是 key，通过名称进行获取。

```java
public class BeanFactory {

  private Map<String, Object> beanMap = new HashMap<>();

  public void registerBean(String name, Object bean) {
    beanMap.put(name, bean);
  }

  public Object getBean(String name) {
    return beanMap.get(name);
  }
}
```

测试：

```java
public class SimpleBeanContainerImplTest {
  @InjectMocks BeanFactory beanFactory;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void test_RegisterBean() {
    beanFactory.registerBean("helloService", new HelloService());
    Assertions.assertNotNull(beanFactory.getBean("helloService"));
  }

  @Test
  public void test_GetBean() {
    beanFactory.registerBean("helloService", new HelloService());
    HelloService helloService = (HelloService) beanFactory.getBean("helloService");
    Assertions.assertEquals(helloService.hello(), "hello");
  }

  static class HelloService {
    public String hello() {
      System.out.println("hello world!");
      return "hello";
    }
  }
}
```
### R

什么是容器？最最最简单的理解是盒子 📦。

而我们所说的 Spring Bean 容器是 Spring 管理 Bean 对象的盒子，也就是说把 Bean 对象的一生交给 Spring 来管理，由 Spring 进行统一分配。这样有什么意义呢？
对使用者而言，只使用 Bean，有一种「随用随取」的方便，而不用担心使用 Bean 的时候，Bean 的某些信息怎么不存在？或者这个 Bean 怎么消失？等问题。

## BeanDefinition 和 BeanDefinitionRegistry

> 代码分支：[bean-definition-and-bean-definition-registry](https://github.com/DoubleW2w/sbs-small-spring/tree/bean-definition-and-bean-definition-registry)

### S

在 [simple-bean-container-impl](https://github.com/DoubleW2w/sbs-small-spring/tree/simple-bean-container-impl) 实现的代码中，我们简单地使用一个 HashMap 实现了一个 Bean 容器，其中 Bean 对象是手动实例化好。

### T

为了完善 Spring 容器框架的功能，我们需要增加一些东西：

- BeanDefinition：用于定义 bean 信息的类，包括 bean 的 class 类型，构造函数，属性值等信息。
- BeanDefinitionRegistry：定义注册 BeanDefinition 的方法
- SingletonBeanRegistry 及其实现类 DefaultSingletonBeanRegistry，定义添加和获取单例 bean 的方法。
- BeanFactory 提供 Bean 的获取方法，抽象类 AbstractBeanFactory 实现 BeanFactory 并结合「模板方法设计模式」来统一方法的调用流程。AbstractAutowireCapableBeanFactory 继承 AbstractBeanFactory 类，实现 AutowireCapableBeanFactory，其作用是提供自动装配的功能。



<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202409290309016.png"/>

### A

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/sbs-small-spring%E5%9B%BE%E7%BA%B8-1-1.drawio.svg"/>

`BeanFactory` 提供 「获取 Bean」的能力

`DefaultSingletonBeanRegistry` 提供 「单例 Bean」的处理能力：获取单例 Bean，注册单例 Bean

`AbstractBeanFactory` 重写了 `BeanFactory#getBean` 方法，默认获取单例 Bean。如果单例 Bean 不存在，则尝试获取 Bean 定义，再根据 Bean 定义创建 Bean 对象

`AbstractAutowireCapableBeanFactory` 重写了 `AbstractBeanFactory#createBean` 方法，创建 Bean 并注册单例 Bean。

`DefaultListableBeanFactory` 是一个具有多个能力的类，「注册 Bean 定义」、「获取 Bean 定义」、「获取 Bean」。 

测试类

```java
  @Test
  public void test_BeanFactory() {
    // 1.初始化 BeanFactory
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    // 2.注册 bean
    BeanDefinition beanDefinition = new BeanDefinition(HelloService.class);
    beanFactory.registerBeanDefinition("helloService", beanDefinition);

    // 3.第一次获取 bean
    HelloService userService = (HelloService) beanFactory.getBean("helloService");
    userService.sayHello();

    // 4.第二次获取 bean from Singleton
    HelloService userService_singleton = (HelloService) beanFactory.getBean("helloService");
    userService_singleton.sayHello(); 
  }
```

### R

通过 **继承** 和 **实现**，让 `AbstractBeanFactory` 变成了一个功能强大且完整的抽象类，采用「模板方法」设计模式，让 `getBean()` 方法遵循一定的模板实现。

- 尝试获取，如果存在就返回
- 如果不存在，就通过 `getBeanDefinition` 获取 Bean 定义，从而 `createBean` 创建出对应的 Bean 对象。

上面的模板方法本质上是交给子类来完成实现。

- `getBeanDefinition()` 交给 `DefaultListableBeanFactory` 来提供默认实现。
- `createBean()` 交给 `AbstractAutowireCapableBeanFactory` 来提供默认实现。



## Bean 实例化策略

> 代码分支：[bean-instantiation-strategy](https://github.com/DoubleW2w/sbs-small-spring/tree/bean-instantiation-strategy)

### S

在 Bean 的实例化过程，我们是通过反射获取 **无参构造函数** 去创建出来的对象。

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202409292254095.png"/>

但是当我们想要通过其他构造函数去实例化时，就会抛出异常

```
org.springframework.beans.factory.BeansException: Instantiation of bean failed

	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:20)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:23)
	at org.springframework.beans.factory.BeanDefinitionAndBeanDefinitionRegistryTest.test_BeanFactory(BeanDefinitionAndBeanDefinitionRegistryTest.java:24)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
Caused by: java.lang.NoSuchMethodException: org.springframework.beans.factory.HelloService.<init>()
	at java.base/java.lang.Class.getConstructor0(Class.java:3349)
	at java.base/java.lang.Class.getDeclaredConstructor(Class.java:2553)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:18)
	... 5 more
```



### T

完善含有构造函数的 Bean 对象的实例化策略

- Java 自身的构造函数
- 使用 Cglib 动态创建 Bean 对象

针对 bean 的实例化，抽象出一个实例化策略的接口 InstantiationStrategy，有两个实现类：

- SimpleInstantiationStrategy，使用 bean 的构造函数来实例化
- CglibSubclassingInstantiationStrategy，使用 CGLIB 动态生成子类

### A

```java
public interface InstantiationStrategy {
  /**
   * 根据Bean定义来实例化Bean
   *
   * @param beanDefinition Bean定义
   * @param beanName Bean名称
   * @param ctor 构造函数
   * @param args 构造函数参数
   * @return Bean对象
   * @throws BeansException 异常
   */
  Object instantiate(
      BeanDefinition beanDefinition, String beanName, Constructor ctor, Object[] args)
      throws BeansException;
}
```

```java
public class SimpleInstantiationStrategy implements InstantiationStrategy {

  @Override
  public Object instantiate(
      BeanDefinition beanDefinition, String beanName, Constructor ctor, Object[] args)
      throws BeansException {
    if (beanDefinition == null) {
      throw new BeansException("beanDefinition is not exist");
    }
    Class clazz = beanDefinition.getBeanClass();
    try {
      if (null != ctor) {
        return clazz.getDeclaredConstructor(ctor.getParameterTypes()).newInstance(args);
      } else {
        return clazz.getDeclaredConstructor().newInstance();
      }
    } catch (NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new BeansException("Failed to instantiate [" + clazz.getName() + "]", e);
    }
  }
}
```

```java
public class CglibSubclassingInstantiationStrategy implements InstantiationStrategy {
  @Override
  public Object instantiate(
      BeanDefinition beanDefinition, String beanName, Constructor ctor, Object[] args)
      throws BeansException {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(beanDefinition.getBeanClass());
    enhancer.setCallback(
        new NoOp() {
          @Override
          public int hashCode() {
            return super.hashCode();
          }
        });
    // 无参
    if (null == ctor) return enhancer.create();
    // 有参
    return enhancer.create(ctor.getParameterTypes(), args);
  }
}
```

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/sbs-small-spring%E5%9B%BE%E7%BA%B8-2-Bean%E7%9A%84%E5%AE%9E%E4%BE%8B%E5%8C%96%E7%AD%96%E7%95%A5-1-1.drawio.svg"/>

### R



## 为 Bean 对象注入属性

> 代码分支：[bean-instantiation-strategy](https://github.com/DoubleW2w/sbs-small-spring/tree/bean-instantiation-strategy)

### S

目前实现了无参构造、有参构造的实例化，但不能每次实例化 Bean，都要以构造函数的方式注入属性。

对于属性的填充不只是 int、Long、String，还包括还没有实例化的对象属性，都需要在 Bean 创建时进行填充操作。

### T

通过将属性封装为 `PropertyValue` 类，增加针对 Bean 对象提供注入属性的方式。

### A

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202410031737160.png"/>

```java
public class PopulateBeanWithPropertyValuesTest {
  @Test
  public void test_BeanFactoryInstantiationStrategy() {
    // 1.初始化 BeanFactory
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    // 2.注册 bean
    beanFactory.registerBeanDefinition("worldService", new BeanDefinition(WorldService.class));

    // 3. helloService 设置属性[name、worldService]
    PropertyValues propertyValues = new PropertyValues();
    propertyValues.addPropertyValue(
        new PropertyValue("worldService", new BeanReference("worldService")));

    // 4. 注册 bean
    beanFactory.registerBeanDefinition(
        "helloService", new BeanDefinition(HelloService.class, propertyValues));

    // 5.获取 bean
    HelloService userService = (HelloService) beanFactory.getBean("helloService","zhangsan");
    userService.sayHello();
    userService.sayWorld();
  }
}

public class HelloService {

  private String name;
  @Setter private WorldService worldService;

  public HelloService(String name) {
    this.name = name;
  }

  public HelloService() {}

  public String sayHello() {
    System.out.println("name=" + name);
    return "hello";
  }

  public void sayWorld() {
    worldService.world();
  }
}

public class WorldService {
  private String id = "world";

  public WorldService() {}

  public WorldService(String id) {
    this.id = id;
  }

  public String world() {
    System.out.println(id);
    return id;
  }
}
```

### R

- 一开始是通过无参构造函数完成实例化 Bean 对象，后面增加了有参构造函数的方式，这也算是一种变相的注入属性。
- 本节增加 Bean 属性的注入方式，通过反射的方式进行填充，不过这里使用的 hutool 的工具类来处理。

## 从 Spring.xml 解析和注册 Bean 对象

> 代码分支：[resource-load-registry-bean](https://github.com/DoubleW2w/sbs-small-spring/tree/resource-load-registry-bean)

### S

通过单元测试进行手动操作 Bean 对象的定义、注册和属性填充，以及最终获取对象调用方法，但在 Spring 中，一开始是通过 xml 方式进行配置的。

### T

将注册 Bean 的过程转移到 XML 配置文件中，完成 Spring 配置的读取、解析、注册。

### A

#### 最简单实现，通过 xml 标签的解析。

```java
public class XmlBeanDefinitionReader {

  private final DefaultListableBeanFactory defaultListableBeanFactory;

  public XmlBeanDefinitionReader(DefaultListableBeanFactory defaultListableBeanFactory) {
    this.defaultListableBeanFactory = defaultListableBeanFactory;
  }

  public void doLoadBeanDefinitions(InputStream inputStream) throws Exception {
    Document doc = XmlUtil.readXML(inputStream);
    Element root = doc.getDocumentElement();
    NodeList childNodes = root.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      // 判断元素
      if (!(childNodes.item(i) instanceof Element)) continue;
      // 判断对象
      if (!"bean".equals(childNodes.item(i).getNodeName())) continue;

      // 解析标签
      Element bean = (Element) childNodes.item(i);
      String id = bean.getAttribute("id");
      String name = bean.getAttribute("name");
      String className = bean.getAttribute("class");

      // 获取 Class，方便获取类中的名称
      Class<?> clazz = Class.forName(className);
      // 优先级 id > name
      String beanName = StrUtil.isNotBlank(id) ? id : name;
      if (StrUtil.isNotBlank(beanName)) {
        beanName = StrUtil.lowerFirst(clazz.getSimpleName());
      }
      // 定义Bean
      BeanDefinition beanDefinition = new BeanDefinition(clazz);

      // 读取属性并填充
      NodeList beanChildNodes = bean.getChildNodes();
      for (int j = 0; j < beanChildNodes.getLength(); j++) {
        if (!(beanChildNodes.item(j) instanceof Element)) continue;
        if (!"property".equals(beanChildNodes.item(j).getNodeName())) continue;
        // 解析标签：property
        Element property = (Element) beanChildNodes.item(j);
        String attrName = property.getAttribute("name");
        String attrValue = property.getAttribute("value");
        String attrRef = property.getAttribute("ref");
        // 获取属性值：引入对象、值对象
        Object value = StrUtil.isNotBlank(attrRef) ? new BeanReference(attrRef) : attrValue;
        // 创建属性信息
        PropertyValue propertyValue = new PropertyValue(attrName, value);
        beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
      }

      // 如果存在相同的bean定义，就抛出异常
      if (defaultListableBeanFactory.hasBeanDefinition(beanName)) {
        throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
      }
      // 注册 BeanDefinition
      defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
    }
  }
}
```

测试类

```java
public class ResourceLoadRegistryBeanTest {
  @Test
  public void test_xml() throws Exception {
    // 1.初始化 BeanFactory
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    // 2. 读取配置文件&注册Bean
    XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
    reader.doLoadBeanDefinitions(ResourceUtil.getStream("spring-config.xml"));

    // 3. 获取Bean对象调用方法
    HelloService helloService = (HelloService) beanFactory.getBean("helloService");
    String result = helloService.sayHello();
    helloService.sayWorld();
    System.out.println("测试结果：" + result);
  }
}
```

资源类的加载，我们通过 hutool 的 `ResourceUtil` 来解决，而 xml 内容的解析通过我们自定义的 `XmlBeanDefinitionReader` 来完成

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!-- spring-config.xml -->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="worldService" class="org.springframework.beans.factory.WorldService">
    </bean>

    <bean id="helloService" class="org.springframework.beans.factory.HelloService">
        <property name="worldService" ref="worldService"/>
        <property name="name" value="lisi"/>
    </bean>
</beans>
```

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202410032016748.png"/>

### R

- 解析配置文件，注册 Bean 信息
- 通过 Bean 工厂，获取 Bean，以及对应的调用方法。