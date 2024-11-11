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

#### 参考 Spring 的实现

```java
public interface BeanDefinitionReader {
  BeanDefinitionRegistry getRegistry();

  ResourceLoader getResourceLoader();

  void loadBeanDefinitions(Resource resource) throws BeansException;

  void loadBeanDefinitions(Resource... resources) throws BeansException;

  void loadBeanDefinitions(String location) throws BeansException;
}
```

```java
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
  private final BeanDefinitionRegistry registry;

  private ResourceLoader resourceLoader;

  protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
    this(registry, new DefaultResourceLoader());
  }

  public AbstractBeanDefinitionReader(
      BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
    this.registry = registry;
    this.resourceLoader = resourceLoader;
  }

  @Override
  public BeanDefinitionRegistry getRegistry() {
    return registry;
  }

  @Override
  public ResourceLoader getResourceLoader() {
    return resourceLoader;
  }
}
```

```java
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

  public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
    super(registry);
  }

  public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
    super(registry, resourceLoader);
  }

  protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException {
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

      if (getRegistry().containsBeanDefinition(beanName)) {
        throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
      }
      // 注册 BeanDefinition
      getRegistry().registerBeanDefinition(beanName, beanDefinition);
    }
  }

  @Override
  public void loadBeanDefinitions(Resource resource) throws BeansException {
    try {
      try (InputStream inputStream = resource.getInputStream()) {
        doLoadBeanDefinitions(inputStream);
      }
    } catch (IOException | ClassNotFoundException e) {
      throw new BeansException("IOException parsing XML document from " + resource, e);
    }
  }

  @Override
  public void loadBeanDefinitions(Resource... resources) throws BeansException {
    for (Resource resource : resources) {
      loadBeanDefinitions(resource);
    }
  }

  @Override
  public void loadBeanDefinitions(String location) throws BeansException {
    ResourceLoader resourceLoader = getResourceLoader();
    Resource resource = resourceLoader.getResource(location);
    loadBeanDefinitions(resource);
  }
}
```



### R

- 解析配置文件，注册 Bean 信息
- 通过 Bean 工厂，获取 Bean，以及对应的调用方法。



## BeanFactoryPostProcessor 和 BeanPostProcessor



## 实现应用上下文

### S

已经实现了通过解析 XML 配置文件完成 Bean 对象的注册，调用。

### T

- 减少 `DefaultListableBeanFactory`，`XmlBeanDefinitionReader` 的直接使用，因为这些类是直接面向 Spring 内部的，而应用上下文 ApplicationContext 面向 spring 的使用者，应用场合使用 ApplicationContext
  - `ApplicationContext` 是 Spring 的 **IoC 容器**，负责管理应用中的对象（也就是 Bean）。

  - 负责依赖注入（Dependency Injection, DI）

  - 支持事件模型（Event Publishing），它允许应用程序发布和监听事件。

  - 提供了对国际化（i18n）的支持。

  - 管理 Bean 的生命周期

  - 资源加载

- 增加用户对 Bean 从注册到实例化的自定义扩展机制：`BeanFactoryPostProcessor` 和 `BeanPostProcessor`

- `BeanFactoryPostProcessor` ：允许在 Bean 对象注册后但未实例化之前，对 Bean 的定义信息 `BeanDefinition` 执行修改操作。

- `BeanPostProcessor`： 是在 Bean 对象实例化之后修改 Bean 对象，也可以替换 Bean 对象。

### A

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202410070123682.png"/>

在实例化 Bean 的前置和后置添加接口处理类，并交给用户进行自定义实现。

#### 在使用上下文之前

假设定义好的 `BeanFactoryPostProcessor` 和 `BeanPostProcessor` 如下

```java
public interface BeanFactoryPostProcessor {
  /**
   * 在所有BeanDefinition加载完成后，但在bean实例化之前，提供修改BeanDefinition属性值的机制
   *
   * @param beanFactory bean工厂
   * @throws BeansException 异常
   */
  void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}


public interface BeanPostProcessor {
  /**
   * 在bean执行初始化方法之前执行此方法
   *
   * @param bean
   * @param beanName
   * @return
   * @throws BeansException
   */
  Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

  /**
   * 在bean执行初始化方法之后执行此方法
   *
   * @param bean
   * @param beanName
   * @return
   * @throws BeansException
   */
  Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
```



AutowireCapableBeanFactory 负责执行所有用户自定义的 BeanPostProcessors 的功能。

```java
public interface AutowireCapableBeanFactory extends BeanFactory {
  /**
   * 执行 BeanPostProcessors 的 postProcessBeforeInitialization 方法
   *
   * @param existingBean 存在的bean对象
   * @param beanName bean名称
   * @return bean对象
   * @throws BeansException bean异常
   */
  Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
      throws BeansException;

  /**
   * 执行 BeanPostProcessors的 postProcessAfterInitialization 方法
   *
   * @param existingBean 存在的bean对象
   * @param beanName bean名称
   * @return bean对象
   * @throws BeansException bean异常
   */
  Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
      throws BeansException;
}
```

 

那么测试类应该如下

```java
@Test
public void test_BeanFactoryPostProcessorAndBeanPostProcessor() {
  // 1.初始化 BeanFactory
  DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

  // 2. 读取配置文件&注册Bean
  XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
  reader.loadBeanDefinitions("classpath:spring-config.xml");

  // 3. BeanDefinition 加载完成 & Bean实例化之前，修改 BeanDefinition 的属性值
  MyBeanFactoryPostProcessor beanFactoryPostProcessor = new MyBeanFactoryPostProcessor();
  beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

  // 4. Bean实例化之后，修改 Bean 属性信息
  MyBeanPostProcessor beanPostProcessor = new MyBeanPostProcessor();
  beanFactory.addBeanPostProcessor(beanPostProcessor);

  // 5. 获取Bean对象调用方法
  HelloService helloService = beanFactory.getBean("helloService", HelloService.class);
  String result = helloService.sayHello();
  System.out.println("测试结果：" + result);
}
```

从上面的测试类就可以知道，步骤就是通过 `DefaultListableBeanFactory` 来加载配置文件，在结合 `BeanPostFactoryProcessor` 和 `BeanPostProcessor` 的作用。

因为没有上下文的连接作用，因此 `BeanPostFactoryProcessor` 和 `BeanPostProcessor` 的效果是通过硬编码的方式进行调用。

而上下文就是将 1、2、3、4 整合起来的。

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

    <bean class="org.springframework.beans.factory.MyBeanPostProcessor"/>
    <bean class="org.springframework.beans.factory.MyBeanFactoryPostProcessor"/>
</beans>
```

#### 使用应用上下文

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202410080136709.png"/>

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/sbs-small-spring%E5%9B%BE%E7%BA%B8-5-%E5%AE%9E%E7%8E%B0%E5%BA%94%E7%94%A8%E4%B8%8A%E4%B8%8B%E6%96%87.drawio-1.svg"/>

```java
@Test
public void test_ApplicationContext() {
  // 1.初始化 BeanFactory
  ClassPathXmlApplicationContext applicationContext =
    new ClassPathXmlApplicationContext("classpath:spring-config.xml");

  // 2. 获取Bean对象调用方法
  HelloService helloService = applicationContext.getBean("helloService", HelloService.class);
  String result = helloService.sayHello();
  System.out.println("测试结果：" + result);
}
```

本质上就是通过 `ClassPathXmlApplicationContext` 来去调用 `AbstractApplicationContext#refresh()` 来完成上下文的操作。

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
  implements ConfigurableApplicationContext { 
  @Override
  public void refresh() throws BeansException {
    // 1. 创建 BeanFactory，并加载 BeanDefinition
    refreshBeanFactory();

    // 2. 获取 BeanFactory
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();

    // 3. 在 Bean 实例化之前，执行 BeanFactoryPostProcessor (Invoke factory processors registered as beans in
    // the context.)
    invokeBeanFactoryPostProcessors(beanFactory);

    // 4. BeanPostProcessor 需要提前于其他 Bean 对象实例化之前执行注册操作
    registerBeanPostProcessors(beanFactory);

    // 5. 提前实例化单例Bean对象
    beanFactory.preInstantiateSingletons();
  }
  // 省略其他....
}
```

```java
// 1. 创建 BeanFactory，并加载 BeanDefinition
refreshBeanFactory();

// 等价于
DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
loadBeanDefinitions(beanFactory);


// 3. 在 Bean 实例化之前，执行 BeanFactoryPostProcessor (Invoke factory processors registered as beans in
// the context.)
invokeBeanFactoryPostProcessors(beanFactory);

// 等价于
MyBeanFactoryPostProcessor beanFactoryPostProcessor = new MyBeanFactoryPostProcessor();
beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

// 4. BeanPostProcessor 需要提前于其他 Bean 对象实例化之前执行注册操作
registerBeanPostProcessors(beanFactory);

//等价于
MyBeanPostProcessor beanPostProcessor = new MyBeanPostProcessor();
beanFactory.addBeanPostProcessor(beanPostProcessor);
```

### R

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/sbs-small-spring%E5%9B%BE%E7%BA%B8-5-%E5%AE%9E%E7%8E%B0%E5%BA%94%E7%94%A8%E4%B8%8A%E4%B8%8B%E6%96%87.drawio-2.png"/>

很多抽象类都是通过继承的机制，来增加自己的职责，直到 `ClassPathXmlApplicationContext` 变成一个很完善的类，用户可以直接进行使用来进行测试。

`AbstractApplicationContext#refresh()` 方法本质上就是一个「模板方法」的设计模式。



## 初始化和销毁方法

> 代码分支: [init-method-and-destroy-method](https://github.com/DoubleW2w/sbs-small-spring/tree/init-method-and-destroy-method)

### S

为了给 Bean 提供更全面的生命周期管理，希望可以在 Bean 初始化过程中，执行一些别的操作。比如预加载数据或者在程序关闭时销毁资源等。

### T

在 XML 配置文件中配置 **初始化方法 init-method** 和 **销毁方法 destroy-method** 。

- `init-method` 在 Bean 实例化后，并且所有的属性都已经设置完成时被调用。这个阶段通常发生在以下步骤之后：
  - **Bean 实例化**：Spring 容器根据配置创建 Bean 实例。
  - **属性赋值**：Spring 容器将配置中定义的属性注入到 Bean 中。
  - **Bean 后处理**：如果有 `BeanPostProcessor`，它们会在此阶段执行。

- `destroy-method` 在 Bean 被销毁之前调用。
  - **容器关闭**：当 Spring 容器被关闭时
  - **单例 Bean 销毁**：对于单例 Bean，在容器关闭时执行销毁方法。


### A

在 spring.xml 配置中添加 `init-method、destroy-method` 两个注解

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!-- spring-config.xml -->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="worldService" class="org.springframework.beans.factory.WorldService"           init-method="initDataMethod"
        destroy-method="destroyDataMethod">
  </bean>

  <bean id="helloService" class="org.springframework.beans.factory.HelloService">
    <property name="worldService" ref="worldService"/>
    <property name="name" value="lisi"/>
  </bean>
</beans>
```

在配置文件加载过程中，会将配置统一加载到 BeanDefinition 的属性中，这样就可以通过 **反射** 的方式来调用方法信息。如果是以接口实现的方式，那么直接通过 Bean 对象调用对应接口的方法接口，即 `((InitializingBean) bean).afterPropertiesSet()`。

注册销毁方法的信息到 `DefaultSingletonBeanRegistry` 类中的 disposableBeans 属性里，而关于销毁方法需要在虚拟机执行关闭之前进行操作，所以这里需要用到一个注册钩子的操作，比如 `Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("close！")));`。

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202410081112729.png"/>



定义好「初始化接口」和「销毁接口」

```java
public interface DisposableBean {
  /**
   * 销毁方法
   * @throws Exception
   */
  void destroy() throws Exception;
}

