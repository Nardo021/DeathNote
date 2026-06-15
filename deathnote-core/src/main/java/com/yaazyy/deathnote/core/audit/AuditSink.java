package com.yaazyy.deathnote.core.audit;

/**
 * Where audit lines are written. The core stays platform-agnostic; adapters
 * provide a sink that writes to a file, the server console, or both.
 */
@FunctionalInterface
public interface AuditSink {

    void write(String line);
}
