package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.HashMap;
import java.util.Map;

public class RedstoneLimiterListener implements Listener {

    private final MineMizer plugin;
    private final Map<Chunk, Integer> redstoneActivity = new HashMap<>();
    private long lastResetTime = System.currentTimeMillis();

    public RedstoneLimiterListener(MineMizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRedstoneUpdate(BlockRedstoneEvent event) {
        long now = System.currentTimeMillis();

        // Her 1 saniyede (1000ms) bir sayaçları sıfırla
        if (now - lastResetTime > 1000) {
            redstoneActivity.clear();
            lastResetTime = now;
        }

        Chunk chunk = event.getBlock().getChunk();
        int maxUpdates = plugin.getConfig().getInt("redstone-limiter.max-updates-per-second", 30);
        int currentUpdates = redstoneActivity.getOrDefault(chunk, 0) + 1;

        redstoneActivity.put(chunk, currentUpdates);

        // Belirlenen saniyelik limit aşıldıysa redstone sinyal değişimini engelle
        if (currentUpdates > maxUpdates) {
            event.setNewCurrent(event.getOldCurrent());
        }
    }
}