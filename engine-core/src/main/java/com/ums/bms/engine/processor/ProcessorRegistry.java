package com.ums.bms.engine.processor;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Processor注册处理器
 *
 * @author violet
 * @since 2025/4/27
 */
@SuppressWarnings("rawtypes")
@Slf4j
public class ProcessorRegistry {
    private Map<String, Processor> cache;

    public void registry() {
        cache = new HashMap<>();
        ServiceLoader<Processor> loader = ServiceLoader.load(Processor.class);
        log.info("initial engine processor");
        AtomicLong count = new AtomicLong();
        loader.forEach(it -> {
            if (log.isDebugEnabled()) {
                log.debug("ProcessorRegistry registry processor: {}", it.getClass().getSimpleName());
            }
            count.getAndIncrement();
            cache.put(it.getClass().getSimpleName(), it);
        });
        log.info("initial engine processor completed count: {}", count.get());
    }

    public Processor getProcessor(String processorName) {
        return cache.get(processorName);
    }

}
