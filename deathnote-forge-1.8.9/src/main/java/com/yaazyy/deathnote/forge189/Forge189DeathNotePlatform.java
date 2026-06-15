package com.yaazyy.deathnote.forge189;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.api.DeathNotePlatform;
import com.yaazyy.deathnote.api.KillExecutionResult;
import com.yaazyy.deathnote.api.KillRequest;
import com.yaazyy.deathnote.core.KillStrategy;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;

/**
 * Forge 1.8.9 {@link DeathNotePlatform} — kill strategies honour core ordering.
 */
public final class Forge189DeathNotePlatform implements DeathNotePlatform {

    private final DeathNoteConfig config;

    public Forge189DeathNotePlatform(DeathNoteConfig config) {
        this.config = config;
    }

    @Override
    public Optional<DNPlayer> findOnlinePlayerByName(String name) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return Optional.empty();
        }
        EntityPlayerMP player = server.getConfigurationManager().getPlayerByUsername(name);
        return player == null ? Optional.empty() : Optional.of(new Forge189DNPlayer(player));
    }

    @Override
    public boolean hasPermission(DNPlayer player, String permission) {
        if (permission.equals(config.permissions().use)) {
            return true;
        }
        EntityPlayerMP sp = asServer(player);
        if (sp == null) {
            return false;
        }
        if (permission.equals(config.permissions().give) || permission.equals(config.permissions().admin)) {
            return sp.canCommandSenderUseCommand(4, "give");
        }
        if (permission.equals(config.permissions().bypass)) {
            return sp.canCommandSenderUseCommand(4, "deathnote");
        }
        return false;
    }

    @Override
    public void sendMessage(DNPlayer player, String message) {
        EntityPlayerMP sp = asServer(player);
        if (sp != null) {
            sp.addChatMessage(new ChatComponentText(message));
        }
    }

    @Override
    public void broadcast(String message) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            server.getConfigurationManager().sendChatMsg(new ChatComponentText(message));
        }
    }

    @Override
    public KillExecutionResult executeKill(DNPlayer actor, DNPlayer target, KillRequest request) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return KillExecutionResult.failure("no server");
        }
        String name = request.targetName();
        List<KillStrategy> strategies = KillStrategy.enabledFor(request);
        KillExecutionResult last = KillExecutionResult.failure("no strategy attempted");

        for (KillStrategy strategy : strategies) {
            try {
                if (attempt(server, strategy, name, request.nativeDamageAmount())) {
                    return KillExecutionResult.success(strategy.name());
                }
                last = KillExecutionResult.failure("strategy " + strategy + " did not kill");
            } catch (Throwable t) {
                last = KillExecutionResult.failure("strategy " + strategy + ": " + t.getMessage());
            }
        }
        return last;
    }

    private boolean attempt(MinecraftServer server, KillStrategy strategy, String name, double nativeDamage) {
        switch (strategy) {
            case COMMAND_MINECRAFT_KILL:
                server.getCommandManager().executeCommand(server, "minecraft:kill " + name);
                return isDeadOrGone(server, name);
            case COMMAND_KILL:
                server.getCommandManager().executeCommand(server, "kill " + name);
                return isDeadOrGone(server, name);
            case NATIVE_PLATFORM_KILL: {
                EntityPlayerMP p = server.getConfigurationManager().getPlayerByUsername(name);
                if (p == null) {
                    return false;
                }
                p.attackEntityFrom(DamageSource.outOfWorld, (float) nativeDamage);
                return isDeadOrGone(server, name);
            }
            case SET_HEALTH_ZERO: {
                EntityPlayerMP p = server.getConfigurationManager().getPlayerByUsername(name);
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
        EntityPlayerMP p = server.getConfigurationManager().getPlayerByUsername(name);
        return p == null || !p.isEntityAlive() || p.getHealth() <= 0.0F;
    }

    @Override
    public void runSync(Runnable task) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            server.addScheduledTask(task);
        } else {
            task.run();
        }
    }

    @Override
    public void runLater(Runnable task, long ticks) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return;
        }
        if (ticks <= 0L) {
            server.addScheduledTask(task);
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
                    server.addScheduledTask(this);
                }
            }
        };
        server.addScheduledTask(tickTask);
    }

    @Override
    public String platformName() {
        return "Forge";
    }

    @Override
    public String platformVersion() {
        MinecraftServer server = MinecraftServer.getServer();
        return server == null ? "1.8.9" : server.getMinecraftVersion();
    }

    private EntityPlayerMP asServer(DNPlayer player) {
        if (player instanceof Forge189DNPlayer) {
            return ((Forge189DNPlayer) player).serverPlayer();
        }
        return null;
    }
}
