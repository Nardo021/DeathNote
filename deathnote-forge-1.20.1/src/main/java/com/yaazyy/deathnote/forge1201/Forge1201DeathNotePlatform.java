package com.yaazyy.deathnote.forge1201;

import java.util.List;
import java.util.Optional;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.api.DeathNotePlatform;
import com.yaazyy.deathnote.api.KillExecutionResult;
import com.yaazyy.deathnote.api.KillRequest;
import com.yaazyy.deathnote.core.KillStrategy;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Forge 1.20.1 {@link DeathNotePlatform} — kill strategies honour core ordering.
 */
public final class Forge1201DeathNotePlatform implements DeathNotePlatform {

    private final DeathNoteConfig config;

    public Forge1201DeathNotePlatform(DeathNoteConfig config) {
        this.config = config;
    }

    @Override
    public Optional<DNPlayer> findOnlinePlayerByName(String name) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return Optional.empty();
        }
        ServerPlayer player = server.getPlayerList().getPlayerByName(name);
        return player == null ? Optional.empty() : Optional.of(new Forge1201DNPlayer(player));
    }

    @Override
    public boolean hasPermission(DNPlayer player, String permission) {
        if (permission.equals(config.permissions().use)) {
            return true;
        }
        ServerPlayer sp = asServer(player);
        if (sp == null) {
            return false;
        }
        if (permission.equals(config.permissions().give) || permission.equals(config.permissions().admin)) {
            return sp.hasPermissions(4);
        }
        if (permission.equals(config.permissions().bypass)) {
            return sp.hasPermissions(4);
        }
        return false;
    }

    @Override
    public void sendMessage(DNPlayer player, String message) {
        ServerPlayer sp = asServer(player);
        if (sp != null) {
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal(message));
        }
    }

    @Override
    public void broadcast(String message) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.getPlayerList().broadcastSystemMessage(
                    net.minecraft.network.chat.Component.literal(message), false);
        }
    }

    @Override
    public KillExecutionResult executeKill(DNPlayer actor, DNPlayer target, KillRequest request) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return KillExecutionResult.failure("no server");
        }
        String name = request.targetName();
        CommandSourceStack source = server.createCommandSourceStack();
        List<KillStrategy> strategies = KillStrategy.enabledFor(request);
        KillExecutionResult last = KillExecutionResult.failure("no strategy attempted");

        for (KillStrategy strategy : strategies) {
            try {
                if (attempt(server, source, strategy, name, request.nativeDamageAmount())) {
                    return KillExecutionResult.success(strategy.name());
                }
                last = KillExecutionResult.failure("strategy " + strategy + " did not kill");
            } catch (Throwable t) {
                last = KillExecutionResult.failure("strategy " + strategy + ": " + t.getMessage());
            }
        }
        return last;
    }

    private boolean attempt(MinecraftServer server, CommandSourceStack source,
                            KillStrategy strategy, String name, double nativeDamage) {
        switch (strategy) {
            case COMMAND_MINECRAFT_KILL:
                server.getCommands().performPrefixedCommand(source, "minecraft:kill " + name);
                return isDeadOrGone(server, name);
            case COMMAND_KILL:
                server.getCommands().performPrefixedCommand(source, "kill " + name);
                return isDeadOrGone(server, name);
            case NATIVE_PLATFORM_KILL: {
                ServerPlayer p = server.getPlayerList().getPlayerByName(name);
                if (p == null) {
                    return false;
                }
                DamageSource ds = server.overworld().damageSources().fellOutOfWorld();
                p.hurt(ds, (float) nativeDamage);
                return isDeadOrGone(server, name);
            }
            case SET_HEALTH_ZERO: {
                ServerPlayer p = server.getPlayerList().getPlayerByName(name);
                if (p == null) {
                    return false;
                }
                p.setHealth(0.0F);
                return isDeadOrGone(server, name);
            }
            default:
                throw new IllegalStateException("Unhandled strategy: " + strategy);
        }
    }

    private boolean isDeadOrGone(MinecraftServer server, String name) {
        ServerPlayer p = server.getPlayerList().getPlayerByName(name);
        return p == null || p.isDeadOrDying() || p.getHealth() <= 0.0F;
    }

    @Override
    public void runSync(Runnable task) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.execute(task);
        } else {
            task.run();
        }
    }

    @Override
    public void runLater(Runnable task, long ticks) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        if (ticks <= 0L) {
            server.execute(task);
            return;
        }
        final long[] remaining = {ticks};
        Runnable tickTask = new Runnable() {
            @Override
            public void run() {
                remaining[0]--;
                if (remaining[0] <= 0L) {
                    task.run();
                } else {
                    server.execute(this);
                }
            }
        };
        server.execute(tickTask);
    }

    @Override
    public String platformName() {
        return "Forge";
    }

    @Override
    public String platformVersion() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server == null ? "1.20.1" : server.getServerVersion();
    }

    private ServerPlayer asServer(DNPlayer player) {
        if (player instanceof Forge1201DNPlayer) {
            return ((Forge1201DNPlayer) player).serverPlayer();
        }
        return null;
    }
}
