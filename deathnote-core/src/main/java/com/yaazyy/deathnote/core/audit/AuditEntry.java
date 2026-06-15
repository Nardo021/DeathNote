package com.yaazyy.deathnote.core.audit;

import java.util.UUID;

import com.yaazyy.deathnote.core.DeathNoteResult;
import com.yaazyy.deathnote.core.KillStrategy;

/**
 * Immutable record of a single Death Note usage attempt.
 */
public final class AuditEntry {

    private final long timestampMillis;
    private final String actorName;
    private final UUID actorUuid;
    private final String targetName;
    private final KillStrategy selectedStrategy;
    private final DeathNoteResult result;
    private final String platform;
    private final String serverVersion;

    private AuditEntry(Builder b) {
        this.timestampMillis = b.timestampMillis;
        this.actorName = b.actorName;
        this.actorUuid = b.actorUuid;
        this.targetName = b.targetName;
        this.selectedStrategy = b.selectedStrategy;
        this.result = b.result;
        this.platform = b.platform;
        this.serverVersion = b.serverVersion;
    }

    public long timestampMillis() { return timestampMillis; }
    public String actorName() { return actorName; }
    public UUID actorUuid() { return actorUuid; }
    public String targetName() { return targetName; }
    public KillStrategy selectedStrategy() { return selectedStrategy; }
    public DeathNoteResult result() { return result; }
    public String platform() { return platform; }
    public String serverVersion() { return serverVersion; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private long timestampMillis = System.currentTimeMillis();
        private String actorName = "?";
        private UUID actorUuid;
        private String targetName = "?";
        private KillStrategy selectedStrategy;
        private DeathNoteResult result;
        private String platform = "unknown";
        private String serverVersion = "unknown";

        public Builder timestampMillis(long v) { this.timestampMillis = v; return this; }
        public Builder actorName(String v) { this.actorName = v; return this; }
        public Builder actorUuid(UUID v) { this.actorUuid = v; return this; }
        public Builder targetName(String v) { this.targetName = v; return this; }
        public Builder selectedStrategy(KillStrategy v) { this.selectedStrategy = v; return this; }
        public Builder result(DeathNoteResult v) { this.result = v; return this; }
        public Builder platform(String v) { this.platform = v; return this; }
        public Builder serverVersion(String v) { this.serverVersion = v; return this; }

        public AuditEntry build() {
            return new AuditEntry(this);
        }
    }
}
