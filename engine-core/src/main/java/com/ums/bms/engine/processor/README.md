# 方法绑定处理器设计文档

## 概述

方法绑定处理器是一种灵活的数据处理机制，允许在配置中动态绑定字段与处理方法，实现数据的灵活转换和处理。本设计支持两种方法绑定处理器：

1. **基本方法绑定处理器**：支持简单的字段到方法的绑定
2. **高级方法绑定处理器**：支持方法链和多参数方法调用

## 核心组件

### 1. 处理器接口 (Processor)

定义了处理器的基本行为，包括初始化、处理和资源释放等方法。

```java
public interface Processor<OUT> {
    default void beforeProcess(Node node) {}
    default void initial(Node node, Context context) {}
    OUT process(Context context);
    default void close() {}
}
```

### 2. 方法绑定处理器 (MethodBindingProcessor)

实现了基本的方法绑定功能，支持将字段与方法进行绑定，并在处理时动态调用这些方法。

主要特点：
- 支持字段到方法的一对一绑定
- 支持输出字段自定义
- 支持处理整个输入对象或特定字段

### 3. 高级方法绑定处理器 (AdvancedMethodBindingProcessor)

在基本方法绑定处理器的基础上，增加了更多高级功能：

- 支持方法链（多个方法按顺序处理同一字段）
- 支持多参数方法调用
- 支持从不同来源获取参数（字段值、全局参数、常量）

### 4. 处理器工具类 (ProcessorUtils)

提供了各种可被方法绑定处理器调用的工具方法，如：

- 过滤IP地址
- 转换VLAN格式
- 检测接口类型
- 提取IP地址和子网掩码
- 处理VRRP信息

## 配置示例

### 基本方法绑定

```yaml
methodBindings:
  vlanId:
    beanName: "processorUtils"
    methodName: "convertVlanFormat"
  
  name:
    beanName: "processorUtils"
    methodName: "detectInterfaceType"
    methodParams:
      outputField: "type"
```

### 高级方法绑定（方法链）

```yaml
methodBindings:
  ip:
    - beanName: "processorUtils"
      methodName: "extractIpAddress"
      methodParams:
        outputField: "ipAddress"
    - beanName: "processorUtils"
      methodName: "extractSubnetMask"
      methodParams:
        outputField: "subnetMask"
```

## 使用场景

方法绑定处理器适用于以下场景：

1. **数据转换**：将原始数据转换为标准格式
2. **数据提取**：从复杂数据中提取特定信息
3. **数据验证**：验证数据是否符合特定规则
4. **数据处理链**：对数据进行一系列处理

## 扩展方式

要扩展方法绑定处理器的功能，可以：

1. 在ProcessorUtils中添加新的工具方法
2. 创建自定义的Bean，并在配置中引用
3. 扩展MethodBindingProcessor或AdvancedMethodBindingProcessor，添加新的功能

## 注意事项

1. 方法绑定处理器依赖于Spring的Bean管理机制
2. 方法参数类型需要与字段值类型兼容
3. 处理大量数据时需要注意性能问题