```

```java
public interface InitializingBean {
  /**
   * Bean 处理了属性填充后调用
   *
   * @throws Exception
   */
  void afterPropertiesSet() throws Exception;
}
```



定义一个「销毁适配器」，目的是适配反射调用和接口直接调用的方法，本质上初始化也是如此，不过初始化方法是在内部的过程中，也是可以提取出一个适配器出来。

```java
public class DisposableBeanAdapter implements DisposableBean {
  /** bean对象 */
  private final Object bean;

  /** bean名称 */
  private final String beanName;

  /** 销毁方法 */
  private String destroyMethodName;

  public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
    this.bean = bean;
    this.beanName = beanName;
    this.destroyMethodName = beanDefinition.getDestroyMethodName();
  }

  @Override
  public void destroy() throws Exception {
    // 1. 实现接口 DisposableBean
    if (bean instanceof DisposableBean) {
      ((DisposableBean) bean).destroy();
    }

    // 2. 注解配置 destroy-method {判断是为了避免二次执行销毁}
    if (StrUtil.isNotEmpty(destroyMethodName)
        && !(bean instanceof DisposableBean && "destroy".equals(this.destroyMethodName))) {
      Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
      if (null == destroyMethod) {
        throw new BeansException(
            "Couldn't find a destroy method named '"
                + destroyMethodName
                + "' on bean with name '"
                + beanName
                + "'");
      }
      destroyMethod.invoke(bean);
    }
  }
}
```





```java
//AbstractAutowireCapableBeanFactory 
private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition)
  throws Exception {
  // 1. 实现接口 InitializingBean
  if (bean instanceof InitializingBean) {
    ((InitializingBean) bean).afterPropertiesSet();
  }

  // 2. 注解配置 init-method {判断是为了避免二次执行初始化}
  String initMethodName = beanDefinition.getInitMethodName();
  if (StrUtil.isNotEmpty(initMethodName) && !(bean instanceof InitializingBean)) {
    Method initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
    if (null == initMethod) {
      throw new BeansException(
        "Could not find an init method named '"
        + initMethodName
        + "' on bean with name '"
        + beanName
        + "'");
    }
    initMethod.invoke(bean);
  }
}
```

真正使用的地方还是在 `createBean` 方法中，在 **创建 Bean** 的时候，注册销毁方法信息和初始化方法信息。

```java
////AbstractAutowireCapableBeanFactory
protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args)
      throws BeansException {
    Object bean = null;
    try {
      bean = createBeanInstance(beanDefinition, beanName, args);
      // 给 Bean 填充属性
      applyPropertyValues(beanName, bean, beanDefinition);
      // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
      bean = initializeBean(beanName, bean, beanDefinition);
    } catch (Exception e) {
      throw new BeansException("Instantiation of bean failed", e);
    }

    // 注册实现了 DisposableBean 接口的 Bean 对象
    registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

    registerSingleton(beanName, bean);
    return bean;
  }
```

```java
  @Test
  public void test_ApplicationContext() {
    // 1.初始化 BeanFactory
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-config-init-method-destroy-method.xml");
    applicationContext.registerShutdownHook();

    // 2. 获取Bean对象调用方法
    HelloService helloService = applicationContext.getBean("helloService", HelloService.class);
    String result = helloService.sayHello();
    System.out.println("测试结果：" + result);
  }
```

为了确保销毁方法在虚拟机关闭之前执行，向虚拟机中注册一个钩子方法，查看 `AbstractApplicationContext#registerShutdownHook`（非 web 应用需要手动调用该方法）。当然也可以手动调用 `ApplicationContext#close` 方法关闭容器。

```java
//AbstractApplicationContext
public void registerShutdownHook() {
  Runtime.getRuntime().addShutdownHook(new Thread(this::close));
}

public void close() {
  getBeanFactory().destroySingletons();
}
```

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202410081228249.png"/>

<p style="text-align:center"> 图片来自：小傅哥 </p>



bean 的生命周期如下：

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202410081339508.png"/>

<p style="text-align:center"> 图片来自：DerekYRC/mini-spring </p>

### R

`AbstractAutowireCapableBeanFactory` 完成初始方法

`AbstractApplicationContext` 处理销毁动作

各种 BeanFacotry 的 UML 类图关系有点凌乱，需要整理，主要弄清楚它们各自的职责比较重要。

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/sbs-small-spring%E5%9B%BE%E7%BA%B8-6-%E5%88%9D%E5%A7%8B%E5%8C%96%E6%96%B9%E6%B3%95%E5%92%8C%E9%94%80%E6%AF%81%E6%96%B9%E6%B3%95.drawio-2.png"/>

- **BeanFacotry**：最顶层的 bean 工厂接口，提供了获取 Bean 对象的能力
- **HierarchicalBeanFactory**：扩展了「层次化」的 Bean 工厂结构能力，比如父子级
- **AutowireCapableBeanFactory**：扩展了「自动装配」和「创建 Bean」，还有「BeanPostProcessor 支持」的能力
- **ListableBeanFactory**：提供查询和列表功能
- **ConfigurableBeanFactory**：提供配置和管理 Bean 的能力，比如添加 BeanPostProcessor 的 bean 对象
- **ConfigurableListableBeanFactory**：是整合 `ListableBeanFactory` 和 `ConfigurableBeanFactory` 的能力
- **AbstractBeanFactory**：是 `BeanFactory` 的抽象类，供了一些基本的功能和方法，以支持具体的 BeanFactory 实现
- **AbstractAutowireCapableBeanFactory**： 扩展了 `AbstractBeanFactory` 类，并实现了 `AutowireCapableBeanFactory` 接口，提供自动装配的能力和相关的管理功能
- **DefaultListableBeanFactory**：是面向 Spring 内部使用的，功能很完善，Bean 的定义和注册，实例化，查询 Bean

总之，一般都是通过定义顶层接口，然后定义抽象类实现接口，提供基础的功能或者模板形式，最后使用的是一个默认实现类。

## Aware 接口感知容器对象

