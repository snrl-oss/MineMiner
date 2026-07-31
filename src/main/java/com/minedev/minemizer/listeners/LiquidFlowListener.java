package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.HashMap;
import java.util.Map;

public class LiquidFlowListener implements Listener {

    private final MineMizer plugin;
    private final Map<Chunk, Integer> flowCounts = new HashMap<>();

    public LiquidFlowListener(MineMizer plugin) {
        this.plugin = plugin;
        // Her saniye sıvı akış sayılarını sıfırla
        plugin.getServer().getScheduler().runTaskTimer(plugin, flowCounts::clear, 20L, 20L);
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        if (!plugin.getConfig().getBoolean("liquid-limiter.enabled", true)) return;

        Chunk chunk = event.getBlock().getChunk();
        int maxFlows = plugin.getConfig().getInt("liquid-limiter.max-flows-per-second", 40);

        int current = flowCounts.getOrDefault(chunk, 0) + 1;
        flowCounts.put(chunk, current);

        if (current > maxFlows) {
            event.setCancelled(true);
        }
    }
}