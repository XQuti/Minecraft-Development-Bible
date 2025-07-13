package io.xquti.mdb.exception;

public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s with id %d not found", entityName, id));
    }
    
    public EntityNotFoundException(String entityName, String field, String value) {
        super(String.format("%s with %s '%s' not found", entityName, field, value));
    }
}