> 代码分支: [aware-interface](https://github.com/DoubleW2w/sbs-small-spring/tree/aware-interface)

### S

目前的功能实现有 Bean 对象的定义和注册、BeanFactoryPostProcessor、BeanPostProcessor、InitializingBean、DisposableBean、以及在 XML 新增的一些配置处理，初始化方法和销毁方法等。

但如果我们想获得 Spring 框架提供的 BeanFactory、ApplicationContext、BeanClassLoader 等这些能力做一些扩展框架的使用时该怎么操作呢？

### T

通过 `Aware` 接口来标识这是一个感知容器，具体的子类定义和实现能感知容器中的相关对象，用于让 Spring 容器中的 Bean 获取某些特定的上下文信息或资源。

- 以什么方式去获取。
- `Aware` 的内容如何在 Spring 的管理中承接。

### A

#### 接口定义

```java
public interface Aware {}


public interface ApplicationContextAware extends Aware {
  void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}

public interface BeanClassLoaderAware extends Aware {
  void setBeanClassLoader(ClassLoader classLoader);
}

public interface BeanFactoryAware extends Aware {
  void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}

public interface BeanNameAware extends Aware {
  void setBeanName(String name);
}
```



#### ApplicationContextAware 处理

`refresh()` 方法就是 Spring 容器的模板操作过程，由于 ApplicationContext 的获取并不能直接在创建 Bean 时候就可以拿到，所以添加 `ApplicationContextAwareProcessor` 的操作，让继承自 `ApplicationContextAware` 的 Bean 对象都能感知所属的 `ApplicationContext`，

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
  implements ConfigurableApplicationContext {  
  public void refresh() throws BeansException {
    // 1. 创建 BeanFactory，并加载 BeanDefinition
    refreshBeanFactory();

    // 2. 获取 BeanFactory
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();

    // 添加 ApplicationContextAwareProcessor，让继承自ApplicationContextAware的bean能感知bean
    beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

    // 3. 在 Bean 实例化之前，执行 BeanFactoryPostProcessor (Invoke factory processors registered as beans in
    // the context.)
    invokeBeanFactoryPostProcessors(beanFactory);

    // 4. BeanPostProcessor 需要提前于其他 Bean 对象实例化之前执行注册操作
    registerBeanPostProcessors(beanFactory);

    // 5. 提前实例化单例Bean对象
    beanFactory.preInstantiateSingletons();
  }
}
```

#### 感知调用操作

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
    implements AutowireCapableBeanFactory {
private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
    // 感知beanFactory
    // invokeAwareMethods
    if (bean instanceof Aware) {
      if (bean instanceof BeanFactoryAware) {
        ((BeanFactoryAware) bean).setBeanFactory(this);
      }
      if (bean instanceof BeanClassLoaderAware) {
        ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
      }
      if (bean instanceof BeanNameAware) {
        ((BeanNameAware) bean).setBeanName(beanName);
      }
    }

    // 1. 执行 BeanPostProcessor Before 处理
    Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

    // 待完成内容：invokeInitMethods(beanName, wrappedBean, beanDefinition);
    try {
      invokeInitMethods(beanName, wrappedBean, beanDefinition);
    } catch (Exception e) {
      throw new BeansException("Invocation of init method of bean[" + beanName + "] failed", e);
    }

    // 2. 执行 BeanPostProcessor After 处理
    wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    return wrappedBean;
  }
}
```



#### 目前流程图

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/sbs-small-spring%E5%9B%BE%E7%BA%B8-7-Aware%E6%84%9F%E7%9F%A5%E5%AE%B9%E5%99%A8%E5%AF%B9%E8%B1%A1-1.png"/>

#### 测试

```java
public class AwareInterfaceTest {
  @Test
  public void test() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
      new ClassPathXmlApplicationContext("classpath:spring-config-aware-interface.xml");
    HelloWorldService helloService = applicationContext.getBean("helloWorldService", HelloWorldService.class);
    assertThat(helloService.getApplicationContext()).isNotNull();
    assertThat(helloService.getBeanFactory()).isNotNull();
  }
}

```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!-- spring-config.xml -->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd">

  <bean id="helloWorldService" class="org.springframework.beans.factory.HelloWorldService">
  </bean>
</beans>
```

```java
public class HelloWorldService
  implements BeanNameAware, BeanClassLoaderAware, ApplicationContextAware, BeanFactoryAware {

  private ApplicationContext applicationContext;

  private BeanFactory beanFactory;

  public String sayHelloWorld() {
    System.out.println("hello world");
    return "hello world";
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public BeanFactory getBeanFactory() {
    return beanFactory;
  }

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    System.out.println("ClassLoader：" + classLoader);
  }

  @Override
  public void setBeanName(String name) {
    System.out.println("Bean Name is：" + name);
  }
}
```

### R

Bean 的生命周期简单来说只有四步：实例化-填充属性-使用-销毁。本章节主要实现 Aware 的感知接口的四个继承接口 BeanNameAware, BeanClassLoaderAware, ApplicationContextAware, BeanFactoryAware。

## 对象作用域和 FactoryBean

> 代码分支: [bean-scope-and-factory-bean](https://github.com/DoubleW2w/sbs-small-spring/tree/bean-scope-and-factory-bean)

### S

如果我们想创建出原型对象而不是单例对象呢？

### T

实现 FactoryBean 来帮助用户创建更为复杂的 Bean 对象。

- 控制 Bean 对象的创建过程，包括对象的初始化、配置以及返回的对象类型等。
- 提供 Bean 对象的类型信息
- 单例对象和原型对象的支持

### A

我们知道创建 Bean 对象的入口是 `getBean()` 方法，真正的逻辑是在 `createBean()` 方法中。

因此增加了从 FactoryBeanRegistrySupport 中创建 Bean 的方式。

```java
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport
  implements ConfigurableBeanFactory {
  protected <T> T doGetBean(final String name, final Object[] args) {
    Object sharedInstance = getSingleton(name);
    if (sharedInstance != null) {
      // 如果是 FactoryBean，则需要调用 FactoryBean#getObject
      return (T) getObjectForBeanInstance(sharedInstance, name);
    }
		// 否则直接返回
    BeanDefinition beanDefinition = getBeanDefinition(name);
    Object bean = createBean(name, beanDefinition, args);
    return (T) getObjectForBeanInstance(bean, name);
  }

  private Object getObjectForBeanInstance(Object beanInstance, String beanName) {
    // bean实例不是 FactoryBean 类型就直接返回bean实例
    if (!(beanInstance instanceof FactoryBean)) {
      return beanInstance;
    }

    Object object = getCachedObjectForFactoryBean(beanName);
    // 如果缓存中没有找到对象，则通过FactoryBean创建
    if (object == null) {
      FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
      object = getObjectFromFactoryBean(factoryBean, beanName);
    }
    return object;
  }
}
```

createBean 执行对象创建、属性填充、依赖加载、前置后置处理、初始化等操作后，就要开始做执行判断整个对象是否是一个 FactoryBean 对象。

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
    implements AutowireCapableBeanFactory { 
@Override
  protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args)
      throws BeansException {
    Object bean = null;
    try {
      bean = createBeanInstance(beanDefinition, beanName, args);
      // 给 Bean 填充属性
      applyPropertyValues(beanName, bean, beanDefinition);
      // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
      bean = initializeBean(beanName, bean, beanDefinition);
    } catch (Exception e) {
      throw new BeansException("Instantiation of bean failed", e);
    }

    // 注册实现了 DisposableBean 接口的 Bean 对象
    registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);
    // 判断 SCOPE_SINGLETON、SCOPE_PROTOTYPE
    if (beanDefinition.isSingleton()) {
      addSingleton(beanName, bean);
    }
    return bean;
  }
}
```

<p> </p>

在加载 BeanDefinition 的方法中也添加了对 `scope` 属性的解析

```java
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
  protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException 
    Document doc = XmlUtil.readXML(inputStream);
  Element root = doc.getDocumentElement();
  NodeList childNodes = root.getChildNodes();

  for (int i = 0; i < childNodes.getLength(); i++) {
		//....
    String beanScope = bean.getAttribute("scope");

    // 获取 Class，方便获取类中的名称
    Class<?> clazz = Class.forName(className);
    // 优先级 id > name
    String beanName = StrUtil.isNotEmpty(id) ? id : name;
    if (StrUtil.isEmpty(beanName)) {
      beanName = StrUtil.lowerFirst(clazz.getSimpleName());
    }

    // 定义Bean
    BeanDefinition beanDefinition = new BeanDefinition(clazz);
    beanDefinition.setInitMethodName(initMethod);
    beanDefinition.setDestroyMethodName(destroyMethodName);

    if (StrUtil.isNotEmpty(beanScope)) {
      beanDefinition.setScope(beanScope);
    }
		//...
  }
}
```

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/sbs-small-spring%E5%9B%BE%E7%BA%B8-8-%E5%AF%B9%E8%B1%A1%E4%BD%9C%E7%94%A8%E5%9F%9F%E5%92%8CFacotryBean.drawio-1.png"/>

### R

对象作用域简单分为 `singleton` 和 `prototype`，单例的情况下，会从缓存中查看 bean 对象是否已经生成过，否则调用生成方法。原型情况下，不会做判断，而是每次都会调用生成方法。



FactoryBean 可以用于创建复杂的或可配置的 Spring bean 实例。

```java
public interface FactoryBean<T> {
  T getObject() throws Exception;

  Class<?> getObjectType();

  boolean isSingleton();
}
```

从上面就可以判断它的作用有以下几点：

1. 创建对象：实现这个接口的类需要实现 getObject() 方法，该方法返回所需对象的实例。这个创建过程中，用户可以控制创建逻辑等。
2. 提供对象的类型信息：getObjectType() 方法返回创建的对象的类型。
3. 单例和原型的判断支持：isSingleton() 方法返回一个布尔值，指示返回的对象是单例还是原型。



## 容器事件和事件监听器

> 代码分支: [application-event-and-listener](https://github.com/DoubleW2w/sbs-small-spring/tree/aware-interface)


### S

Spring 的事件功能是基于观察者模式——**当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新。**

在 Event 事件功能中，可以提供 **事件的定义**、**事件的发布**、**事件的监听** 来完成一些自定义的动作。

### T

本章节目标是 **实现事件功能**

- 初始化
- 定义出事件类、监听类、发布类
- 事件监听
- 事件发布
- 广播器——接收到事件推送时进行分析处理符合监听事件接受者感兴趣的事件，也就是使用 isAssignableFrom 进行判断。

### A

```java
public abstract class ApplicationEvent extends EventObject {

  /**
   * Constructs a prototypical Event.
   *
   * @param source the object on which the Event initially occurred
   * @throws IllegalArgumentException if source is null
   */
  public ApplicationEvent(Object source) {
    super(source);
  }
}

public class ApplicationContextEvent extends ApplicationEvent {

  /**
   * Constructs a prototypical Event.
   *
   * @param source the object on which the Event initially occurred
   * @throws IllegalArgumentException if source is null
   */
  public ApplicationContextEvent(Object source) {
    super(source);
  }

  /** Get the <code>ApplicationContext</code> that the event was raised for. */
  public final ApplicationContext getApplicationContext() {
    return (ApplicationContext) getSource();
  }
}

public class ContextClosedEvent extends ApplicationContextEvent {
  /**
   * Constructs a prototypical Event.
   *
   * @param source the object on which the Event initially occurred
   * @throws IllegalArgumentException if source is null
   */
  public ContextClosedEvent(Object source) {
    super(source);
  }
}

public class ContextRefreshedEvent extends ApplicationContextEvent {
  /**
   * Constructs a prototypical Event.
   *
   * @param source the object on which the Event initially occurred
   * @throws IllegalArgumentException if source is null
   */
  public ContextRefreshedEvent(Object source) {
    super(source);
  }
}
```

- ApplicationEvent 继承 EventObject 以后就具备了事件功能，所有的事件定义都要继承 ApplicationEvent
- ApplicationContextEvent 是应用上下文事件，是事件类的一个基类。
- ContextClosedEvent 和 ContextRefreshedEvent 是内部用来实现关闭事件和刷新事件的类



```java
public interface ApplicationEventPublisher {
  /**
   * 将应用程序事件通知注册到此应用程序的所有侦听器。 事件可以是框架事件（如RequestHandledEvent）或应用程序特定的事件。
   *
   * @param event the event to publish
   */
  void publishEvent(ApplicationEvent event);
}
```

- 事件发布者，所有的事件都需要从这个接口发布出去

```java
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
  /**
   * Handle an application event.
   *
   * @param event the event to respond to
   */
  void onApplicationEvent(E event);
}
```

- 事件监听者，负责监听感兴趣的事件。

```java
public interface ApplicationEventMulticaster {
  /**
   * Add a listener to be notified of all events.
   *
   * @param listener the listener to add
   */
  void addApplicationListener(ApplicationListener<?> listener);

  /**
   * Remove a listener from the notification list.
   *
   * @param listener the listener to remove
   */
  void removeApplicationListener(ApplicationListener<?> listener);

  /**
   * Multicast the given application event to appropriate listeners.
   *
   * @param event the event to multicast
   */
  void multicastEvent(ApplicationEvent event);
}
```

- 在事件广播器中定义了添加监听和删除监听的方法以及一个广播事件的方法 `multicastEvent` 最终推送时间消息也会经过这个接口方法来处理谁该接收事件。

#### 测试

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean class="org.springframework.beans.factory.aeal.ContextRefreshedEventListener"/>

    <bean class="org.springframework.beans.factory.aeal.AEALCustomEventListener"/>

    <bean class="org.springframework.beans.factory.aeal.AEALContextClosedEventListener"/>

</beans>
```

```java
public class AEALContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {
  @Override
  public void onApplicationEvent(ContextClosedEvent event) {
    System.out.println("关闭事件：" + this.getClass().getName());
  }
}

@Setter
@Getter
public class AEALCustomEvent extends ApplicationContextEvent {
  private Long id;
  private String message;

  public AEALCustomEvent(Object source, Long id, String message) {
    super(source);
    this.id = id;
    this.message = message;
  }
}


public class AEALCustomEventListener implements ApplicationListener<AEALCustomEvent> {
  @Override
  public void onApplicationEvent(AEALCustomEvent event) {
    System.out.println("收到：" + event.getSource() + "消息; 时间：" + new Date());
    System.out.println("消息：" + event.getId() + ":" + event.getMessage());
  }
}

public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {
  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    System.out.println("刷新事件：" + this.getClass().getName());
  }
}
```

```java
public class ApplicationEventAndListenerTest {
  @Test
  public void test_event() {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-application-event-and-listener.xml");
    applicationContext.publishEvent(
        new AEALCustomEvent(applicationContext, 1019129009086763L, "成功了！"));

    applicationContext.registerShutdownHook();
  }
}
```



### R

`isAssignableFrom` 和 `instanceof` 相似，不过 `isAssignableFrom` 是用来判断子类和父类的关系的，或者接口的实现类和接口的关系的，默认所有的类的终极父类都是 `Object`。如果 `A.isAssignableFrom(B)` 结果是 true，证明 B 可以转换成为 A, 也就是 A 可以由 B 转换而来。


## 基于 JDK、Cglib 实现 AOP

> 代码分支: [jdk-cglib-dynamic-proxy](https://github.com/DoubleW2w/sbs-small-spring/tree/jdk-cglib-dynamic-proxy)



### S

#### 什么是切面

想象一下，你现在有一个功能点是需要进行嵌入到多个地方，如果一个一个点去嵌入的话，会很费事，如果后续这个嵌入点有点变动，所有地方的代码都需要进行修改。
这个时候，如果从嵌入点的角度来看，哪些个地方符合我的监听条件，我就嵌入哪里，不符合的我干脆不搭理。“就像你切菜，长度为 10cm 的我就进行对半切”，而上面的“长度为 10cm”就是要执行“对半切”这个功能点的条件。

如果放到代码来说明，就相当于有很多个方法需要执行，但需要进行嵌入功能点的方法是有条件的。那么符合条件的方法，在执行的时候就会被拦截，然后去执行扩展操作。

专业地来说，**面向切面编程，它能够将那些与「业务逻辑」无关，却为「业务模块」所共同调用的逻辑（事务处理、日志处理、权限控制等）封装起来，便于减少系统的重复代码，降低耦合度。**

<img src="https://bugstack.cn/assets/images/spring/spring-12-01.png">

<p style="text-align: center"> <a href="https://bugstack.cn"> 图片来自小傅哥 </a> </p>



#### 切面的一些术语

JoinPoint 织入点、连接点，指的是需要执行代理操作的某个类的方法

PointCut 切入点是 JoinPoint 的表达方式，也就是被切面拦截的连接点。



### T1-切点表达式

需要匹配类，定义 `ClassFilter` 接口；匹配方法，定义 `MethodMatcher` 接口。

而一个 PonitCut 需要匹配类和方法，所以会包含 `ClassFilter`、`MethodMatcher`

```java
public class PointcutExpressionTest {
  @Test
  public void testPointcutExpression() throws Exception {
    // 简单实现，只实现了 execution 方式，表示匹配 HelloService和他子类 下面的所有方法
    AspectJExpressionPointcut pointcut =
        new AspectJExpressionPointcut(
            "execution(* org.springframework.test.service.HelloService.*(..))");
    Class<HelloService> clazz = HelloService.class;
    Method method = clazz.getDeclaredMethod("sayHello");
    assertThat(pointcut.matches(clazz)).isTrue();
    assertThat(pointcut.matches(method, clazz)).isTrue();
  }
}
```

### T2—JDK 动态代理

AopProxy 是获取代理对象的抽象接口

```java
public interface AopProxy {
  Object getProxy();
}
```

JdkDynamicAopProxy 的基于 JDK 动态代理的具体实现, 并实现 InvocationHandler, 在 invoke 方法中进行拦截处理。如果当前类和当前方法匹配成功，就执行拦截，转而执行拦截器的方法，否则就直接放行。

```java
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

  private final AdvisedSupport advised;

  public JdkDynamicAopProxy(AdvisedSupport advised) {
    this.advised = advised;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 检查方法匹配器是否与当前方法是否匹配
    if (advised
        .getMethodMatcher()
        .matches(method, advised.getTargetSource().getTarget().getClass())) {
      // 代理方法
      MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
      return methodInterceptor.invoke(
          new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(), method, args));
    }
    return method.invoke(advised.getTargetSource().getTarget(), args);
  }

  @Override
  public Object getProxy() {
    return Proxy.newProxyInstance(
        getClass().getClassLoader(), advised.getTargetSource().getTargetClass(), this);
  }
}
```

TargetSource 是被代理的目标对象的封装。

```java
public class TargetSource {
  /** 目标对象 */
  private final Object target;

