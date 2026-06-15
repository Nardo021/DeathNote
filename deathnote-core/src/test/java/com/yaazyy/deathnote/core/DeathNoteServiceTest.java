package com.yaazyy.deathnote.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.yaazyy.deathnote.core.audit.AuditLog;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;
import com.yaazyy.deathnote.core.support.FakePlatform;
import com.yaazyy.deathnote.core.support.FakePlayer;

class DeathNoteServiceTest {

    private final List<String> auditLines = new ArrayList<>();
    private final AuditLog auditLog = new AuditLog(auditLines::add);

    private DeathNoteService newService(DeathNoteConfig config, FakePlatform platform) {
        return new DeathNoteService(config, platform, new CooldownService(() -> 0L), auditLog);
    }

    @Test
    void rejectsWithoutUsePermissionAndAlwaysAudits() {
        FakePlatform platform = new FakePlatform();
        DeathNoteConfig cfg = DeathNoteConfig.defaults();
        DeathNoteService svc = newService(cfg, platform);
        FakePlayer actor = new FakePlayer("Light");

        DeathNoteResult result = svc.process(actor, "Notch");
        assertEquals(DeathNoteResult.NO_PERMISSION, result);
        // Every attempt, even a rejected one, writes exactly one audit line.
        assertEquals(1, auditLines.size());
    }

    @Test
    void rejectsInvalidName() {
        FakePlatform platform = new FakePlatform();
        DeathNoteConfig cfg = DeathNoteConfig.defaults();
        DeathNoteService svc = newService(cfg, platform);
        FakePlayer actor = new FakePlayer("Light");
        platform.grant(actor, cfg.permissions().use);

        assertEquals(DeathNoteResult.INVALID_NAME, svc.process(actor, "@e"));
        assertEquals(DeathNoteResult.INVALID_NAME, svc.process(actor, "minecraft:kill @a"));
        assertEquals(DeathNoteResult.INVALID_NAME, svc.process(actor, "ab"));
    }

    @Test
    void targetNotFound() {
        FakePlatform platform = new FakePlatform();
        DeathNoteConfig cfg = DeathNoteConfig.defaults();
        DeathNoteService svc = newService(cfg, platform);
        FakePlayer actor = new FakePlayer("Light");
        platform.grant(actor, cfg.permissions().use);

        assertEquals(DeathNoteResult.TARGET_NOT_FOUND, svc.process(actor, "Notch"));
    }

    @Test
    void selfTargetBlocked() {
        FakePlatform platform = new FakePlatform();
        DeathNoteConfig cfg = DeathNoteConfig.defaults();
        DeathNoteService svc = newService(cfg, platform);
        FakePlayer actor = new FakePlayer("Light");
        platform.grant(actor, cfg.permissions().use);
        platform.addOnline(actor);

        assertEquals(DeathNoteResult.SELF_TARGET_BLOCKED, svc.process(actor, "Light"));
    }

    @Test
    void opTargetImmune() {
        FakePlatform platform = new FakePlatform();
        DeathNoteConfig cfg = DeathNoteConfig.defaults();
        DeathNoteService svc = newService(cfg, platform);
        FakePlayer actor = new FakePlayer("Light");
        FakePlayer target = new FakePlayer("Notch").op(true);
        platform.grant(actor, cfg.permissions().use);
        platform.addOnline(target);

        assertEquals(DeathNoteResult.TARGET_IMMUNE, svc.process(actor, "Notch"));
    }

    @Test
    void bypassPermissionImmune() {
        FakePlatform platform = new FakePlatform();
        DeathNoteConfig cfg = DeathNoteConfig.defaults();
        DeathNoteService svc = newService(cfg, platform);
        FakePlayer actor = new FakePlayer("Light");
        FakePlayer target = new FakePlayer("Notch");
        platform.grant(actor, cfg.permissions().use);
        platform.grant(target, cfg.permissions().bypass);
        platform.addOnline(target);

        assertEquals(DeathNoteResult.TARGET_IMMUNE, svc.process(actor, "Notch"));
    }

    @Test
    void cooldownActiveOnSecondUse() {
        FakePlatform platform = new FakePlatform();
        DeathNoteConfig cfg = DeathNoteConfig.defaults();
        DeathNoteService svc = newService(cfg, platform);
        FakePlayer actor = new FakePlayer("Light");
        FakePlayer target = new FakePlayer("Steve");
        platform.grant(actor, cfg.permissions().use);
        platform.addOnline(target);

        assertEquals(DeathNoteResult.SUCCESS, svc.process(actor, "Steve"));
        assertEquals(DeathNoteResult.COOLDOWN_ACTIVE, svc.process(actor, "Steve"));
    }

    @Test
    void successPassesValidatedNameToPlatform() {
        FakePlatform platform = new FakePlatform();
        DeathNoteConfig cfg = DeathNoteConfig.defaults();
        DeathNoteService svc = newService(cfg, platform);
        FakePlayer actor = new FakePlayer("Light");
        FakePlayer target = new FakePlayer("Steve");
        platform.grant(actor, cfg.permissions().use);
        platform.addOnline(target);

        assertEquals(DeathNoteResult.SUCCESS, svc.process(actor, "  Steve  "));
        assertNotNull(platform.lastKillRequest);
        assertEquals("Steve", platform.lastKillRequest.targetName());
    }

    @Test
    void killFailureReported() {
        FakePlatform platform = new FakePlatform().killSucceeds(false);
        DeathNoteConfig cfg = DeathNoteConfig.defaults();
        DeathNoteService svc = newService(cfg, platform);
        FakePlayer actor = new FakePlayer("Light");
        FakePlayer target = new FakePlayer("Steve");
        platform.grant(actor, cfg.permissions().use);
        platform.addOnline(target);

        assertEquals(DeathNoteResult.KILL_FAILED, svc.process(actor, "Steve"));
    }
}
