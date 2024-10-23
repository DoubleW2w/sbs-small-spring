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

<p></p>

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

对象作用域简单分为 `singleton` 和 `prototype`，单例的情况下，会从缓存中查看bean对象是否已经生成过，否则调用生成方法。原型情况下，不会做判断，而是每次都会调用生成方法。



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