  public TargetSource(Object target) {
    this.target = target;
  }

  public Class<?>[] getTargetClass() {
    return this.target.getClass().getInterfaces();
  }

  public Object getTarget() {
    return this.target;
  }
}
```

<p> </p>

**测试类**

创建出目标对象，并根据目标对象创建一个「通知增强支持类 AdvisedSupport」，设置好「TargetSource」、「MethodInterceptor」、「MethodMatcher」，从而达到拦截的效果。

```java
public class DynamicProxyTest {
  @Test
  public void test_jdkDynamic() throws Exception {
    LoveUService loveUService = new LoveUServiceImpl();

    AdvisedSupport advisedSupport = new AdvisedSupport();
    TargetSource targetSource = new TargetSource(loveUService);
    LoveUServiceInterceptor methodInterceptor = new LoveUServiceInterceptor();
    AspectJExpressionPointcut pointcut =
        new AspectJExpressionPointcut(
            "execution(* org.springframework.test.service.LoveUService.explode(..))");
    MethodMatcher methodMatcher = pointcut.getMethodMatcher();
    advisedSupport.setTargetSource(targetSource);
    advisedSupport.setMethodInterceptor(methodInterceptor);
    advisedSupport.setMethodMatcher(methodMatcher);

    LoveUService proxy = (LoveUService) new JdkDynamicAopProxy(advisedSupport).getProxy();
    proxy.explode();
  }
}
```



### T3-cglib 代理

```xml
<dependency>
  <groupId>cglib</groupId>
  <artifactId>cglib</artifactId>
  <version>3.3.0</version>
</dependency>
```



cglib 通过 Enhancer 类来完成，设置超类，设置接口，设置回调。

```java
public class CglibAopProxy implements AopProxy {

  private final AdvisedSupport advised;

  public CglibAopProxy(AdvisedSupport advised) {
    this.advised = advised;
  }

  @Override
  public Object getProxy() {
    Enhancer enhancer = new Enhancer();
    Class<?> aClass = advised.getTargetSource().getTarget().getClass();
    aClass = ClassUtils.isCglibProxyClass(aClass) ? aClass.getSuperclass() : aClass;
    enhancer.setSuperclass(aClass);
    enhancer.setInterfaces(advised.getTargetSource().getTargetClass());
    enhancer.setCallback(new DynamicAdvisedInterceptor(advised));
    return enhancer.create();
  }

  /** 注意此处的MethodInterceptor是cglib中的接口，advised中的MethodInterceptor的AOP联盟中定义的接口，因此定义此类做适配 */
  private static class DynamicAdvisedInterceptor implements MethodInterceptor {
    private final AdvisedSupport advised;

    public DynamicAdvisedInterceptor(AdvisedSupport advised) {
      this.advised = advised;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
        throws Throwable {
      // cglib方法调用
      CglibMethodInvocation methodInvocation =
          new CglibMethodInvocation(
              advised.getTargetSource().getTarget(), method, objects, methodProxy);
      // 检查目标方法是否符合通知条件
      if (advised
          .getMethodMatcher()
          .matches(method, advised.getTargetSource().getTarget().getClass())) {
        return advised.getMethodInterceptor().invoke(methodInvocation);
      }
      return methodInvocation.proceed();
    }
  }

  private static class CglibMethodInvocation extends ReflectiveMethodInvocation {
    private final MethodProxy methodProxy;

    public CglibMethodInvocation(
        Object target, Method method, Object[] arguments, MethodProxy methodProxy) {
      super(target, method, arguments);
      this.methodProxy = methodProxy;
    }

    @Override
    public Object proceed() throws Throwable {
      return this.methodProxy.invoke(this.target, this.arguments);
    }
  }
}
```

**测试类**

```java
@Test
public void test_cglibDynamic() throws Exception {
  LoveUService proxy = (LoveUService) new CglibAopProxy(advisedSupport).getProxy();
  proxy.explode();
}
```

### R

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/sbs-small-spring%E5%9B%BE%E7%BA%B8-%E5%9F%BA%E4%BA%8Ejdk%E3%80%81cglib%E4%BB%A3%E7%90%86%E5%AE%9E%E7%8E%B0aop.drawio-1.png"/>

本节完成了「切面技术」的一个具体实现，如何代理目标对象，过滤方法，拦截方法，以及使用 cglib 和 jdk 代理。

- 代理目标对象本质上是通过 Proxy 类(jdk 方式）和 Enhancer 类（cglib 方法）创建目标对象的代理对象。
- 被代理的目标对象是被封装在 TargetSource 类中。
- 拦截一个方法需要知道是否匹配到目标类、目标方法，这就用到了 `TargetSource` 和 `MethodMatcher` ，来进行检查。如果 **不符合通知条件**，那就直接放行，否则进行拦截，执行增强代码逻辑——`MethodInterceptor`。



## AOP 代理工厂将 AOP 扩展到 Bean 的生命周期

> 代码分支: [aop-into-bean-lifecycle](https://github.com/DoubleW2w/sbs-small-spring/tree/aop-into-bean-lifecycle)

### S

在 [上一章节](#基于 JDK、Cglib 实现 AOP) 中，已经实现了切点表达式、以及通过 JDK 动态代理和 Cglib 代理的方式实现简单的 AOP 功能。

其中，代理对象的创建是通过 `Proxy.newProxyInstance()` 或者 `Enhancer enhancer` 来完成，以及通过匹配方法来拦截方法执行扩展操作。但是这个步骤却没有融合进 Spring 的生命周期中。

而且我们通过测试类来看，也很复杂。

### T1-AOP 代理工厂

之前我们创建代理工厂是直接通过 `new` 的方式进行实现的，现在我们实现一个 aop 代理工厂来实现，目的是简化代理的创建方式。

```java
LoveUService proxy = (LoveUService) new JdkDynamicAopProxy(advisedSupport).getProxy();
proxy.explode();
// 或者
LoveUService proxy = (LoveUService) new CglibAopProxy(advisedSupport).getProxy();
proxy.explode();
```

定义代理工厂

```java
public class ProxyFactory {
  private AdvisedSupport advisedSupport;

  public ProxyFactory(AdvisedSupport advisedSupport) {
    this.advisedSupport = advisedSupport;
  }

  public Object getProxy() {
    return createAopProxy().getProxy();
  }

  /**
   * 默认情况下是JDK动态代理
   *
   * @return
   */
  private AopProxy createAopProxy() {
    if (advisedSupport.isProxyTargetClass()) {
      return new CglibAopProxy(advisedSupport);
    }
    return new JdkDynamicAopProxy(advisedSupport);
  }
}
```

本质上还是不变，不过需要增加 **代理方式的选择**，即通过 `proxyTargetClass` 字段来判断要进行 jdk 代理还是 cglib 代理。测试类的修改就统一通过 `ProxyFactory` 来实现。

```java
@Test
public void test_proxyFactory() throws Exception {
  // 使用JDK动态代理
  advisedSupport.setProxyTargetClass(false);
  LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
  proxy.explode();
  System.out.println("---------------------------------------------------------------");
  // 使用CGLIB动态代理
  advisedSupport.setProxyTargetClass(true);
  proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
  proxy.explode();
}
```

### T2-各种通知(advice)实现

Spring 将 AOP 联盟中的 Advice 细化出各种类型的 Advice，常用的有 `BeforeAdvice`/`AfterAdvice`/`AfterReturningAdvice`/`ThrowsAdvice`，我们可以通过扩展 MethodInterceptor 来实现。为了统一管理 MethodInterceptor，我们定义出一个 GenericInterceptor，并将各种类型的通知放置进去，类似实现一个模板方法。

```java
public class GenericInterceptor implements MethodInterceptor {
  /** 前置通知 */
  private BeforeAdvice beforeAdvice;

  /** 后置通知 */
  private AfterAdvice afterAdvice;

  /** 返回后置通知 */
  private AfterReturningAdvice afterReturningAdvice;

  /** 抛出通知 */
  private ThrowsAdvice throwsAdvice;
  
