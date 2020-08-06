## 版本更新记录

# now(1.17.0)
- 实现`@Depend`的`orNull`参数。当`orNull=true`的时候，如果注入的值不存在，则会为其注入null。（默认情况下依旧会报错）

# 1.16.3-BETA
- 为资源加载流程增加部分debug日志

# 1.16.3
- 更新`cqCodeUtils`模组至`1.6.1`
- 尝试优化`javax.annotation.Resource`带来的JDK版本兼容问题
- 改变限流拦截器的key值计算方式。
- 修复限流拦截器中无法根据code分类限流的bug。
- 增加一些待实现的功能代码

- 增加对（被）好友删除事件: `FriendDelete`和对应的注解：`@Listen(MsgGetTypes.friendDelete)` 和 `@OnFriendDelete`
- 删除部分多余、过时内容

# 1.16.2
- 更新`cqCodeUtils`模组至`1.6.0`以解决接收at全体的时候可能会报错的问题

# 1.16.1
- 更新`cqCodeUtils`模组至`1.5.1-1.15`以解决其remove的bug

# 1.16.0
- `quartz` 版本更新到2.3.2
- 集成cqCodeUtils模组，现在开始推荐使用`KQCodeUtils`并弃用原版的`CQCodeUtil`（依旧能用，但是不再维护）。
- 对`@Filter(at = true)`时候的情况做一些特殊处理，现在当at=true的时候，匹配会自动移除文本中的at类型的CQ码了。
- 修复`@Filter`动态参数提取的bug
- 追加注解`@Async(...)`(标记异步函数)，标注在监听函数上以表示此函数为异步执行。异步执行的线程由初始化的线程池控制。
- 追加注解`@Limit(...)`(标记限流函数)，标注监听函数在多长一段时间内可触发。例如`@Limit(5)`，则代表5秒内只会触发一次。此功能基于监听函数拦截器实现。
- 优化`@Filter`注解的`at`参数，现在当at=true的时候，在进行匹配的时候会自动移除掉消息文本中的 **`at`** 类型的CQ码。
- 实现注解的参数映射，现在模板监听注解（例如`@OnPrivate(...)`）提供了与`@Listen(...)`相同的参数（例如`sort`、`name`）
以`@OnPrivate(...)`为例，`@OnPrivate(sort = 500)`等效于`@Listen(value = MsgGetTypes.privateMsg, sort = 500)`
- 上述的注解继承、参数映射功能使用者也可任意自定义，并通过`AnnotationUtils.getAnnotation(...)`获取。


# 1.15.2
- 修复botManager实现类中refreshBot逻辑错误的问题
- 优化国际化语言加载机制


# 1.15.1
- 修复@Filter参数提取的时候如果不是结尾为`{{...}}`就会抛出异常的问题。

# 1.15.0
- 修改接口定义，为一些接口增加获取昵称和备注的方法（例如群消息等）
昵称和备注的获取通过三个接口`NicknameAble`、`RemarkAble`、`NickOrRemark`控制，可用于在自定义过滤器中进行各种判断。
- 为Configuration中增加一些方法：
`putValue(String, Object)`、`getValue(String)`、`clearValue()`、`getValueEntrySet()`
使得Configuration支持存入任何自定义信息了。
- `HttpClientHelper`类增加静态方法`clear()`以清除所有保存的http模板。
- `ListenerFilter`类的自定义过滤器不再是静态了。
- 完善close机制，现在你可以通过组件的Application实例或者run方法所返回的Context实例的close()方法来关闭当前的服务，并通过构建一个新的Application实例来再次启动。
不过一般情况下，我不推荐频繁关闭开启服务。



# 1.14.1
- 修改`@SimpleRobotApplication`的resource参数默认值，由`/simple-robot-conf.properties` 改为 `simple-robot-conf.properties`
- **修复listenerManager中出现的排序异常bug**



