package com.ums.bms.engine.processor;

import com.ums.bms.engine.model.Context;
import com.ums.bms.engine.model.Node;

/**
 * 处理器接口
 * 
 * @param <OUT> 输出类型
 */
public interface Processor<OUT> {
    /**
     * 在流程处理和初始化参数之前调用的切面方法
     * 
     * @param node 节点
     */
    default void beforeProcess(Node node) {
    }
    
    /**
     * 用于构建和初始化相关节点的参数信息和资源
     * 
     * @param node 节点
     * @param context 上下文
     */
    default void initial(Node node, Context context) {
    }
    
    /**
     * 具体的流程处理
     * 
     * @param context 上下文
     * @return 处理结果
     */
    OUT process(Context context);

    /**
     * 在流程结束之后的切面方法
     *
     * @param node 节点
     */
    default void afterProcess(Node node) {
    }
    
    /**
     * 释放节点相关的资源
     */
    default void close() {
    }
}