    @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Object result = null;
    try {
      // 前置通知
      if (beforeAdvice != null) {
        beforeAdvice.before(
            invocation.getMethod(), invocation.getArguments(), invocation.getThis());
      }
      result = invocation.proceed();
    } catch (Exception throwable) {
      // 异常通知
      if (throwsAdvice != null) {
        throwsAdvice.throwsHandle(
            throwable, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
      }
    } finally {
      // 后置通知
      if (afterAdvice != null) {
        afterAdvice.after(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
      }
    }
    // 返回通知
    if (afterReturningAdvice != null) {
      afterReturningAdvice.afterReturning(
          result, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
    }
    return result;
  }
  // 省略setter
}
```

测试类

```java
@Test
public void test_allAdvice() throws Exception {
  LoveUService loveUService = new LoveUServiceImpl();

  advisedSupport = new AdvisedSupport();
  TargetSource targetSource = new TargetSource(loveUService);
  AspectJExpressionPointcut pointcut =
    new AspectJExpressionPointcut(
    "execution(* org.springframework.test.service.LoveUService.explode(..))");
  MethodMatcher methodMatcher = pointcut.getMethodMatcher();
  advisedSupport.setTargetSource(targetSource);
  advisedSupport.setMethodMatcher(methodMatcher);

  GenericInterceptor methodInterceptor = new GenericInterceptor();
  methodInterceptor.setBeforeAdvice(new LoveUServiceBeforeAdvice());
  methodInterceptor.setAfterReturningAdvice(new LoveUServiceAfterReturningAdvice());
  methodInterceptor.setThrowsAdvice(new LoveUServiceThrowsAdvice());
  methodInterceptor.setAfterAdvice(new LoveUServiceAfterAdvice());
  advisedSupport.setMethodInterceptor(methodInterceptor);

  LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
  proxy.explode();
}
```

```java
BeforeAdvice: do something before the earth explodes
I Am Missing
AfterAdvice: do something after the earth explodes
AfterReturningAdvice: do something after the earth explodes return
```

如果想测试抛出异常的增强通知，我们需要刻意抛出异常

```java
public class LoveUServiceWithExceptionImpl implements LoveUService {
  @Override
  public void explode() {
    System.out.println("I Am Missing");
    throw new RuntimeException();
  }
}
```



```java
@Test
public void test_allAdviceWithException() throws Exception {
  LoveUService loveUService = new LoveUServiceWithExceptionImpl();

  advisedSupport = new AdvisedSupport();
  TargetSource targetSource = new TargetSource(loveUService);
  AspectJExpressionPointcut pointcut =
    new AspectJExpressionPointcut(
    "execution(* org.springframework.test.service.LoveUService.explode(..))");
  MethodMatcher methodMatcher = pointcut.getMethodMatcher();
  advisedSupport.setTargetSource(targetSource);
  advisedSupport.setMethodMatcher(methodMatcher);
  
  // 拦截器并设置好各种通知
  GenericInterceptor methodInterceptor = new GenericInterceptor();
  methodInterceptor.setBeforeAdvice(new LoveUServiceBeforeAdvice());
  methodInterceptor.setAfterReturningAdvice(new LoveUServiceAfterReturningAdvice());
  methodInterceptor.setThrowsAdvice(new LoveUServiceThrowsAdvice());
  methodInterceptor.setAfterAdvice(new LoveUServiceAfterAdvice());
  advisedSupport.setMethodInterceptor(methodInterceptor);

  advisedSupport.setTargetSource(new TargetSource(loveUService));
  LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
  proxy.explode();
}
```



```java
BeforeAdvice: do something before the earth explodes
I Am Missing
ThrowsAdvice: do something when the earth explodes function throw an exception
AfterAdvice: do something after the earth explodes
AfterReturningAdvice: do something after the earth explodes return
```



### T3-Pointcut 与 Advice 的结合

让我们回顾一下 Pointcut 与 JoinPoint 的概念。

**JoinPoint 可以理解为一个“可插入的点”。它指的是在代码中，可能被 AOP 拦截的具体位置。**

- 方法调用 `MethodInvocation`
- 构造方法调用 `ConstructorInvocation`
- 异常抛出

**Pointcut 是一个表达式或者规则，用于选择多个 JoinPoint。比如你定义一个 Pointcut 匹配所有 public 方法调用。**



定义出 `Advisor` 类和 `PointcutAdvisor` 类，其实 `PointcutAdvisor` 类就是将 Pointcut 和 Advice 结合起来。

```java
public interface Advisor {
  Advice getAdvice();
}

 public interface PointcutAdvisor extends Advisor {
  Pointcut getPointcut();
}
```



```java
@Test
public void test_advisor() throws Exception {
  LoveUService loveUService = new LoveUServiceImpl();
  // Advisor是Pointcut和Advice的组合
  AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
  advisor.setExpression("execution(* org.springframework.test.service.LoveUService.explode(..))");
  GenericInterceptor methodInterceptor = new GenericInterceptor();
  methodInterceptor.setBeforeAdvice(new LoveUServiceBeforeAdvice());
  advisor.setAdvice(methodInterceptor);

  AdvisedSupport advisedSupport = new AdvisedSupport();
  TargetSource targetSource = new TargetSource(loveUService);
  advisedSupport.setTargetSource(targetSource);
  advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
  advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
  //			advisedSupport.setProxyTargetClass(true);   //JDK or CGLIB

  LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
  proxy.explode();
}
```

- `PointcutAdvisor` 负责将 `Pointcut` 和 `Advice` 进行关联。
- 在满足条件时应用 `Advice`，实现动态、条件性的 AOP 功能。

### T4-将 AOP 嵌入 bean 的生命管理周期

本节目标是通过 `BeanPostProcessor` 把动态代理融入到 Bean 的生命周期中，以及如何组装各项切点、拦截、前置的功能和适配对应的代理器。

```java
public class DefaultAdvisorAutoProxyCreator
    implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

  private DefaultListableBeanFactory beanFactory;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = (DefaultListableBeanFactory) beanFactory;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName)
      throws BeansException {
    if (isInfrastructureClass(beanClass)) return null;

    Collection<AspectJExpressionPointcutAdvisor> advisors =
        beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

    for (AspectJExpressionPointcutAdvisor advisor : advisors) {
      ClassFilter classFilter = advisor.getPointcut().getClassFilter();
      if (!classFilter.matches(beanClass)) continue;

      AdvisedSupport advisedSupport = new AdvisedSupport();

      TargetSource targetSource = null;
      try {
        targetSource = new TargetSource(beanClass.getDeclaredConstructor().newInstance());
      } catch (Exception e) {
        e.printStackTrace();
      }
      advisedSupport.setTargetSource(targetSource);
      advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
      advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
      advisedSupport.setProxyTargetClass(false);

      return new ProxyFactory(advisedSupport).getProxy();
    }

    return null;
  }

  /**
   * 「类对象表示的」类或者接口与「beanClass所表示的」类或者接口是否相同，或者是「beanClass所表示的」类或者接口的父类
   *
   * <p>即 是否是 Advice、Pointcut、Advisor 类
   *
   * @param beanClass bean对象所表示的类
   * @return
   */
  private boolean isInfrastructureClass(Class<?> beanClass) {
    return Advice.class.isAssignableFrom(beanClass)
        || Pointcut.class.isAssignableFrom(beanClass)
        || Advisor.class.isAssignableFrom(beanClass);
  }
}
```

核心方法在于 `postProcessBeforeInstantiation()` 中，通过 beanFactory.getBeansOfType 获取 `AspectJExpressionPointcutAdvisor` 开始, 针对每一个这样的 bean 类型进行一次代理，而代理的操作跟 [T3-Pointcut 与 Advice 的结合](#T3-Pointcut与Advice的结合) 是一样的。

当获取到对应的 advisor 后，填充对应的属性信息，包括：目标对象、拦截方法、匹配器，之后返回代理对象即可。

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

  private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

  @Override
  protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
    Object bean = null;
    try {
      // 判断是否返回代理 Bean 对象
      bean = resolveBeforeInstantiation(beanName, beanDefinition);
      if (null != bean) {
        return bean;
      }
      // 省略...
    } catch (Exception e) {
      throw new BeansException("Instantiation of bean failed.", e);
    }
    // 省略...
    return bean;
  }

  protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) {
    Object bean = applyBeanPostProcessorBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
    if (null != bean) {
      bean = applyBeanPostProcessorAfterInitialization(bean, beanName);
    }
    return bean;
  }

  // 注意，此方法为新增方法，与 “applyBeanPostProcessorBeforeInitialization” 是两个方法
  public Object applyBeanPostProcessorBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
      if (processor instanceof InstantiationAwareBeanPostProcessor) {
        Object result = ((InstantiationAwareBeanPostProcessor)processor).postProcessBeforeInstantiation(beanClass, beanName);
        if (null != result) return result;
      }
    }
    return null;
  }
}
```

判断是否返回代理 bean 对象为前置操作，如果当前的 `BeanPostProcessor` 是 `InstantiationAwareBeanPostProcessor` 就执行 postProcessBeforeInstantiation 进行代理，最后返回代理对象

### A

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
              http://www.springframework.org/schema/beans/spring-beans.xsd">
    <!-- 注入bean对象 -->
    <bean id="loveUService" class="org.springframework.test.service.LoveUServiceImpl"/>
    <!-- bean对象：自动扫描advisor，并注入 -->
    <bean class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"/>

    <bean id="pointcutAdvisor"
          class="org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor">
        <property name="expression"
                  value="execution(* org.springframework.test.service.LoveUService.explode(..))"/>
        <property name="advice" ref="methodInterceptor"/>
    </bean>


    <bean id="methodInterceptor" class="org.springframework.aop.GenericInterceptor">
        <property name="beforeAdvice" ref="beforeAdvice"/>
    </bean>
    <bean id="beforeAdvice" class="org.springframework.test.common.LoveUServiceBeforeAdvice"/>
</beans>
```

```java
public interface LoveUService {
  void explode();

  String explodeReturn();
}

public class LoveUServiceImpl implements LoveUService {
  @Override
  public void explode() {
    System.out.println("I Am Missing");
  }

  @Override
  public String explodeReturn() {
    return "I Love U";
  }
}

public class LoveUServiceBeforeAdvice implements BeforeAdvice {
  @Override
  public void before(Method method, Object[] args, Object target) throws Throwable {
    System.out.println("BeforeAdvice: do something before the earth explodes");
  }
}

```



```java
public class AopIntoBeanLifecycleTest {
  @Test
  public void testAutoProxy() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-aop-into-bean-lifecycle.xml");

