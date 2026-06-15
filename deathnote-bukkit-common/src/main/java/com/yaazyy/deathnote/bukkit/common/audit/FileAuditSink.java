package com.yaazyy.deathnote.bukkit.common.audit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import com.yaazyy.deathnote.core.audit.AuditSink;

/**
 * Writes audit lines to both the plugin logger and an append-only
 * {@code deathnote-audit.log} file in the plugin data folder.
 */
public final class FileAuditSink implements AuditSink {

    private final File logFile;
    private final Logger logger;

    public FileAuditSink(File dataFolder, Logger logger) {
        this.logFile = new File(dataFolder, "deathnote-audit.log");
        this.logger = logger;
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
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
