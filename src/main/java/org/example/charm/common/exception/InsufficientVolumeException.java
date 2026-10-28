package org.example.charm.common.exception;

public class InsufficientVolumeException extends RuntimeException {
    public InsufficientVolumeException(String message) {
        super(message);
    }
}