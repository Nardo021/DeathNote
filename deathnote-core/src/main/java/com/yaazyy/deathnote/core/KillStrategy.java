package com.yaazyy.deathnote.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yaazyy.deathnote.api.KillRequest;

/**
 * The ordered set of ways a target can be killed.
 *
 * <p>The execution priority is fixed: prefer the highest-authority command
 * path first, then degrade to native calls.</p>
 *
 * <ol>
 *   <li>{@link #COMMAND_MINECRAFT_KILL} - {@code minecraft:kill <name>}</li>
 *   <li>{@link #COMMAND_KILL} - {@code kill <name>}</li>
 *   <li>{@link #NATIVE_PLATFORM_KILL} - platform damage call</li>
 *   <li>{@link #SET_HEALTH_ZERO} - set health to 0</li>
 * </ol>
 *
 * <p>The enum declaration order IS the priority order; {@link #ordered()}
 * relies on {@link #ordinal()}.</p>
 */
public enum KillStrategy {

    COMMAND_MINECRAFT_KILL,
    COMMAND_KILL,
    NATIVE_PLATFORM_KILL,
    SET_HEALTH_ZERO;

    /** The full priority-ordered list of strategies. */
    public static List<KillStrategy> ordered() {
        List<KillStrategy> list = new ArrayList<>();
        Collections.addAll(list, values());
        return list;
    }

    /**
     * Build the priority-ordered list of strategies that are actually enabled
     * for the given request. The two command strategies are always present;
     * {@code minecraft:kill} only takes precedence when the request prefers it.
     * The native and set-health strategies are toggled by the request.
     */
    public static List<KillStrategy> enabledFor(KillRequest request) {
        List<KillStrategy> list = new ArrayList<>();
        if (request.preferMinecraftNamespaceCommand()) {
            list.add(COMMAND_MINECRAFT_KILL);
            list.add(COMMAND_KILL);
        } else {
            list.add(COMMAND_KILL);
            list.add(COMMAND_MINECRAFT_KILL);
        }
        if (request.allowFallbackNativeKill()) {
            list.add(NATIVE_PLATFORM_KILL);
        }
        if (request.allowFallbackSetHealth()) {
            list.add(SET_HEALTH_ZERO);
        }
        return list;
    }
}
