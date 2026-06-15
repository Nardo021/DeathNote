package com.yaazyy.deathnote.forge1710;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.api.DeathNotePlatform;
import com.yaazyy.deathnote.api.KillExecutionResult;
import com.yaazyy.deathnote.api.KillRequest;
import com.yaazyy.deathnote.core.KillStrategy;
import com.yaazyy.deathnote.core.config.DeathNoteConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;

/**
 * Forge 1.7.10 {@link DeathNotePlatform} — kill strategies honour core ordering.
 */
public final class Forge1710DeathNotePlatform implements DeathNotePlatform {

    private final DeathNoteConfig config;

    public Forge1710DeathNotePlatform(DeathNoteConfig config) {
        this.config = config;
    }

    @Override
    public Optional<DNPlayer> findOnlinePlayerByName(String name) {
        MinecraftServer server = server();
        if (server == null) {
            return Optional.empty();
        }
        EntityPlayerMP player = server.getConfigurationManager().func_152612_a(name);
        return player == null ? Optional.empty() : Optional.of(new Forge1710DNPlayer(player));
    }

    @Override
    public boolean hasPermission(DNPlayer player, String permission) {
        if (permission.equals(config.permissions().use)) {
            return true;
        }
        EntityPlayerMP entity = asServer(player);
        if (entity == null) {
            return false;
        }
        if (permission.equals(config.permissions().give) || permission.equals(config.permissions().admin)) {
            return entity.canCommandSenderUseCommand(4, "");
        }
        if (permission.equals(config.permissions().bypass)) {
            return entity.canCommandSenderUseCommand(4, "");
        }
        return false;
    }

    @Override
    public void sendMessage(DNPlayer player, String message) {
        EntityPlayerMP entity = asServer(player);
        if (entity != null) {
            entity.addChatMessage(new ChatComponentText(message));
        }
    }

    @Override
    public void broadcast(String message) {
        MinecraftServer server = server();
        if (server != null) {
            server.getConfigurationManager().sendChatMsg(new ChatComponentText(message));
        }
    }

    @Override
    public KillExecutionResult executeKill(DNPlayer actor, DNPlayer target, KillRequest request) {
        MinecraftServer server = server();
        if (server == null) {
            return KillExecutionResult.failure("no server");
        }
        String name = request.targetName();
        ICommandSender source = server;
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

    private boolean attempt(MinecraftServer server, ICommandSender source,
                            KillStrategy strategy, String name, double nativeDamage) {
        switch (strategy) {
            case COMMAND_MINECRAFT_KILL:
                server.getCommandManager().executeCommand(source, "minecraft:kill " + name);
                return isDeadOrGone(server, name);
            case COMMAND_KILL:
                server.getCommandManager().executeCommand(source, "kill " + name);
                return isDeadOrGone(server, name);
            case NATIVE_PLATFORM_KILL: {
                EntityPlayerMP player = server.getConfigurationManager().func_152612_a(name);
                if (player == null) {
                    return false;
                }
                player.attackEntityFrom(DamageSource.outOfWorld, (float) nativeDamage);
                return isDeadOrGone(server, name);
            }
            case SET_HEALTH_ZERO: {
                EntityPlayerMP player = server.getConfigurationManager().func_152612_a(name);
                if (player == null) {
                    return false;
                }
                player.setHealth(0.0F);
                return isDeadOrGone(server, name);
            }
            default:
                throw new IllegalStateException("Unhandled strategy: " + strategy);
        }
    }

    private boolean isDeadOrGone(MinecraftServer server, String name) {
        EntityPlayerMP player = server.getConfigurationManager().func_152612_a(name);
        return player == null || player.isDead || player.getHealth() <= 0.0F;
    }

    @Override
    public void runSync(Runnable task) {
        ServerTasks.runSync(server(), task);
    }

    @Override
    public void runLater(Runnable task, long ticks) {
        ServerTasks.runLater(server(), task, ticks);
    }

    @Override
    public String platformName() {
        return "Forge-1.7.10";
    }

    @Override
    public String platformVersion() {
        MinecraftServer server = server();
        return server == null ? "1.7.10" : server.getMinecraftVersion();
    }

    private EntityPlayerMP asServer(DNPlayer player) {
        if (player instanceof Forge1710DNPlayer) {
            return ((Forge1710DNPlayer) player).serverPlayer();
        }
        return null;
    }

    private MinecraftServer server() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }
}
