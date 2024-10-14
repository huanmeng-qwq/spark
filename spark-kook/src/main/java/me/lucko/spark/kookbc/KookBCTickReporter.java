package me.lucko.spark.kookbc;

import me.lucko.spark.common.tick.AbstractTickReporter;
import me.lucko.spark.kookbc.event.TickEvent;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.plugin.Plugin;

public class KookBCTickReporter extends AbstractTickReporter implements Listener {
    private final Plugin plugin;
    private long start = System.currentTimeMillis();

    public KookBCTickReporter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        plugin.getCore().getEventManager().registerHandlers(plugin, this);
    }

    @EventHandler
    public void tick(TickEvent e) {
        onTick(System.currentTimeMillis() - start);
    }

    @Override
    public void close() {
        plugin.getCore().getEventManager().unregisterHandlers(this);
    }
}
