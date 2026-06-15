package com.yaazyy.deathnote.forge1710;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.server.MinecraftServer;

/**
 * Queues work onto the server main thread via the server tick loop (1.7.10 has no
 * {@code addScheduledTask} on {@link MinecraftServer}).
 */
public final class ServerTasks {

    private static final ConcurrentLinkedQueue<Runnable> IMMEDIATE = new ConcurrentLinkedQueue<Runnable>();
    private static final List<DelayedTask> DELAYED = new ArrayList<DelayedTask>();

    public ServerTasks() {
    }

    public static void runSync(MinecraftServer server, Runnable task) {
        if (server == null) {
            task.run();
            return;
        }
        IMMEDIATE.offer(task);
    }

    public static void runLater(MinecraftServer server, Runnable task, long ticks) {
        if (server == null) {
            return;
        }
        if (ticks <= 0L) {
            runSync(server, task);
            return;
        }
        synchronized (DELAYED) {
            DELAYED.add(new DelayedTask(task, ticks));
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Runnable task;
        while ((task = IMMEDIATE.poll()) != null) {
            task.run();
        }
        synchronized (DELAYED) {
            Iterator<DelayedTask> iterator = DELAYED.iterator();
            while (iterator.hasNext()) {
                DelayedTask delayed = iterator.next();
                delayed.ticksRemaining--;
                if (delayed.ticksRemaining <= 0L) {
                    iterator.remove();
                    delayed.task.run();
                }
            }
        }
    }

    private static final class DelayedTask {
        private final Runnable task;
        private long ticksRemaining;

        private DelayedTask(Runnable task, long ticks) {
            this.task = task;
            this.ticksRemaining = ticks;
        }
    }
}