    // 获取代理对象
    LoveUService loveUService = applicationContext.getBean("loveUService", LoveUService.class);
    loveUService.explode();
  }
}
```

### R

本章节的内容拆分了好几个小节

1. 复习上一节的代理操作，以及 JDK 动态代理和 cglib 代理两种方式
2. 通过实现 AOP 代理工厂，来简化创建代理对象的操作
3. 细化各种通知（advice）来增强 joinpoint（织入点）的逻辑，比如 beforeAdvice, afterAdvice, afterReturningAdvice 等
4. 将 pointcut 和 advice 结合起来，创建出 advisor，这是一个将 pointcut 和 advice 关联起来的类。使用起来只需要放入对应的 advice 和切点表达式便可形成一个 advisor，即完成一个增强点。
5. 定义 DefaultAdvisorAutoProxyCreator 类并实现 InstantiationAwareBeanPostProcessor，目的是让 DefaultAdvisorAutoProxyCreator 类具有在应用上下文中扫描 advisor，并根据切点表达式创建出对应的代理对象。在 AbstractAutowireCapableBeanFactory#createBean() 中，先行判断 **是否返回代理对象**，让 aop 自动代理操作融入了 bean 对象的生命管理周期中。

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202411092248784.png"/>

<p style="text-align:center"> <a href="https://github.com/DerekYRC/mini-spring/blob/main/changelog.md#"> 图片来自：mini-spring </a> </p>

## 属性占位符+自动扫描 Bean 对象注册

> 代码分支: [auto-scan-bean-object-register](https://github.com/DoubleW2w/sbs-small-spring/tree/auto-scan-bean-object-register)

### 属性占位符

我们知道 BeanFactoryPostProcessor 的作用是 **在所有的 BeanDefinition 加载完成之后，实例化 Bean 对象之前，可以提供修改 BeanDefinition 属性的机制**，其核心是在 `postProcessBeanFactory()` 方法中。

因此我们可以结合 BeanFactoryPostProcessor 实现 `${token}` 给 Bean 对象注入进去属性信息。

```java
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

  public static final String PLACEHOLDER_PREFIX = "${";

  public static final String PLACEHOLDER_SUFFIX = "}";

  /** 配置文件路径 */
  private String location;

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    // 加载属性配置文件
    Properties properties = loadProperties();

    // 属性值替换占位符
    processProperties(beanFactory, properties);
  }

  /**
   * 加载属性配置文件
   *
   * @return
   */
  private Properties loadProperties() {
    try {
      DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
      Resource resource = resourceLoader.getResource(location);
      Properties properties = new Properties();
      properties.load(resource.getInputStream());
      return properties;
    } catch (Exception e) {
      throw new BeansException("Could not load properties", e);
    }
  }

  /**
   * 属性值替换占位符
   *
   * @param beanFactory bean工厂
   * @param properties 属性
   * @throws BeansException bean异常
   */
  private void processProperties(ConfigurableListableBeanFactory beanFactory, Properties properties)
      throws BeansException {
    String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
    for (String beanName : beanDefinitionNames) {
      BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
      resolvePropertyValues(beanDefinition, properties);
    }
  }

  private void resolvePropertyValues(BeanDefinition beanDefinition, Properties properties) {
    PropertyValues propertyValues = beanDefinition.getPropertyValues();
    for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
      // 属性占位符的值${xxx}
      Object value = propertyValue.getValue();
      if (!(value instanceof String)) continue;
      // TODO 仅简单支持一个占位符的格式
      String strVal = (String) value;
      StringBuffer buf = new StringBuffer(strVal);
      int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
      int endIndex = strVal.indexOf(PLACEHOLDER_SUFFIX);
      if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
        String propKey = strVal.substring(startIndex + 2, endIndex); // 属性名称
        String propVal = properties.getProperty(propKey); // 从配置属性获取属性值
        buf.replace(startIndex, endIndex + 1, propVal);
        propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), buf.toString()));
      }
    }
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
```

测试类

```java
public class AutoScanBeanObjectRegisterTest {
  @Test
  public void test_propertyPlaceholderConfigurer() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-auto-scan-object-register-1.xml");

    Car car = applicationContext.getBean("car", Car.class);
    assertThat(car.getBrand()).isEqualTo("bmw");
  }
}

@Data
@ToString
public class Car {
  private String brand;
  private String name;
}
```

```properties
brand=bmw
name=yoah
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	         http://www.springframework.org/schema/beans/spring-beans.xsd
		 http://www.springframework.org/schema/context
		 http://www.springframework.org/schema/context/spring-context-4.0.xsd">

    <bean class="org.springframework.beans.factory.PropertyPlaceholderConfigurer">
        <property name="location" value="classpath:car.properties" />
    </bean>

    <bean id="car" class="org.springframework.test.bean.Car">
        <property name="brand" value="${brand}" />
        <property name="name" value="${name}"/>
    </bean>
</beans>
```

核心就是 **在 bean 实例化之前，编辑 BeanDefinition，解析 XML 文件中的占位符，然后用 properties 文件中的配置值替换占位符。**

### 包扫描自动注册

在 XmlBeanDefinitionReader 中解析 `<context:component-scan />` 标签，扫描类组装 BeanDefinition 然后注册到容器中的操作在 `ClassPathBeanDefinitionScanner#doScan` 中实现。

```java
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
  protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException, DocumentException {
    SAXReader reader = new SAXReader();
    Document document = reader.read(inputStream);
    Element root = document.getRootElement();

    // 解析 context:component-scan 标签，扫描包中的类并提取相关信息，用于组装 BeanDefinition
    Element componentScan = root.element("component-scan");
    if (null != componentScan) {
      String scanPath = componentScan.attributeValue("base-package");
      if (StrUtil.isEmpty(scanPath)) {
        throw new BeansException("The value of base-package attribute can not be empty or null");
      }
      // 扫描包路径
      scanPackage(scanPath);
    }

    // 省略....
  }

  /**
   * 扫描注解Component的类，提取信息，组装成BeanDefinition
   *
   * @param scanPath 扫描路径
   */
  private void scanPackage(String scanPath) {
    String[] basePackages = StrUtil.splitToArray(scanPath, ',');
    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getRegistry());
    scanner.doScan(basePackages);
  }
}
```

<p> </p>

```java
public class ClassPathScanningCandidateComponentProvider {
  public Set<BeanDefinition> findCandidateComponents(String basePackage) {
    Set<BeanDefinition> candidates = new LinkedHashSet<>();
    // 扫描有org.springframework.stereotype.Component注解的类
    Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);
    for (Class<?> clazz : classes) {
      BeanDefinition beanDefinition = new BeanDefinition(clazz);
      candidates.add(beanDefinition);
    }
    return candidates;
  }
}
```

`ClassPathScanningCandidateComponentProvider` 类具有 **扫描类型并筛选出符合条件的类** 的职责



```java
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

  private BeanDefinitionRegistry registry;

  public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
    this.registry = registry;
  }

  /**
   * 扫描类路径的某个包
   *
   * @param basePackages 包路径
   */
  public void doScan(String... basePackages) {
    for (String basePackage : basePackages) {
      // 寻找Component注解的类的bean定义
      Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
      for (BeanDefinition candidate : candidates) {
        // 解析bean的作用域
        String beanScope = resolveBeanScope(candidate);
        if (StrUtil.isNotEmpty(beanScope)) {
          candidate.setScope(beanScope);
        }
        // 生成bean的名称
        String beanName = determineBeanName(candidate);
        // 注册BeanDefinition
        registry.registerBeanDefinition(beanName, candidate);
      }
    }
  }
}
```

- `ClassPathBeanDefinitionScanner` 类通过 `ClassPathScanningCandidateComponentProvider` 获取到所有符合条件的候选类
- 解析候选类有没有 `@Scope` 注解，并设置 bean 的作用域
- 解析 bean 名称是来自注解还是默认生成
- 调用 `BeanDefinitionRegistry` 的注册 bena 定义的能力

测试类

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	         http://www.springframework.org/schema/beans/spring-beans.xsd
		 http://www.springframework.org/schema/context
		 http://www.springframework.org/schema/context/spring-context-4.0.xsd">

    <context:component-scan base-package="org.springframework.test.bean"/>

</beans>
```

```java
@Component
@Data
@ToString
public class Car {
  private String brand;
  private String name;
}
```

```java
  @Test
  public void test_packageScan() throws Exception {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-auto-scan-object-register-2.xml");
    Car car = applicationContext.getBean("car", Car.class);
    assertThat(car).isNotNull();
  }
```

### 总结

学习到这里，我们要明白一点：其实 Spring 的扩展内容，是基于 Bean 的生命周期来进行嵌入。

## 通过注解注入属性信息

> 代码分支: [annotation-inject-properties](https://github.com/DoubleW2w/sbs-small-spring/tree/annotation-inject-properties)

### S

通过 [属性占位符+自动扫描 Bean 对象注册](#属性占位符+自动扫描 Bean 对象注册) 我们完成了自动扫描包，注册 Bean 对象，并且通过配置 `${name}` 这样的方式完成属性注入。

### T

本节目标就是实现 `@Autowired`、`@Value` 注解，完成对属性和对象的注入操作。

- 修改 Bean 的定义要用到 `BeanFactoryPostProcessor`
- 处理 Bean 的属性要用到 `BeanPostProcessor`
- 要处理自动扫描注入，包括属性注入、对象注入，则需要在对象属性 `applyPropertyValues` 填充之前 ，把属性信息写入到 PropertyValues 的集合中去。

### A1-value 注解

value 注解定义, 该注解可以放在字段，方法，参数上，但在解析的时候，我们只针对了字段上。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Value {
  String value();
}
```

假设我们定义了 bean 类如下

```java
@Component
@Data
@ToString
public class Car {
  @Value(value = "${brand}")
  private String brand;

  @Value(value = "${name}")
  private String name;
}
```

```properties
brand=bmw
name=yoah
```

为了达到这个注解能够注入属性信息，可以分为最简单的几步：1.加载 properties 文件、2.在实例化 Bean 的时候，遍历字段并解析 `@Value` 注解、3.获取到里面的属性信息，并最终设置字段的值。

<span style="text-emphasis:filled red;"> 加载 Properties 文件 </span> 的职责交给了 `PropertyPlaceholderConfigurer` 类，由于该类实现了 `BeanFactoryPostProcessor`，因此 **在所有 beanDefinition 加载完成之后，在 bean 实例化之前，就会执行加载属性配置文件、属性值替换占位符以及往容器里添加字符串解析器**

```java
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {
 
@Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    // 加载属性配置文件
    Properties properties = loadProperties();

    // 属性值替换占位符
    processProperties(beanFactory, properties);

    // 往容器中添加字符解析器，供解析@Value注解使用
    StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(properties);
    beanFactory.addEmbeddedValueResolver(valueResolver);
  }
}
```

而遍历字段并解析 `@Value` 注解的职责，获取到里面的属性并完成设置的操作的职责是交给了 `AutowiredAnnotationBeanPostProcessor`，在 `postProcessPropertyValues()` 负责处理注解。此步骤出发是在 Bean 实例化之后，但在设置属性之前执行。

```java
public class AutowiredAnnotationBeanPostProcessor
    implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

  private ConfigurableListableBeanFactory beanFactory;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
  }

  @Override
  public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName)
      throws BeansException {
    // 处理@Value注解
    Class<?> clazz = bean.getClass();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      Value valueAnnotation = field.getAnnotation(Value.class);
      if (valueAnnotation != null) {
        String value = valueAnnotation.value();
        value = beanFactory.resolveEmbeddedValue(value);
        BeanUtil.setFieldValue(bean, field.getName(), value);
      }
    }
    // todo：处理@autowired注解
    return pvs;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName)
      throws BeansException {
    return null;
  }
}
```

而上面的步骤，要放在 bean 的生命管理周期中

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
    implements AutowireCapableBeanFactory {  
@Override
  protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args)
      throws BeansException {
    Object bean = null;
    try {
      // 判断是否返回代理 Bean 对象
      bean = resolveBeforeInstantiation(beanName, beanDefinition);
      if (null != bean) {
        return bean;
      }
      bean = createBeanInstance(beanDefinition, beanName, args);
      
      // 在设置bean属性之前，允许BeanPostProcessor修改属性值
      applyBeanPostprocessorsBeforeApplyingPropertyValues(beanName, bean, beanDefinition);
      // 在设置bean属性之前，允许BeanPostProcessor修改属性值
      
      // 给 Bean 填充属性
      applyPropertyValues(beanName, bean, beanDefinition);
      // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
      bean = initializeBean(beanName, bean, beanDefinition);
    } catch (Exception e) {
      throw new BeansException("Instantiation of bean failed", e);
    }

    // 注册实现了 DisposableBean 接口的 Bean 对象
    registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);
    // 判断 SCOPE_SINGLETON、SCOPE_PROTOTYPE
    if (beanDefinition.isSingleton()) {
      addSingleton(beanName, bean);
    }
    return bean;
  }
}
```

测试类

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	         http://www.springframework.org/schema/beans/spring-beans.xsd
		 http://www.springframework.org/schema/context
		 http://www.springframework.org/schema/context/spring-context-4.0.xsd">

    <bean class="org.springframework.beans.factory.PropertyPlaceholderConfigurer">
        <property name="location" value="classpath:car.properties" />
    </bean>

    <context:component-scan base-package="org.springframework.test.bean"/>
</beans>
```

