package com.yaazyy.deathnote.core;

import java.util.Optional;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.api.DeathNotePlatform;
import com.yaazyy.deathnote.api.KillExecutionResult;
import com.yaazyy.deathnote.api.KillRequest;
import com.yaazyy.deathnote.core.audit.AuditEntry;
import com.yaazyy.deathnote.core.audit.AuditLog;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;

/**
 * The heart of the project: turns a submitted book page into a validated,
 * permission-checked, cooldown-gated, audited kill request.
 *
 * <p>This class is platform-agnostic. It talks to the world only through
 * {@link DeathNotePlatform} and is therefore fully unit-testable with a fake
 * platform.</p>
 */
public final class DeathNoteService {

    private final DeathNoteConfig config;
    private final DeathNotePlatform platform;
    private final CooldownService cooldowns;
    private final AuditLog auditLog;

    public DeathNoteService(DeathNoteConfig config,
                            DeathNotePlatform platform,
                            CooldownService cooldowns,
                            AuditLog auditLog) {
        this.config = config;
        this.platform = platform;
        this.cooldowns = cooldowns;
        this.auditLog = auditLog;
    }

    /**
     * Process a Death Note submission.
     *
     * <p>The caller (a platform listener) is responsible for ensuring the
     * submitting item is a genuine, signature-verified Death Note before
     * calling this. Item authenticity is a platform concern, not a core one,
     * so it is not re-checked here.</p>
     *
     * @param actor             the player who clicked "Done"
     * @param firstPageFirstLine the raw first line of the first page
     * @return the structured result; an audit entry is always written
     */
    public DeathNoteResult process(DNPlayer actor, String firstPageFirstLine) {
        if (config == null) {
            return audit(actor, null, null, DeathNoteResult.CONFIG_ERROR);
        }

        // Actor permission to use the Death Note.
        if (!platform.hasPermission(actor, config.permissions().use)) {
            return audit(actor, firstPageFirstLine, null, DeathNoteResult.NO_PERMISSION);
        }

        // Name validation (anti command-injection / anti-selector).
        String targetName = firstPageFirstLine == null ? "" : firstPageFirstLine.trim();
        if (!NameValidator.isValid(targetName)) {
            return audit(actor, targetName, null, DeathNoteResult.INVALID_NAME);
        }

        // Target lookup (online only).
        Optional<DNPlayer> targetOpt = platform.findOnlinePlayerByName(targetName);
        if (!targetOpt.isPresent()) {
            return audit(actor, targetName, null, DeathNoteResult.TARGET_NOT_FOUND);
        }
        DNPlayer target = targetOpt.get();

        // Self-target protection.
        if (config.security().blockSelfTarget && actor.uuid().equals(target.uuid())) {
            return audit(actor, targetName, null, DeathNoteResult.SELF_TARGET_BLOCKED);
        }

        // Immunity: bypass permission or op-target protection.
        if (platform.hasPermission(target, config.permissions().bypass)
                || (config.security().blockOpTarget && target.isOp())) {
            return audit(actor, targetName, null, DeathNoteResult.TARGET_IMMUNE);
        }

        // Cooldown gate.
        if (config.cooldown().enabled
                && cooldowns.isOnCooldown(actor.uuid(), config.cooldown().seconds)) {
            return audit(actor, targetName, null, DeathNoteResult.COOLDOWN_ACTIVE);
        }

        // Execute the kill via the platform using the validated name only.
        KillRequest request = KillRequest.builder(targetName)
                .preferMinecraftNamespaceCommand(config.kill().preferMinecraftNamespaceCommand)
                .allowFallbackNativeKill(config.kill().allowFallbackNativeKill)
                .allowFallbackSetHealth(config.kill().allowFallbackSetHealth)
                .build();

        KillExecutionResult execResult = platform.executeKill(actor, target, request);
        KillStrategy used = parseStrategy(execResult.strategy());

        if (execResult.isSuccess()) {
            if (config.cooldown().enabled) {
                cooldowns.recordUse(actor.uuid());
            }
            return audit(actor, targetName, used, DeathNoteResult.SUCCESS);
        }
        return audit(actor, targetName, used, DeathNoteResult.KILL_FAILED);
    }

    private static KillStrategy parseStrategy(String name) {
        if (name == null) {
            return null;
        }
        try {
            return KillStrategy.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private DeathNoteResult audit(DNPlayer actor, String targetName, KillStrategy strategy,
                                  DeathNoteResult result) {
        if (auditLog != null) {
            auditLog.record(AuditEntry.builder()
                    .actorName(actor == null ? "?" : actor.name())
                    .actorUuid(actor == null ? null : actor.uuid())
                    .targetName(targetName == null ? "-" : targetName)
                    .selectedStrategy(strategy)
                    .result(result)
                    .platform(platform == null ? "unknown" : platform.platformName())
                    .serverVersion(platform == null ? "unknown" : platform.platformVersion())
                    .build());
        }
        return result;
    }

    public DeathNoteConfig config() {
        return config;
    }
}
