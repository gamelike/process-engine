package com.ums.bms.engine.processor;

import ch.qos.logback.classic.Level;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import static org.mockito.Mockito.*;

/**
 * @author violet
 * @since 2025/4/27
 */
@ExtendWith(MockitoExtension.class)
public class ProcessorRegistryTest {

    @Spy
    ProcessorRegistry processorRegistry;

    @Mock
    Logger log;

    @BeforeEach
    void initMockito() {
        log = (Logger)
                LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        processorRegistry = new ProcessorRegistry();
        spy(processorRegistry);
    }


    @Test
    void test_debug_log_processor_registry_return_is_succeed() {
        debug_log();
        processor_registry_return_is_succeed();
    }

    @Test
    void test_info_log_processor_registry_return_is_succeed() {
        info_log();
        processor_registry_return_is_succeed();
    }

    @SuppressWarnings("rawtypes")
    void processor_registry_return_is_succeed() {
        processorRegistry.registry();
        Processor regexProcessor = processorRegistry.getProcessor("RegexProcessor");
        Processor methodBindProcessor = processorRegistry.getProcessor("MethodBindingProcessor");
        Processor AdvancedMethodBinding = processorRegistry.getProcessor("AdvancedMethodBindingProcessor");
        Assertions.assertThat(regexProcessor).isNotNull();
        Assertions.assertThat(methodBindProcessor).isNotNull();
        Assertions.assertThat(AdvancedMethodBinding).isNotNull();
    }

    void debug_log() {
        log.setLevel(Level.DEBUG);
    }

    void info_log() {
        log.setLevel(Level.INFO);
    }
}
