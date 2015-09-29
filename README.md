stats-helper
=======================

统计最近一段时间的数据

* 支持自定义的时间间隔（默认10秒、1分钟和1小时）
* 自定义统计的数据类型
* 目前只支持jdk1.8

## 使用

```xml
<dependency>
    <groupId>com.github.phantomthief</groupId>
    <artifactId>stats-helper</artifactId>
    <version>0.2.0-SNAPSHOT</version>
</dependency>
```

```Java

// 声明
SimpleDurationStats<SimpleCounter> durationStats = SimpleDurationStats.newBuilder().build();
        
// 执行统计
durationStats.stat(SimpleCounter.stats(10));

// 获取统计数据，key是时间间隔
Map<Long, SimpleCounter> stats = durationStats.getStats();

// 友好输出
DurationStatsUtils.format(stats, Object::toString);
    
```

## 注意事项

* 定制的Counter必须实现com.github.phantomthief.stats.n.counter.Duration接口
* 可以使用MultiDurationStats实现一个不同key的统计集合