# 1.14.0
- 修改配置信息覆盖规则，现在的优先级是：启动参数 > 配置文件 > 注解 
- configuration类中增加runParameter相关, 即启动参数相关。
- 启动参数中，使用'--xxx'来通过启动参数向配置中追加参数。
- 增加一个配置项：`simbot.profiles.active` （或兼容spring：`spring.profiles.active`）, 其值可以允许加载额外的配置文件。类似于Spring。
例如, 你的配置文件是`conf.properties`, 其中你写了一个`simbot.profiles.active=dev,test`, 则除了当前配置文件以外，还会加载`conf-dev.properties`文件和`conf-test.properties`文件。
之后加载的配置信息会覆盖原先的配置信息。你可以结合启动参数`--simbot.profiles.active=`来实现不同环境的多配置文件，例如线上默认是8080端口，本地则是8877端口啥的。

- KeywordMatchType中追加一些正则相关的匹配规则：`FIND`、`TRIM_FIND`、`RE_CQCODE_FIND`、`RE_CQCODE_TRIM_FIND`、`FIND_0`、`TRIM_FIND_0`、`RE_CQCODE_FIND_0`、`RE_CQCODE_TRIM_FIND_0`
其中，结尾为`FIND`的，使用正则的`find(0)`进行匹配。

- 所有`KeywordMatchType`下的正则相关的匹配规则（例如`REGEX`、`FIND`等），全部支持动态参数提取。
动态参数提取的语法：
`{{name[,regex]}}`，其中，name为动态提取参数的名称，regex为其匹配正则。
例如：
```java
@Beans
public class Test{
    /** 监听正则为 'number is (\\d+)'的消息，并提取\\d+为number参数 */
    @Listen(MsgGetTypes.privateMsg)
    @Filter("number is {{number,\\d+}}")
    public void test1(PrivateMsg msg, MsgSender sender, @FilterValue("number") Long number){
        sender.SENDER.sendPrivateMsg(msg, "您的号码为：" + number);
    }
    // ...
}
```
其中`@Filter`默认匹配规则为`REGEX`,因此可以使用动态参数提取，然后再方法参数中添加了`@FilterValue("number") Long number`，
其中`@FilterValue("number")`的参数`number`就是上述@Filter注解中的number参数，number的匹配规则为`\\d+`，即数字。
而@Filter中真正的匹配规则会变成：`number is (\\d+)`。
参数提取语法中，regex可以省略，变成{{name}}，此时匹配规则默认为`.+`。
如果要使用普通的`'{'`字符串，使用反斜杠转义。

- 追加模板注解：
    - @OnDiscuss
    - @OnFriendAdd
    - @OnFriendAddRequest
    - @OnGroup
    - @OnGroupAddRequest
    - @OnGroupAdminChange
    - @OnGroupBan
    - @OnGroupFileUpload
    - @OnGroupMemberIncrease
    - @OnGroupMemberReduce
    - @OnGroupMsgDelete
    - @OnPrivate
    - @OnPrivateMsgDelete
其代表各个对应的监听类型，例如`@OnPrivate` 等同于 `@Listen(MsgGetTypes.privateMsg)`

- 兼容注解`@Resource`, `@Resource`中的`name`与`type`参数分别对应`@Depend`的`value`和`type`

- ListenerManager类开放方法`getListenerMethods()`，你可以通过注入此类并通过此方法得到所有的监听函数实例，配合一些自定义注解即可实现基于监听函数的动态菜单信息。

# 1.13.2
- 修改`BaseConfiguation`一些字段的访问权限

# 1.13.1
- `BeanUtils`替换为`hutool-core`的
- `IOUtils`替换为`hutool-core`的
- `ListenerManager`中尝试使用新的紧凑map
- 修复`botManager`中可能会导致无法获取info的情况
- `SimpleRobotContext`实现`Closeable`接口

# 1.13.0
- 变更监听消息拦截器的加载机制
- 依赖加载的日志类型变更为`debug`
- 依赖中心增加`Closeable`接口的实现，当执行close的时候，会将所有实现了`Closeable`接口的**单例**对象遍历并close，然后清除单例值。
- 为ListenContext增加静态方法`getLocal()`，当监听函数触发的时候，会将ListenContext存入当前线程的`ThreadLocal`中, 并在监听函数全部执行结束后清除。

