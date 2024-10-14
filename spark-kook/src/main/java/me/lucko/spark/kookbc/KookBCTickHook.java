package me.lucko.spark.kookbc;

import me.lucko.spark.common.tick.AbstractTickHook;
import me.lucko.spark.common.tick.TickHook;
import snw.jkook.plugin.Plugin;
import snw.jkook.scheduler.Task;

public class KookBCTickHook extends AbstractTickHook implements TickHook, Runnable {
    private final Plugin plugin;
    private Task task;

    public KookBCTickHook(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        onTick();
    }

    @Override
    public void start() {
        this.task = this.plugin.getCore().getScheduler().runTaskTimer(this.plugin, this, 1, 1);
    }

    @Override
    public void close() {
        if (task == null) return;
        if (task.isCancelled()) return;
        this.task.cancel();
        this.task = null;
    }

}
