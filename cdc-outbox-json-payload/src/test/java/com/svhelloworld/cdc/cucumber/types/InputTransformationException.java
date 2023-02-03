package com.svhelloworld.cdc.cucumber.types;

/**
 * Represents an exception thrown during an attempt to transform a {@link io.cucumber.datatable.DataTable}.
 */
public class InputTransformationException extends RuntimeException {
    
    public InputTransformationException(Throwable cause) {
        super(cause);
    }
    
    public InputTransformationException(String message, Throwable cause) {
        super(message, cause);
    }
}
