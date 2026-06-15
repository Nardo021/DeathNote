package com.yaazyy.deathnote.forge1201.audit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import com.yaazyy.deathnote.core.audit.AuditSink;

/** Append-only audit log under the mod config directory. */
public final class ForgeFileAuditSink implements AuditSink {

    private final File logFile;
    private final Logger logger;

    public ForgeFileAuditSink(File configDir, Logger logger) {
        this.logFile = new File(configDir, "deathnote-audit.log");
        this.logger = logger;
    }

    @Override
    public synchronized void write(String line) {
        logger.info(line);
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println(line);
        } catch (IOException e) {
            logger.warning("Failed to write Death Note audit log: " + e.getMessage());
        }
    }
}
