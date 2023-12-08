# brave springboot task

为springboot task异步任务组件提供链路监控能力

## 如何使用
引入依赖
```xml
<dependency>
    <groupId>com.xmair</groupId>
    <artifactId>breave-instrumentation-spring-task</artifactId>
    <version>5.14.1-SNAPSHOT</version>
</dependency>
```

在线程池配置中，设置TaskTracingDecorator修饰器实现对链路的监控

```java
executor.setTaskDecorator(TaskTracingDecorator.create(tracing));
```