- 增加监听函数拦截器`ListenIntercept`，使用方法即实现`ListenIntercept`接口并标注`@Beans`注解，当函数返回true即为放行，返回false即为拦截。
- 增加context类`ListenInterceptContext`，为`ListenIntercept`中使用，提供了大量ListenMethod中可获得的参数。

- `MsgSender`中增加`reply(...)`方法来支持快捷回复。此方法判断MsgGet参数的类型，如果不是`PrivateMsg`、`GroupMsg`、`DiscussMsg`三种类型其中之一则会抛出异常。

- 预装一个实验性功能`runAuto()`


# 1.12.1 (beta)
- 配置中增加一个配置项`simbot.core.checkBot`，默认为true。当为true的时候，监听函数触发前会优先判断当前消息所接收的bot是否为已注册的bot，如果为未注册bot则其将会被拦截。
- 简单优化HttpClientAble
- 将常量类`com.forte.qqrobot.PriorityConstant`移动至`com.forte.qqrobot.constant.PriorityConstant`
- 修复默认的`BotManager`实现类中，使用带账号的注册会导致无法触发验证的bug
- 修改部分需要初始化的bean的初始化流程。
- 修改默认`BotManager`的自动配置形式。
- 修复依赖中心中疑似会导致死锁的bug
- 修改`ConfigProperties`类的实现，可能会导致兼容问题。


# 1.12.0 (beta)
- 优化依赖工厂，并修复部分隐藏bug
- `@Beans`增加两个参数：`boolean init() default false`和`int priority() default Integer.MAX_VALUE`
分别代表被标注的Beans是否在依赖工厂注入流程结束后执行一次初始化和这个Beans的优先级。
- 为`@Beans`追加优先级概念。当在通过类型获取一个Beans的时候，如果依赖工厂中存在多个此类型的实例，则会选择优先级最高的(`升序排序，即数值最小的`)使用。
例如，`TestInterface`接口存在两个实现类`Test1`和`Test2`，他们所标注的`@Beans`注解分别为`@Beans(priority = 1)`和`@Beans(priority = 2)`, 则在获取`TestInterface`d额时候会获取到`Test1`。
注意，当最高优先级存在多个的时候，将会抛出异常。
默认情况下优先级为最低，即`Integer.MAX_VALUE`
- 为BotInfo追加接口`closeable`的继承。
- 增加一个`PriorityConstant`常量类，定义了一些比较基本的常量。
- 修改`BotManager`接口的`registerBot`方法的返回值，修改前为`boolean`，目前为注册成功后的`botInfo`
- 基础配置类中追加指定类加载器的配置`setClassLoader`用于一些类似于包扫描的地方。默认为当前线程中的类加载器。
- 将`BotManager`的内部默认实例`BotManagerImpl`由硬编码形式修改为模组自动加载形式，其优先级为默认的最低。

- 增加了一个会被默认注入到依赖工厂的`ConfigProperties`实例，当你使用了配置文件启动的时候，可以通过此类得到配置文件中的配置项。可用来为模组提供额外的配置。
- 配置文件现在推荐在所有的配置前缀增加`simbot`以进行大分类。旧配置暂时依旧可用，但是不再推荐。

- 为三个送信拦截器的参数的父类`SenderContext`追加一个方法`getMethod()`以支持获取当前拦截的方法实例。
- 三个送信拦截器将不会再拦截Object的默认方法了。（例如`toString()`方法，除非组件实例重写了此方法。）

- 大幅度调整内部结构。现在启动时，如果没有注册任何bot信息，将不会再强制注册一个默认地址了，而是变更为一个警告。



# 1.11.4
- 语言系统中增加模组(module)相关语言加载
- 修复ListenContext对象在使用的时候无法正常依靠`get`与`set`取值设值的问题，并在Context中追加一个`put`方法，含义与`get`一致。

# 1.11.3
- 修复CQAppendList中出现二次转义的情况。
- 修复CQCodeUtil中获取CQ码的时候出现索引越界的异常。
- 群签到接口默认为“运势”签到