```java
@Component
@Data
@ToString
public class Car {
  @Value(value = "${brand}")
  private String brand;

  @Value(value = "${name}")
  private String name;
}
```

```java
public class AnnotationInjectPropertiesTest {
  @Test
  public void test_valueAnnotation() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-annotation-inject-properties-1.xml");

    Car car = applicationContext.getBean("car", Car.class);
    assertThat(car).isNotNull();
    assertThat(car.getBrand()).isEqualTo("bmw");
  }
}
```



### A2-Autowired 注解

在 `AutowiredAnnotationBeanPostProcessor` 类补充对 autowired 注解的解析。

```java
public class AutowiredAnnotationBeanPostProcessor
    implements InstantiationAwareBeanPostProcessor, BeanFactoryAware { 
@Override
  public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName)
      throws BeansException {
    // 处理@Value注解
    Class<?> clazz = bean.getClass();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      Value valueAnnotation = field.getAnnotation(Value.class);
      if (valueAnnotation != null) {
        String value = valueAnnotation.value();
        value = beanFactory.resolveEmbeddedValue(value);
        BeanUtil.setFieldValue(bean, field.getName(), value);
      }
    }
    // 处理@Autowired注解
    for (Field field : fields) {
      Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
      if (autowiredAnnotation != null) {
        Class<?> fieldType = field.getType();
        String dependentBeanName = null;
        Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
        Object dependentBean = null;
        if (qualifierAnnotation != null) {
          dependentBeanName = qualifierAnnotation.value();
          dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
        } else {
          dependentBean = beanFactory.getBean(fieldType);
        }
        BeanUtil.setFieldValue(bean, field.getName(), dependentBean);
      }
    }
    return pvs;
  }
}
```

测试类

```java
  @Test
  public void test_autowiredAnnotation() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-annotation-inject-properties-2.xml");

    Person person = applicationContext.getBean(Person.class);
    assertThat(person.getCar()).isNotNull();
    assertThat(person.getCar().getBrand()).isEqualTo("bmw");
  }
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	         http://www.springframework.org/schema/beans/spring-beans.xsd
		 http://www.springframework.org/schema/context
		 http://www.springframework.org/schema/context/spring-context-4.0.xsd">

    <bean class="org.springframework.beans.factory.PropertyPlaceholderConfigurer">
        <property name="location" value="classpath:car.properties" />
    </bean>

    <context:component-scan base-package="org.springframework.test.bean"/>
</beans>
```

```java
@Component
@Data
@ToString
public class Person {
  private String name;

  @Autowired private Car car;
}
```



### R

我们只需要把我们的实现融入到一个已经细分好的 Bean 生命周期中。你会发现它的设计是如此的好，可以让你在任何初始化的时间点上，任何面上，都能做你需要的扩展。

## 给代理对象设置属性

> 代码分支: [properties-setter-proxy-object](https://github.com/DoubleW2w/sbs-small-spring/tree/properties-setter-proxy-object)

本章节要实现的是 **给代理对象中的属性填充相对应的值**。

之前的情况是在 `DefaultAdvisorAutoProxyCreator#postProcessBeforeInstantiation()` 完成代理对象的创建。

根据下面的流程来看，在 `resolveBeforeInstantiation()` 方法中会去调用 `applyBeanPostProcessorsBeforeInstantiation()`，最终会走 `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation()`，而该方法如果返回非 null，会导致 "短路"，不会执行后面的设置属性逻辑。因此如果该方法中返回代理 bean 后，不会为代理 bean 设置属性。

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

  @Override
  protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
    Object bean = null;
    try {
      // 判断是否返回代理 Bean 对象
      bean = resolveBeforeInstantiation(beanName, beanDefinition);
      if (null != bean) {
        return bean;
      }
      // 实例化 Bean
      bean = createBeanInstance(beanDefinition, beanName, args);
      // 在设置 Bean 属性之前，允许 BeanPostProcessor 修改属性值
      applyBeanPostProcessorsBeforeApplyingPropertyValues(beanName, bean, beanDefinition);
      // 给 Bean 填充属性
      applyPropertyValues(beanName, bean, beanDefinition);
      // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
      bean = initializeBean(beanName, bean, beanDefinition);
    } catch (Exception e) {
      throw new BeansException("Instantiation of bean failed", e);
    }

    // 注册实现了 DisposableBean 接口的 Bean 对象
    registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

    // 判断 SCOPE_SINGLETON、SCOPE_PROTOTYPE
    if (beanDefinition.isSingleton()) {
      addSingleton(beanName, bean);
    }
    return bean;
  }

  protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) {
    Object bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
    if (null != bean) {
      bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
    }
    return bean;
  }

  protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
    for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
      if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
        Object result = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessBeforeInstantiation(beanClass, beanName);
        if (null != result) return result;
      }
    }
    return null;
  }
}
```

所以 **需要把创建代理对象的逻辑迁移到 Bean 对象执行初始化方法之后，在执行代理对象的创建。**

也就是把 `DefaultAdvisorAutoProxyCreator#postProcessBeforeInstantiation` 的内容迁移到 `DefaultAdvisorAutoProxyCreator#postProcessAfterInitialization` 中。

```java
public class TargetSource {

    private final Object target;

    /**
     * Return the type of targets returned by this {@link TargetSource}.
     * <p>Can return <code>null</code>, although certain usages of a
     * <code>TargetSource</code> might just work with a predetermined
     * target class.
     *
     * @return the type of targets returned by this {@link TargetSource}
     */
    public Class<?>[] getTargetClass() {
        Class<?> clazz = this.target.getClass();
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
        return clazz.getInterfaces();
    }

}
```

```java
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

  private DefaultListableBeanFactory beanFactory;

  @Override
  public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
    return null;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

    if (isInfrastructureClass(bean.getClass())) return bean;

    Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

    for (AspectJExpressionPointcutAdvisor advisor : advisors) {
      ClassFilter classFilter = advisor.getPointcut().getClassFilter();
      // 过滤匹配类
      if (!classFilter.matches(bean.getClass())) continue;

      AdvisedSupport advisedSupport = new AdvisedSupport();

      TargetSource targetSource = new TargetSource(bean);
      advisedSupport.setTargetSource(targetSource);
      advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
      advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
      advisedSupport.setProxyTargetClass(false);

      // 返回代理对象
      return new ProxyFactory(advisedSupport).getProxy();
    }

    return bean;
  }  

}
```

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

  private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

  @Override
  protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
    Object bean = null;
    try {
      // ...

      // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
      bean = initializeBean(beanName, bean, beanDefinition);
    } catch (Exception e) {
      throw new BeansException("Instantiation of bean failed", e);
    }
    // ...
    return bean;
  }

  private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {

    // ...

    wrappedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
    return wrappedBean;
  }

  @Override
  public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
    Object result = existingBean;
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
      Object current = processor.postProcessAfterInitialization(result, beanName);
      if (null == current) return result;
      result = current;
    }
    return result;
  }

}

```

测试

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	         http://www.springframework.org/schema/beans/spring-beans.xsd
		 http://www.springframework.org/schema/context
		 http://www.springframework.org/schema/context/spring-context-4.0.xsd">

    <bean id="loveUService" class="org.springframework.test.service.LoveUServiceImpl">
        <property name="name" value="earth"/>
    </bean>

    <bean class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"/>
    <bean id="beforeAdvice" class="org.springframework.test.common.LoveUServiceBeforeAdvice"/>

    <bean id="methodInterceptor" class="org.springframework.aop.GenericInterceptor">
        <property name="beforeAdvice" ref="beforeAdvice"/>
    </bean>
    <bean id="pointcutAdvisor"
          class="org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor">
        <property name="expression"
                  value="execution(* org.springframework.test.service.LoveUService.*(..))"/>
        <property name="advice" ref="methodInterceptor"/>
    </bean>
</beans>
```

```java
public class PropertiesSetterProxyObjectTest {
  @Test
  public void testPopulateProxyBeanWithPropertyValues() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext(
            "classpath:properties-setter-proxy-object-1.xml");

    // 获取代理对象
    LoveUService loveUService = (LoveUService) applicationContext.getBean("loveUService", LoveUService.class);
    loveUService.explode();
    assertThat(loveUService.getName()).isEqualTo("earth");
  }
}
```

```java
public interface LoveUService {
  void explode();

  String explodeReturn();

  String getName();
}

public class LoveUServiceImpl implements LoveUService {

  private String name;
  @Override
  public void explode() {
    System.out.println("I Am Missing");
  }

  @Override
  public String explodeReturn() {
    return "I Love U";
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
```

<img src="https://doublew2w-note-resource.oss-cn-hangzhou.aliyuncs.com/img/202411110053079.png"/>

<p style="text-align:center"> <a href="https://github.com/DerekYRC/mini-spring/blob/main/changelog.md#"> 图片来自：mini-spring </a> </p>



## 类型转换 1

