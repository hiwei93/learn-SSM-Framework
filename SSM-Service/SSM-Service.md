# Java高并发秒杀API之Service层
DAO层工作演变为：接口设计+SQL编写
DAO层拼接等逻辑在Service层完成

## 一、Service接口设计
新建包：
- service：
- exception： 存放Service层的异常，比如重复秒杀、秒杀已停止等；
- dto：数据传输层，与entity相似，但是web与service的数据传递，比如分页信息等。

业务接口设计：站在“使用者”角度设计接口
三个方面：
- 方法定义粒度：定义明确，不关注具体实现；
- 参数：简单、直接；
- 返回类型（return 类型/ 异常）：返回类型要友好；

## 二、Service接口实现
使用枚举Enum标出常量数据字典，即将state和stateInfo封装成枚举类型

## 三、Spring管理Service依赖
通过Spring IOC来实现
### 1. 业务对象依赖关系
```
					SeckillDao    -->
SeckillService --> {                     } -->SqlSessionFactory
					SuccessKilledDao -->
```

### 2. 为什么用IOC
- 对象创建统一托管
- 规范的生命周期托管
- 灵活的依赖注入
- 一致的获取对象方式

### 3. Spring-IOC注入方式和场景
|注入方式     | 应用场景 |
|:----------:|:-------------:|
|xml         | 1.Bean实现类来自第三方类库，如：DataSource等； 2.需要命名空间配置，如：content，aop，mvc等。 |
|注解         |项目中自身开发使用的类，可直接在代码中使用注解，如：@Service,@Control等。|
|java配置类   | 需要代码通过控制对象创建逻辑的场景，如：自定义修改依赖类库等。 |

### 4. 本项目IOC的使用
- xml配置
- package-scan
- Annotation注解

**Service配置：spring-service.xml**
扫描service包下所有使用注解的类型
``` xml
<context:component-scan base-package="org.seckill.service"/>
```

**基于注解的配置**
SeckillServiceImpl.java
- 声明该类为Service；
- 注入DAO层。

``` java
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

	... ...
}
```

## 四、Spring声明式事务
### 1. Spring声明式事务的方式
1. ProxyFactoryBean + xml：Spring早期使用方式（2.0）；
2. tx:advice-aop命名空间：一次配置永久生效；
3. 注解@Transactional：注解控制（推荐）。

### 2. 事务方法嵌套
声明式事务独有概念
Spring默认的传播行为是：propagation_required

### 3. 回滚事务
- 抛出运行期异常（RuntimeException）；
- 小心不当的try-catch；

### 4. 配置声明式事务
spring-service.xml
1. 配置事务管理器
``` xml
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!-- 注入数据库连接池 -->
        <property name="dataSource" ref="dataSource"/>
    </bean>
```

2. 配置基于注解的声明式事务
``` xml
<tx:annotation-driven transaction-manager="transactionManager"/>
```

3. 在需要声明事务的方法上添加@Transactional注解
``` java
@Transactional
public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException { ... ... }

```

> 说明：使用注解控制事务方法时
> 1. 开发团队达成一致约定，明确标注事务方法的编程风格；
> 2. 保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部；
> 3. 不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制。无

## 五、Service集成测试
1. 选中SeckillService类，按Ctrl+shift+T创建测试类
2. 测试类配置初始化参数
``` java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
```

3. 注入SeckillService
``` java
    @Autowired
    private SeckillService seckillService;
```

4. 配置slf4j
slf4j是一个标准，具体实现是靠logback，获取logback的配置：https://logback.qos.ch/manual/configuration.html
新建配置文件logback.xml
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true">

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are  by default assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

5. 测试类中引用slf4j打印日志
``` java
private final Logger logger = LoggerFactory.getLogger(this.getClass());
```