/*
 * jSS7 :: ss7-config
 */
package org.restcomm.protocols.ss7.config;

/**
 * Thrown when a config document cannot be read, parsed, validated, or compiled
 * into a jSS7 stack. The message always names the offending element.
 */
public class Ss7ConfigException extends RuntimeException {

    public Ss7ConfigException(String message) {
        super(message);
    }

    public Ss7ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
