package com.yaazyy.deathnote.bukkit.common.platform;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.api.DeathNotePlatform;
import com.yaazyy.deathnote.api.KillExecutionResult;
import com.yaazyy.deathnote.api.KillRequest;
import com.yaazyy.deathnote.core.KillStrategy;

/**
 * Bukkit implementation of {@link DeathNotePlatform}, shared by the legacy and
 * modern artifacts.
 *
 * <p>The kill execution honours the fixed strategy priority and ALWAYS uses
 * the validated, sanitized {@link KillRequest#targetName()} - never raw user
 * input and never selectors.</p>
 */
public final class BukkitPlatform implements DeathNotePlatform {

    private final Plugin plugin;

    public BukkitPlatform(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Optional<DNPlayer> findOnlinePlayerByName(String name) {
        Player p = Bukkit.getPlayerExact(name);
        if (p == null) {
            return Optional.empty();
        }
        return Optional.of(new BukkitDNPlayer(p));
    }

    @Override
    public boolean hasPermission(DNPlayer player, String permission) {
        Permissible permissible = resolveOnline(player);
        return permissible != null && permissible.hasPermission(permission);
    }

    @Override
    public void sendMessage(DNPlayer player, String message) {
        Player p = resolveOnline(player);
        if (p != null) {
            p.sendMessage(message);
        }
    }

    @Override
    public void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    @Override
    public KillExecutionResult executeKill(DNPlayer actor, DNPlayer target, KillRequest request) {
        // This method must run on the main thread; callers schedule it there.
        final String name = request.targetName();
        final ConsoleCommandSender console = Bukkit.getConsoleSender();

        KillExecutionResult last = KillExecutionResult.failure("no strategy attempted");
        List<KillStrategy> strategies = KillStrategy.enabledFor(request);

        for (KillStrategy strategy : strategies) {
            try {
                boolean killed = attempt(strategy, console, name, request.nativeDamageAmount());
                if (killed) {
                    return KillExecutionResult.success(strategy.name());
                }
                last = KillExecutionResult.failure("strategy " + strategy + " did not kill target");
            } catch (Throwable t) {
                // A strategy may be unsupported on this server version; try the next.
                plugin.getLogger().log(Level.FINE,
                        "Kill strategy " + strategy + " failed, trying next", t);
                last = KillExecutionResult.failure("strategy " + strategy + " threw: " + t.getMessage());
            }
        }
        return last;
    }

    private boolean attempt(KillStrategy strategy, ConsoleCommandSender console,
                            String name, double nativeDamage) {
        // Success detection is intentionally version-safe: command strategies
        // rely on dispatchCommand's "was handled" return value (false for an
        // unknown command on older servers), and native strategies succeed if
        // the call completes without throwing. We deliberately avoid
        // getHealth()/getOnlinePlayers() whose signatures differ across
        // 1.7 <-> 1.8 to keep the shared bytecode portable.
        switch (strategy) {
            case COMMAND_MINECRAFT_KILL:
                return Bukkit.dispatchCommand(console, "minecraft:kill " + name);
            case COMMAND_KILL:
                return Bukkit.dispatchCommand(console, "kill " + name);
            case NATIVE_PLATFORM_KILL: {
                Player p = Bukkit.getPlayerExact(name);
                if (p == null) {
                    return false;
                }
                p.damage(nativeDamage);
                return true;
            }
            case SET_HEALTH_ZERO: {
                Player p = Bukkit.getPlayerExact(name);
                if (p == null) {
                    return false;
                }
                p.setHealth(0.0D);
                return true;
            }
            default:
                throw new IllegalStateException("Unhandled kill strategy: " + strategy);
        }
    }

    @Override
    public void runSync(Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    @Override
    public void runLater(Runnable task, long ticks) {
        Bukkit.getScheduler().runTaskLater(plugin, task, Math.max(0L, ticks));
    }

    @Override
    public String platformName() {
        return Bukkit.getName();
    }

    @Override
    public String platformVersion() {
        return Bukkit.getVersion();
    }

    private Player resolveOnline(DNPlayer player) {
        if (player instanceof BukkitDNPlayer) {
            return ((BukkitDNPlayer) player).bukkitPlayer();
        }
        OfflinePlayer offline = Bukkit.getOfflinePlayer(player.uuid());
        return offline.getPlayer();
    }
}
