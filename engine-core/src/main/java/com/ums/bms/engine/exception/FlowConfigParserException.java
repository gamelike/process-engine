package com.ums.bms.engine.exception;

/**
 * @author violet
 * @since 2025/4/27
 */
public class FlowConfigParserException extends RuntimeException {
    public FlowConfigParserException(String message) {
        super(message);
    }

    public FlowConfigParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
