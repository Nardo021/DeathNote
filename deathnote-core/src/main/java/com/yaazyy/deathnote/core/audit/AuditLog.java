package com.yaazyy.deathnote.core.audit;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Formats {@link AuditEntry} records into a stable, greppable line and pushes
 * them to a pluggable {@link AuditSink}.
 */
public final class AuditLog {

    private static final DateTimeFormatter ISO =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

    private final AuditSink sink;

    public AuditLog(AuditSink sink) {
        this.sink = sink;
    }

    public void record(AuditEntry entry) {
        sink.write(format(entry));
    }

    /** Render an entry as a single audit line. */
    public static String format(AuditEntry e) {
        return "[DeathNote-AUDIT] "
                + "time=" + ISO.format(Instant.ofEpochMilli(e.timestampMillis()))
                + " actor=" + e.actorName()
                + " actorUuid=" + (e.actorUuid() == null ? "-" : e.actorUuid())
                + " target=" + e.targetName()
                + " strategy=" + (e.selectedStrategy() == null ? "-" : e.selectedStrategy())
                + " result=" + e.result()
                + " platform=" + e.platform()
                + " version=" + e.serverVersion();
    }
}
