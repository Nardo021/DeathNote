package com.yaazyy.deathnote.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.yaazyy.deathnote.api.KillRequest;

class KillStrategyTest {

    @Test
    void fixedPriorityOrder() {
        List<KillStrategy> ordered = KillStrategy.ordered();
        assertEquals(KillStrategy.COMMAND_MINECRAFT_KILL, ordered.get(0));
        assertEquals(KillStrategy.COMMAND_KILL, ordered.get(1));
        assertEquals(KillStrategy.NATIVE_PLATFORM_KILL, ordered.get(2));
        assertEquals(KillStrategy.SET_HEALTH_ZERO, ordered.get(3));
    }

    @Test
    void enabledForRespectsMinecraftNamespacePreference() {
        KillRequest prefer = KillRequest.builder("Notch")
                .preferMinecraftNamespaceCommand(true)
                .build();
        List<KillStrategy> list = KillStrategy.enabledFor(prefer);
        assertEquals(KillStrategy.COMMAND_MINECRAFT_KILL, list.get(0));
        assertEquals(KillStrategy.COMMAND_KILL, list.get(1));

        KillRequest noPrefer = KillRequest.builder("Notch")
                .preferMinecraftNamespaceCommand(false)
                .build();
        List<KillStrategy> list2 = KillStrategy.enabledFor(noPrefer);
        assertEquals(KillStrategy.COMMAND_KILL, list2.get(0));
        assertEquals(KillStrategy.COMMAND_MINECRAFT_KILL, list2.get(1));
    }

    @Test
    void enabledForRespectsFallbackToggles() {
        KillRequest commandsOnly = KillRequest.builder("Notch")
                .allowFallbackNativeKill(false)
                .allowFallbackSetHealth(false)
                .build();
        List<KillStrategy> list = KillStrategy.enabledFor(commandsOnly);
        assertEquals(2, list.size());
        assertFalse(list.contains(KillStrategy.NATIVE_PLATFORM_KILL));
        assertFalse(list.contains(KillStrategy.SET_HEALTH_ZERO));

        KillRequest all = KillRequest.builder("Notch").build();
        List<KillStrategy> full = KillStrategy.enabledFor(all);
        assertEquals(4, full.size());
        assertTrue(full.contains(KillStrategy.NATIVE_PLATFORM_KILL));
        assertTrue(full.contains(KillStrategy.SET_HEALTH_ZERO));
    }
}