# 1.11.2
- 修复CQCode相关操作中的bug
- 接口`QQCodeAble`、`GroupCodeAble`中分别默认实现了获取QQ头像、群头像的方法。
- 为一些接口继承`QQCodeAble`、`GroupCodeAble`

# 1.11.1
- 将部分ListenerManager内部代码换回原来的模式

# 1.11.0
- 为BotManager增加两个方法：`BotInfo logOutBot(String code)`、`public void refreshBot(String code)`以移除、刷新一些bot信息。
- 为`BotInfoImpl`提供默认的equals方法。
- 修改依赖工厂机制，现在获取不到依赖将会**抛出异常**而不是以null代替。
- 修改监听函数加载机制，现在监听函数在启动的时候会初始化一次。
- 在run方法的返回值BaseContext中增加获取DependCenter的方法。


# 1.10.7
- 默认的httpClient在请求接口并返回值code为300及以上的时候抛出的异常将会被抛出而不是被捕获。
- 1.11.x之前预实装异常处理机制。对应文档：[https://www.kancloud.cn/forte-scarlet/simple-coolq-doc/1614982](https://www.kancloud.cn/forte-scarlet/simple-coolq-doc/1614982)
- `MsgIntercept`消息拦截器中的参数`MsgGetContext`中追加参数`ListenContext`
- 使`ListenContext`继承`BaseContext`抽象类。


# 1.10.6
- 修复在一些控制台编码不是UTF-8的控制台中输出中文可能会乱码的问题。
- 简单修改部分代码的部分逻辑


# 1.10.5
- 优化内部默认的HttpClient模板中对于Cookie的处理
- 为Setter中的群签到增加默认的实现api
- 并且增加一个BaseAPITemplate类以提供一个公用的默认模板API
- 为loginInfo中的头像地址增加默认实现
- 为内部的异常类提供一个新的获取message的方法`getLangMessage`
- 更新版本检测时的日志信息内容
- 简单修改`AnnotationUtils`使其能够适应其他情况
- 修复`QQLog`中，自定义的`QQLogBack`所得到的所有异常实例均为null的问题
- 版本检测方案变更为使用阿里云maven镜像仓库进行检测。

# 1.10.4
- 替换代码中所使用的`EmptyInputStream`为代码内部实现的类而非`httpcore`内的类，以防止一些可能发生的版本冲突。

## 1.10.3
- 修复CQCodeUtil中取出CQ码的bug，并且不再使用正则取值。

## 1.10.2
- 降低CQ码的参数匹配等级
- 增加at判断函数的注册
- 增加一些未来代码

## 1.10.1
- 修复由于`@Filter`过滤机制的变动而导致`KeywordMatchTypeFactory`工厂创建的枚举无法使用的BUG。


## 1.10.0
- 修改SENDER接口，为`setGroupLeave`(退出群)API增加一个参数：`dissolve`: 假如此账号是群主，则此参数代表是否要解散群。默认为false



## 1.9.0
- SENDER中增加一个新的接口：发送群公告(sendGroupNotice)
- 由此版本开始，SENDER中的：`sendPrivateMsg`、`sendGroupMsg`、`sendDiscussMsg`将修改返回值，由`boolean`修改为`String`来代表发送的消息的消息ID，以实现可以对自己的消息进行撤回。
   一般情况下，如果返回的字符串为`null`，则可能代表原返回值的`false`
   注意: 如果你的sender在发送消息的时候被**送信拦截器**所拦截，则默认的返回值可能是一个空字符串而不是`null`。
   
- 启动器中，修改面向组件的约定，分离启动器中的“组件启动”、“组件服务启动”等配置
- 将类似于“无服务启动”的选项整合进核心中，提供一个配置项`core.enableServer`来决定是否需要让组件启动一个服务，默认为`true`，即开启。
其中后两项，由于组件的启动机制修改，你可以通过这两项对象来实现自定义消息接收，并以更高效的处理环境来处理。
例如，你设置了上述配置属性`core.enableServer=false`，来阻止组件内部的服务器的启动(它们大多数效率不够高效)，然后通过上面提到的"监听函数字符串转化器"与"监听函数执行器"来实现通过外部搭建的服务来分配任务(例如常见的springboot+tomcat的web服务或者基于netty的高效nio服务)
 
- 监听器处理逻辑变更：当接收到未注册的bot的消息的时候，不会处理消息内容并提供一个error日志。
   
- 为`Filterable`接口增加一个参数：`ListenContext` (监听上下文)
- 为`Filterable`接口修改一个参数：`@Filter` (注解对象) 修改为类`Filter` (与注解同名的数据类对象)
- 将监听执行异常的文字加入到语言文件
- 修复`ListenContext`的"当前上下文"无法连携当前上下监听器值的bug
- 简单修改`ListenContext`内部结构
- 修改了`@Filter`内部对于`value`参数的匹配规则, 理论上会略微增加效率

- 增加接口`HttpClientAble`接口，以实现自定义http请求方式, 使得用户可以摒弃原本内部的`HttpClientUtil`的使用。如果没有实现，核心内部会提供一个默认的实现方式，类似于原本的`HttpClientUtil`。
- 增加`HttpClientHelper`类，以使用上述的`HttpClientAble`接口，使用`registerClient`方法注册。
- 增加注解`@HttpTemplate`来自动注册一个`HttpClientAble`接口的实现类到`HttpClientHelper`类中。

- 调整父类启动器`BaseApplication`对于子类的约束方式，实现服务启动与信息获取分离，实现了由核心框架控制服务开启。
- 调整父类启动器`BaseApplication`的`run`方法，使其拥有返回值，并将原本可以通过`application`实例对象获取的值移动到`SimpleRobotContext`中获取。
- 提供启动方法`run`的返回值`SimpleRobotContext`(或者是此类针对于组件的特殊子类)，并在其中提供提供一些有用的函数，例如：
  - 默认的三大送信器
  - `BotManager`
  - 监听函数原生字符串转`MsgGet`对象的函数
  - 监听函数的执行器
  - `DependCenter`依赖中心
  
- `@Filter`注解中增加三个参数：`String[] bot()`、`KeywordMatchType botMatchType()`、`MostType mostBotType()`, 这三个参数与群号过滤、qq号过滤类似，用来筛选"当前接收消息的BOT"。假如监听消息获取到的"ThisCode"参数为null，则默认放行。
- `@Filter`匹配策略修改：如果当前消息是**群号携带者**但是获取到的群号为null，则一律放行。（之前为不放行）
- `@Filter`匹配策略修改：如果当前消息是**账号携带者**但是获取到的账号为null，则一律放行。（之前为不放行）
- 目前过滤器自带的过滤规则顺序为：bot -> group -> code -> word -> 自定义

- 修改注解`@SimpleRobotApplication`注解中的`application`参数的匹配规则，如果在`resources`路径中无法获取，则会尝试将其作为文件路径获取。




## 1.8.1
- 修改默认情况下的线程池线各项参数：<br>
    （注：`最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数), 其中: 0 >= 阻塞系数 > 1`）
    - 默认线程阻塞系数为0
    - 默认情况下核心线程数量为最佳CPU线程数量的 1/2
    - 默认情况下最大线程数量核心线程数量的2倍+1
- CoreSystem类中增加两个方法，以检测当前核心版本和获取当前版本，并将当前核心版本加入SystemProperties    
- 配置增加一项：`core.checkVersion`, 参数为布尔类型，即检查当前核心版本下是否有更新的、可直接覆盖的版本。默认为`true`
- 为`GroupInfo`和`GroupList`中的`Group`增加默认的`getHeadUrl` (获取群头像)接口实现。    
- 优化注解配置与启动器的启动转化逻辑，使其支持标注在任何实现了`Application`接口的类上时，会获取其实例并执行。配置覆盖顺序：代码配置 -覆盖-> 注解配置 -覆盖-> 文件配置    
- 修复`AtDetection`在`1.8.x`后出现的bug




## 1.8.0
- KeywordMatchType枚举中增加更多预设：
    STARTS_WITH、TRIM_STARTS_WITH、RE_CQCODE_STARTS_WITH、RE_CQCODE_TRIM_STARTS_WITH、ENDS_WITH、TRIM_ENDS_WITH、RE_CQCODE_ENDS_WITH、RE_CQCODE_TRIM_ENDS_WITH
    分别对应了startsWith与endsWith的4种情况
- 实现支持多账号, 并修改配置类与文件配置，增加部分与多账号相关的配置。
- 增加针对多账户注册的文件配置信息: "core.bots"
- 增加`BotManager`类，以管理多账号。
- 增加`BotManagerImpl`类，以实现自定义BotManager, 例如切换Bot数据的获取形式为使用数据库或者redis等，使其可以适应分布式系统等其他复杂架构。
- 为`GroupMsg`类型增加了`PowerAble`的消息接口，以获取此消息的群员在群内的权限。
- 增加接口`Filterable`以支持使用者自定义过滤规则。
- 以上述的`Filterable`为前提，增加MostDIYType，用来当`@Filter`中出现了多个`Filterable`的时候的匹配规则。
- 在实现了`Filterable`接口的前提下，增加一个注解`@DIYFilter`以指定自定义filter的名称。(同时支持`@Beans`)
- `@Filter`中增加两个参数以使用上述的自定义Filter。
- 增加两个注解：`@SimpleRobotApplication` 和 `@SimpleRobotConfiguration`, 以支持注解形式的启动, 基本摒弃早期的代码配置。
- CQ码中增加：`show`、`contact`、`rich`、`hb`类型
- 增加一个`BotRuntime`类，其可在启动后通过静态代码获取。
- bug修复:
    - 修复依赖注入的实例构建参数自动注入bug
    
            
**※ 注①：此版本核心不向下兼容。**

**※ 注②：多账号功能目前仍在测试，如果遇到BUG请及时反馈。**
            
        


## 1.7.0
- FriendList接口增加：`getAllFriends()`
- QQLog的warning字体更换为黄色
- config中增加locale相关配置
- QQLogBack接口参数调整
- 在MsgGetType中增加几种监听事件类型(但是不一定是有用的)：
    - 群消息撤回
    - 私信消息撤回
- 简单修改监听器的ID生成规则    
- 增加一个新的可使用类：SenderAPIManager, 计划会用于获取所有的送信器中存在的API, 一般用于debug
- 配置中，将所有的配置名称中的前缀"simple.robot.conf" 变更为 "core"
- 优化线程池初始化参数，并增加一个新的线程池参数：阻塞系数(blockingFactor) 
- BaseConfiguration类不再重写toString方法
- 增加了一个异常类 `RobotRunException` 
- 在`MsgGet`类型的监听消息中（基本上属于所有的监听消息）开放接口：
    - `setMsg` , 用来重新设置msg消息内容。
    - `getThisCode`，用来获取“接收到这条消息的账号是哪一个”。
        
    -  [warning]  这会使得 `1.7.x` 无法向下兼容。

- 1.7版本内未来会新增加的功能：
    - 获取所有的监听器的信息：
        - 增加注解：@ListenerAPI
    - 增加消息封装类：ListenerInfo    
    












## 1.6.3
- 将@listen的排序默认值从1修改为100
- 修复logLevel的等级数值混乱的问题
- 修复送信拦截器在不存在@Beans注解的情况下依旧能够被注入的问题
    
## 1.6.2
- 修复配置文件读取无法读取中文的问题

## 1.6.1(not deploy success)
- BaseContext中增加全局上下文参数
- 优化enum工厂的异常展示与处理
- 简单优化部分内部内容

## 1.6.0
- 简单系统优化
- filter中增加对code与group的过滤规则自定义参数
- ※ 不兼容点：修改拦截器相关类的包路径
- 增加送信拦截器接口
- 内部结构优化

## 1.5.0
- 将@Listen标记恢复为单例
- 增加@ListenBody注解
- MsgGetTypes增加群禁言类型
- 修改额外依赖对象
- 增加监听拦截功能
- 增加监听上下文对象
- 修改被at判定机制
- 修改Filter匹配过滤顺序：现在的顺序：关键词->群号->QQ号
- 删除部分无用注释
- 优化ListenResult机制
- ListenResultImpl增加无参构造
- 优化日志类，增加全局日志输入级别
- 会将一个无监听函数的MsgSender注入至依赖中心
- 配置中增加日志级别
- 增加lang文件，为后续的语言文件做准备
- 为版本更新做准备

## 1.4.3
- 修复了1.4.2不能用的bug

## 1.4.2
- 移除image的CQ码中的url参数。
- 为定时任务提供依赖注入功能。需要使用封装接口且必须存在@Beans注解。

## 1.4.1
- 优化CQ码的匹配(contains)方式，原本的匹配方式在字符串中存在换行符的时候会失效。
- image类型的CQ码似乎增加了url参数，但是官方文档并没有指出，所以暂且增加一个url参数且属于可忽略类型。
- 优化CQ码根据参数名列表的转化方式，现在，在CQ码中存在一些多余的key的时候也能够比较好的筛选出cq码的类型了。
- 根据上述优化，间接优化了CQCodeUtil中从字符串提取CQ码的类型判断精准度。
- 优化CQCode对象内部结构，开放CQCode对象中对于部分参数的setter，取消他们的final修饰。
- 群员相关的消息接口中增加一个方法来判断nickname或者普通的name
- **※不兼容点** 变更枚举工厂的包路径。

## 1.4.0
- 改变执行机制，增加注解 `@ListenBreak` 以决定是否截断后续的执行。
- 增加一个接口 `ListenResult` 来控制方法执行的返回值。返回值中的截断参数优先级高于上述的注解形式。
- 增加一个启动器App接口的新实现以实现配置文件的读取。
- 改变MsgGetTypes枚举对象的获取方式以整合枚举工厂。
- 优化基础配置类的配置字段，放在idea里大概会自动高亮了。大概。
- **※不兼容点** 在启动器启动流程中变更start方法的参数（面向开发者的变更）。
    
    

## 1.3.11
- 请不要再使用核心为1.3.10以下的组件
- 修复了一个1.3.10由于手太快压根没测试就上线儿结果出现了问题的bug

## 1.3.10
- 不再使用BETA版本
- 修复了动态参数中的参数注入判断的问题
- 增加枚举工厂，增加byName注解
- 还修复了一些东西但是我忘了都是啥了

## 1.3.9-BETA 
- 先行版, 主要是为了证明我其实是有在更新的。
- 修复若干bug，例如shareCQ码的判断规则，在过滤中填写at类型CQ码将无法判断等。
- 增加若干未实装功能的代码，例如：自定义监听类型、过滤规则，过滤路径参数截取，
- 代码注入
- 简单优化部分启动流程、CQCodeUtil方法。
- 我印象中我改了好多好多东西但是我现在已经想不起来了。
    
    


## 1.3.5-BETA
- 在`utils.proxyhelper`包中添加供开发者使用的便捷工具
- 修复`CQCodeUtil`中`isContains`方法的错误判断
- 在`codeAble`和`GroupAble`接口忠增加群号/qq号转化为Long类型的相关API 
- 修改`BaseApplication` 结构。
- 在`BaseApplication` 中增加部分穿插在执行流程中的扩增方法，使得开发者可作为插件进行扩展。   
- 变更`run()`启动方法的内部执行流程。
- 优化image类型的参数正则匹配规则
- 在`CQCode`中增加对于字符串或者CQCode对象的拼接API，并提供一个新的返回值：`AppendList`
- `CQCode`新实现以下接口：
    - `java.lang.CharSequence`( String类也实现的接口 )
    - `Comparable<CQCode>`( 可根据一定类型顺序进行排序 )
    - `java.io.Serializable`
- 移除`GETTER`进入缓存模式时的多余输出    
    
        

## 1.3-BETA
- 增加本地服务器（※ 尚未开放使用）
- 增加所有消息封装的对应抽象类实现，以简化组件开发成本及用户体验
- pom中增加依赖（个人工具拓展依赖）
- 尝试实现getter的缓存代理 (※ 测试较少可能存在效率问题）
- 修改若干小地方
- 增加一些待实现的计划代码
- `configuration`配置类更新为链式编程的格式
- 变更配置类的内部结构
- 使存在注解@Listen的类在注入的时候默认为非单例对象
- 移除掉包括初始化监听器在内的所有监听器接口及其相关配置
- 增加部分暂未开放使用的预先代码
    
## 1.2.4-BETA
-  `@Filter`注解中增加群号与QQ号过滤功能
    对`CQCode`对象进行拓展：实现了java的`java.util.Map`接口，现在可以想使用Map一样来对CQ码的参数进行操作了。（CQ码中的原始参数不可进行操作。）
    `CQCodeUtil`中: 
- 增加一个获取at全体的方法; 
- 开放参数转义方法并增加参数解码方法;
- 所有获取CQ码字符串的方法均增加对应的获取CQ码封装对象的方法，并将原方法标记过时。（`@Deprecated`）    
- 由于增加了获取对应CQ码封装对象的方法，于是同时在`CQCode`对象的构建过程中增加参数合法性判断。如果忽略了不可忽略的参数、使用了非法的参数格式（例如at的CQ码里用中文）则会抛出异常。        
      ※ CQ码对象直接使用`toString()`方法即为对应CQ码字符串。  
      
    
## 1.2.3-BETA
-  在启动器类中，增加一种有参构造以拦截日志的输出。
    群添加事件中增加一个参数以确定此事件的类型
    在SENDER、GETTER、SETTER中分别增加了对应的简化API。

## 1.2-BETA
-  修复`InfoResultList`接口中的方法`isEmpty`判断错误的问题
    为送信器的实现接口增加部分整合抽象类
    消息封装类中的`getOtherParam`支持多层级获取了。（例如：`result.name`）
    在信息获取接口中开放部分需要选择是否使用缓存或者查询条数的接口并提供默认值
    
## 1.1.3-BETA
-  修复动态参数注入的bug    
    在`GETTER`接口中增加部分需要填写获取数量和是否使用缓存的接口
    增加了为开发者提供遍历的工具类`JSONUtils`

## 1.1.2-BETA
-  BaseApplication中：
- 向子类开放 `线程初始化` 方法
- `start()` 执行结束后输出执行结束语句
- 修复ListenerManager中的隐患bug
- 修复监听函数的动态依赖注入无法整合外部注入的bug


## 1.1.1-BETA
-  更新StrangerInfo接口，增加获取昵称的接口。
移除上一个版本所提到将会移除的两个接口。

## 1.1-BETA
-  更新非注解形式依赖的批量注入功能
在GroupMsg中增加了获取群名称的方法（将会在下一个版本再次移除）
在PrivateMsg中增加了获取发送人昵称的方法（将会在下一个版本中再次移除）
在MsgSender中增加了部分方法以可以便利的通过携带群号、QQ号的对象获取对应的信息

## 1.0.4-BETA
-  完善依赖注入的动态参数问题
重新定义接口结构。
** 不能使用比此版本更低的核心进行开发。接口定义存在严重问题 **

## 1.0.2-BETA
-  修复依赖注入的类型判断问题

## 1.0.1-BETA
-  在消息接口中增加方法，定义获取消息封装的原生数据，以便进行错误排查

## 1.0.01-BETA
-  为定时任务增加一个抽象类

## 1.0-BETA
-  增加依赖注入功能：
    增加注解：@Beans, @Depend
    配置类中增加自定义依赖获取接口
    ***从本版本开始，所有监听函数均需要标注@Beans注解。***


## 0.9.01-BETA
-  为InfoResultList接口增加实现接口：Iterable,现在如果返回值为List类型的数据，则可以直接进行遍历了
为InfoResultList接口增加默认实现方法：Stream<T> stream(),现在如果返回值为List类型的数据，则可以转化为Stream对象了

## 0.9-BETA
-  实现定时任务功能

## 0.8.04-BETA
-  为BaseApplication增加Closeable接口，表示开发者需要实现用户可手动关闭连接的操作

## 0.8.03-BETA
-  移除数据库相关依赖
