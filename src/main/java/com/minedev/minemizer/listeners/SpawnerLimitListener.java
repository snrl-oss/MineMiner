package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class SpawnerLimitListener implements Listener {

    private final MineMizer plugin;

    public SpawnerLimitListener(MineMizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawnerSpawn(CreatureSpawnEvent event) {
        if (!plugin.getConfig().getBoolean("spawner-limiter.enabled", true)) return;

        // Sadece spawner'dan doğan mobları yakala
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            Chunk chunk = event.getLocation().getChunk();
            int maxSpawnerMobs = plugin.getConfig().getInt("spawner-limiter.max-mobs-per-chunk", 8);

            long spawnerMobCount = 0;
            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                    spawnerMobCount++;
                }
            }

            if (spawnerMobCount >= maxSpawnerMobs) {
                event.setCancelled(true);
            }
        }
    }
}