> 代码分支: [type-converter-first](https://github.com/DoubleW2w/sbs-small-spring/tree/type-converter-first)

在 Spring 中为了简化和增强不同数据类型的处理，引入了 **类型转换机制**。比如我们在 Web 应用中请求参数的转换，比如 `String` 转换为 `Date`、`Enum` 类型，还比如我们在服务层中，业务逻辑中的数据类型处理。

### Converter 接口

`Converter` 接口定义了一个单一方法 `convert`，用于将源类型 `S` 转换为目标类型 `T`。

```java
public interface Converter<S, T> {
  /** 类型转换 */
  T convert(S source);
}
```

### ConversionService 接口

`ConversionService` 接口是 Spring 类型转换的核心服务接口，用于注册和管理多种 `Converter`，并执行类型转换。

```java
public interface ConversionService {
  boolean canConvert(Class<?> sourceType, Class<?> targetType);

  <T> T convert(Object source, Class<T> targetType);
}
```

Spring 还提供了默认实现 `DefaultConversionService` 和 `GenericConversionService` 支持大量内置的转换器并允许添加自定义转换器。

```java
public class DefaultConversionService extends GenericConversionService {
  public DefaultConversionService() {
    addDefaultConverters(this);
  }

  public static void addDefaultConverters(ConverterRegistry converterRegistry) {
    converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
    // TODO 添加其他ConverterFactory
  }
}
```

```java
public class GenericConversionService implements ConversionService, ConverterRegistry {

  private Map<ConvertiblePair, GenericConverter> converters = new HashMap<>();

  @Override
  public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
    GenericConverter converter = getConverter(sourceType, targetType);
    return converter != null;
  }

  @Override
  public <T> T convert(Object source, Class<T> targetType) {
    Class<?> sourceType = source.getClass();
    GenericConverter converter = getConverter(sourceType, targetType);
    return (T) converter.convert(source, sourceType, targetType);
  }

  @Override
  public void addConverter(Converter<?, ?> converter) {
    ConvertiblePair typeInfo = getRequiredTypeInfo(converter);
    ConverterAdapter converterAdapter = new ConverterAdapter(typeInfo, converter);
    for (ConvertiblePair convertibleType : converterAdapter.getConvertibleTypes()) {
      converters.put(convertibleType, converterAdapter);
    }
  }

  @Override
  public void addConverterFactory(ConverterFactory<?, ?> converterFactory) {
    ConvertiblePair typeInfo = getRequiredTypeInfo(converterFactory);
    ConverterFactoryAdapter converterFactoryAdapter =
        new ConverterFactoryAdapter(typeInfo, converterFactory);
    for (ConvertiblePair convertibleType : converterFactoryAdapter.getConvertibleTypes()) {
      converters.put(convertibleType, converterFactoryAdapter);
    }
  }

  @Override
  public void addConverter(GenericConverter converter) {
    for (ConvertiblePair convertibleType : converter.getConvertibleTypes()) {
      converters.put(convertibleType, converter);
    }
  }
  // 省略....
}
```

### ConverterFactory 和 ConverterRegistery

`ConverterFactory` 接口负责来获取到对应的 `Converter` 类型，

```java
public interface ConverterFactory<S, R> {
  <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
```

`ConverterRegistry` 接口负责提供注册的能力

```java
public interface ConverterRegistry {
  void addConverter(Converter<?, ?> converter);

  void addConverterFactory(ConverterFactory<?, ?> converterFactory);

  void addConverter(GenericConverter converter);
}
```

`Converter<S,T>` 接口适合一对一的类型转换，如果要将 String 类型转换为 Ineger/Long/Float/Double/Decimal 等类型，就要实现一系列的 StringToInteger/StringToLongConverter/StringToFloatConverter 转换器，非常不优雅。

`ConverterFactory` 接口则适合一对多的类型转换，可以将一种类型转换为另一种类型及其子类。比如将 String 类型转换为 Ineger/Long/Float/Double/Decimal 等 Number 类型时，只需定义一个 ConverterFactory 转换器：



## 类型转换二

在 Spring 中，`FactoryBean` 用于创建特定类型的 Bean 实例。我们通过实现 `FactoryBean` 接口可以自定义 Bean 的创建过程，从而灵活地控制实例化逻辑、对象配置等。Spring 会将 `FactoryBean` 看作一个“工厂”，由它来决定生产的实际对象。

```java
public interface FactoryBean<T> {
  T getObject() throws Exception;

  Class<?> getObjectType();

  boolean isSingleton();
}
```

- `getObject()`: 返回 `FactoryBean` 创建的对象实例，即最终要注册到 Spring 容器的 Bean 实例。
- `getObjectType()`: 返回 `FactoryBean` 创建的对象的类型。
- `isSingleton()`: 指定创建的 Bean 是单例（singleton）还是多例（prototype）。



### 类型转换器工厂Bean

定义 `ConvertersFactoryBean` 类负责创建一个包含多个 `Converter` 实例的集合，将其注册到 Spring 的 `ConversionService` 中，从而实现应用中的自动类型转换。

```java
public class ConvertersFactoryBean implements FactoryBean<Set<?>> {
  @Override
  public Set<?> getObject() throws Exception {
    HashSet<Object> converters = new HashSet<>();
    StringToLocalDateConverter stringToLocalDateConverter =
        new StringToLocalDateConverter("yyyy-MM-dd");
    converters.add(stringToLocalDateConverter);
    return converters;
  }

  @Override
  public Class<?> getObjectType() {
    return Set.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}
```

### 类型转换接口工厂Bean

`ConversionServiceFactoryBean` 的职责是创建并配置 `ConversionService`，并注册多个 Converter，这个注册操作是通过 `ConverterRegistry` 类完成，实际上是做一个缓存。

```java
public class ConversionServiceFactoryBean
    implements FactoryBean<ConversionService>, InitializingBean {
  private Set<?> converters;

  private GenericConversionService conversionService;

  @Override
  public ConversionService getObject() throws Exception {
    return conversionService;
  }

  @Override
  public Class<?> getObjectType() {
    return GenericConversionService.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    conversionService = new DefaultConversionService();
    registerConverters(converters, conversionService);
  }

  /**
   * 注册转换器列表
   *
   * @param converters 类型转换器列表
   * @param registry 类型转换注册机
   */
  private void registerConverters(Set<?> converters, ConverterRegistry registry) {
    if (converters != null) {
      for (Object converter : converters) {
        if (converter instanceof GenericConverter) {
          registry.addConverter((GenericConverter) converter);
        } else if (converter instanceof Converter<?, ?>) {
          registry.addConverter((Converter<?, ?>) converter);
        } else if (converter instanceof ConverterFactory<?, ?>) {
          registry.addConverterFactory((ConverterFactory<?, ?>) converter);
        } else {
          throw new IllegalArgumentException(
              "Each converter object must implement one of the "
                  + "Converter, ConverterFactory, or GenericConverter interfaces");
        }
      }
    }
  }

  public void setConverters(Set<?> converters) {
    this.converters = converters;
  }
}
```

### 嵌入bean的生命周期

在属性设置时和注解`@Value`注入属性信息时，都有可能存在通过类型转换的方式来处理。

```java
public class AutowiredAnnotationBeanPostProcessor
    implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
  // 省略...
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName)
      throws BeansException {
    // 处理@Value注解
    Class<?> clazz = bean.getClass();
    clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
    Field[] declaredFields = clazz.getDeclaredFields();
    for (Field field : declaredFields) {
      Value valueAnnotation = field.getAnnotation(Value.class);
      if (valueAnnotation != null) {
        Object value = valueAnnotation.value();
        value = beanFactory.resolveEmbeddedValue((String) value);

        // 类型转换
        Class<?> sourceType = value.getClass();
        Class<?> targetType = (Class<?>) TypeUtil.getType(field);
        ConversionService conversionService = beanFactory.getConversionService();
        if (conversionService != null) {
          if (conversionService.canConvert(sourceType, targetType)) {
            value = conversionService.convert(value, targetType);
          }
        }

        BeanUtil.setFieldValue(bean, field.getName(), value);
      }
    }

    // 2. 处理注解 @Autowired
    for (Field field : declaredFields) {
      Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
      if (null != autowiredAnnotation) {
        Class<?> fieldType = field.getType();
        String dependentBeanName = null;
        Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
        Object dependentBean = null;
        if (null != qualifierAnnotation) {
          dependentBeanName = qualifierAnnotation.value();
          dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
        } else {
          dependentBean = beanFactory.getBean(fieldType);
        }
        BeanUtil.setFieldValue(bean, field.getName(), dependentBean);
      }
    }

    return pvs;
  }
  //省略...
  
}
```



```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory
    implements AutowireCapableBeanFactory {  
private void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
  // 省略..
    try {
      PropertyValues propertyValues = beanDefinition.getPropertyValues();
      for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {

        String name = propertyValue.getName();
        Object value = propertyValue.getValue();

        if (value instanceof BeanReference) {
          // A 依赖 B，获取 B 的实例化
          BeanReference beanReference = (BeanReference) value;
          value = getBean(beanReference.getBeanName());
        } else {
          // 类型转换
          Class<?> sourceType = value.getClass();
          Class<?> targetType = (Class<?>) TypeUtil.getFieldType(bean.getClass(), name);
          ConversionService conversionService = getConversionService();
          if (conversionService != null) {
            if (conversionService.canConvert(sourceType, targetType)) {
              value = conversionService.convert(value, targetType);
            }
          }
        }
        // 属性填充
        BeanUtil.setFieldValue(bean, name, value);
      }
    } catch (Exception e) {
      throw new BeansException("Error setting property values：" + beanName);
    }
  }
  // 省略...
}
```

在使用之前会先获取注入的 ConversionService，而注入的动作是在**刷新容器 `refresh()`**的时候完成。

```java
public abstract class AbstractApplicationContext extends DefaultResourceLoader
  implements ConfigurableApplicationContext {
  public void refresh() throws BeansException {
    // 1. 创建 BeanFactory，并加载 BeanDefinition
    refreshBeanFactory();

    // 2. 获取 BeanFactory
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();

    // 3. 添加 ApplicationContextAwareProcessor，让继承自 ApplicationContextAware 的 Bean 对象都能感知所属的
    // ApplicationContext
    beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

    // 4. 在 Bean 实例化之前，执行 BeanFactoryPostProcessor (Invoke factory processors registered as beans in
    // the context.)
    invokeBeanFactoryPostProcessors(beanFactory);

    // 5. BeanPostProcessor 需要提前于其他 Bean 对象实例化之前执行注册操作
    registerBeanPostProcessors(beanFactory);

    // 6. 初始化事件发布者
    initApplicationEventMulticaster();

    // 7. 注册事件监听器
    registerListeners();

    ++++++// 8. 设置类型转换器、提前实例化单例Bean对象
    finishBeanFactoryInitialization(beanFactory);

    // 9. 发布容器刷新完成事件
    finishRefresh();
  }
  
  protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    //设置类型转换器
    if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME)) {
      Object conversionService = beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME);
      if (conversionService instanceof ConversionService) {
        beanFactory.setConversionService((ConversionService) conversionService);
      }
    }
    //提前实例化单例bean
    beanFactory.preInstantiateSingletons();
  }
}
```

### 测试

```java
public class TypeConversionSecondPartTest {
  @Test
  public void test_conversionService() throws Exception{
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-type-converter-second.xml");

    Car car = applicationContext.getBean("car", Car.class);
    assertThat(car.getPrice()).isEqualTo(1000000);
    assertThat(car.getProduceDate()).isEqualTo(LocalDate.of(2021, 1, 1));
  }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	         http://www.springframework.org/schema/beans/spring-beans.xsd
		 http://www.springframework.org/schema/context
		 http://www.springframework.org/schema/context/spring-context-4.0.xsd">

    <bean id="car" class="org.springframework.test.bean.Car">
        <property name="price" value="1000000"/>
        <property name="produceDate" value="2021-01-01"/>
    </bean>

    <bean id="conversionService" class="org.springframework.beans.context.support.ConversionServiceFactoryBean">
        <property name="converters" ref="converters"/>
    </bean>
    <bean id="converters" class="org.springframework.test.common.ConvertersFactoryBean"/>

</beans>
```

```java
public class ConvertersFactoryBean implements FactoryBean<Set<?>> {
  @Override
  public Set<?> getObject() throws Exception {
    HashSet<Object> converters = new HashSet<>();
    StringToLocalDateConverter stringToLocalDateConverter =
        new StringToLocalDateConverter("yyyy-MM-dd");
    converters.add(stringToLocalDateConverter);
    return converters;
  }

  @Override
  public Class<?> getObjectType() {
    return Set.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}

public class StringToLocalDateConverter implements Converter<String, LocalDate> {
  private final DateTimeFormatter DATE_TIME_FORMATTER;

  public StringToLocalDateConverter(String pattern) {
    DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(pattern);
  }

  @Override
  public LocalDate convert(String source) {
    return LocalDate.parse(source, DATE_TIME_FORMATTER);
  }
}
```

```java
@Component
@Data
@ToString
public class Car {
  @Value(value = "${brand}")
  private String brand;

  @Value(value = "${name}")
  private String name;

  private int price;

  private LocalDate produceDate;
}
```



