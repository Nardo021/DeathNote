package com.yaazyy.deathnote.forge1165;

import java.util.Optional;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.api.DeathNotePlatform;
import com.yaazyy.deathnote.api.KillExecutionResult;
import com.yaazyy.deathnote.api.KillRequest;
import com.yaazyy.deathnote.core.KillStrategy;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public final class Forge1165DeathNotePlatform implements DeathNotePlatform {

    private final DeathNoteConfig config;

    public Forge1165DeathNotePlatform(DeathNoteConfig config) {
        this.config = config;
    }

    @Override
    public Optional<DNPlayer> findOnlinePlayerByName(String name) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return Optional.empty();
        }
        ServerPlayerEntity player = server.getPlayerList().getPlayerByUsername(name);
        return player == null ? Optional.empty() : Optional.of(new Forge1165DNPlayer(player));
    }

    @Override
    public boolean hasPermission(DNPlayer player, String permission) {
        if (permission.equals(config.permissions().use)) {
            return true;
        }
        ServerPlayerEntity sp = asServer(player);
        if (sp == null) {
            return false;
        }
        if (permission.equals(config.permissions().give) || permission.equals(config.permissions().admin)) {
            return sp.hasPermissionLevel(4);
        }
        if (permission.equals(config.permissions().bypass)) {
            return sp.hasPermissionLevel(4);
        }
        return false;
    }

    @Override
    public void sendMessage(DNPlayer player, String message) {
        ServerPlayerEntity sp = asServer(player);
        if (sp != null) {
            sp.sendMessage(new StringTextComponent(message), sp.getUniqueID());
        }
    }

    @Override
    public void broadcast(String message) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
                player.sendMessage(new StringTextComponent(message), player.getUniqueID());
            }
        }
    }

    @Override
    public KillExecutionResult executeKill(DNPlayer actor, DNPlayer target, KillRequest request) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return KillExecutionResult.failure("no server");
        }
        String name = request.targetName();
        CommandSource source = server.getCommandSource();
        KillExecutionResult last = KillExecutionResult.failure("no strategy attempted");

        for (KillStrategy strategy : KillStrategy.enabledFor(request)) {
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

    private boolean attempt(MinecraftServer server, CommandSource source,
                            KillStrategy strategy, String name, double nativeDamage) {
        switch (strategy) {
            case COMMAND_MINECRAFT_KILL:
                server.getCommandManager().handleCommand(source, "minecraft:kill " + name);
                return isDeadOrGone(server, name);
            case COMMAND_KILL:
                server.getCommandManager().handleCommand(source, "kill " + name);
                return isDeadOrGone(server, name);
            case NATIVE_PLATFORM_KILL: {
                ServerPlayerEntity p = server.getPlayerList().getPlayerByUsername(name);
                if (p == null) {
                    return false;
                }
                p.attackEntityFrom(DamageSource.OUT_OF_WORLD, (float) nativeDamage);
                return isDeadOrGone(server, name);
            }
            case SET_HEALTH_ZERO: {
                ServerPlayerEntity p = server.getPlayerList().getPlayerByUsername(name);
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
        ServerPlayerEntity p = server.getPlayerList().getPlayerByUsername(name);
        return p == null || !p.isAlive() || p.getHealth() <= 0.0F;
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
        return server == null ? "1.16.5" : server.getMinecraftVersion();
    }

    private ServerPlayerEntity asServer(DNPlayer player) {
        if (player instanceof Forge1165DNPlayer) {
            return ((Forge1165DNPlayer) player).serverPlayer();
        }
        return null;
    }
}
