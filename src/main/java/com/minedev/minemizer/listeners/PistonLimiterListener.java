package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.util.HashMap;
import java.util.Map;

public class PistonLimiterListener implements Listener {

    private final MineMizer plugin;
    private final Map<Chunk, Integer> pistonCounts = new HashMap<>();

    public PistonLimiterListener(MineMizer plugin) {
        this.plugin = plugin;

        // Her saniye sayaçları sıfırla
        plugin.getServer().getScheduler().runTaskTimer(plugin, pistonCounts::clear, 20L, 20L);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!plugin.getConfig().getBoolean("piston-limiter.enabled", true)) return;

        Chunk chunk = event.getBlock().getChunk();
        int maxPushes = plugin.getConfig().getInt("piston-limiter.max-pushes-per-second", 20);

        int current = pistonCounts.getOrDefault(chunk, 0) + 1;
        pistonCounts.put(chunk, current);

        if (current > maxPushes) {
            event.setCancelled(true);
        }
    }
}