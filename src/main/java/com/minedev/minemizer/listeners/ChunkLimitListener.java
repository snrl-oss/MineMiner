package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class ChunkLimitListener implements Listener {

    private final MineMizer plugin;

    public ChunkLimitListener(MineMizer plugin) {
        this.plugin = plugin;
    }

    // Mob doğma sınırı
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent event) {
        Chunk chunk = event.getLocation().getChunk();
        int maxMobs = plugin.getConfig().getInt("limits.mobs.max-per-chunk", 15);

        long currentMobCount = 0;
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                currentMobCount++;
            }
        }

        if (currentMobCount >= maxMobs) {
            event.setCancelled(true);
        }
    }

    // Eşya Çerçevesi (Item Frame) koyma sınırı
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            Chunk chunk = event.getBlock().getChunk();
            int maxFrames = plugin.getConfig().getInt("limits.item-frames.max-per-chunk", 8);

            long count = 0;
            for (Entity e : chunk.getEntities()) {
                if (e instanceof ItemFrame) count++;
            }

            if (count >= maxFrames) {
                event.setCancelled(true);
                if (event.getPlayer() != null) {
                    event.getPlayer().sendMessage("§c[MineMizer] Bu chunk'taki esya cercevesi sinirina ulasildi!");
                }
            }
        }
    }

    // Armor Stand koyma sınırı
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onArmorStandPlace(EntityPlaceEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            Chunk chunk = event.getEntity().getLocation().getChunk();
            int maxArmorStands = plugin.getConfig().getInt("limits.armor-stands.max-per-chunk", 6);

            long count = 0;
            for (Entity e : chunk.getEntities()) {
                if (e instanceof ArmorStand) count++;
            }

            if (count >= maxArmorStands) {
                event.setCancelled(true);
                if (event.getPlayer() != null) {
                    event.getPlayer().sendMessage("§c[MineMizer] Bu chunk'taki armor stand sinirina ulasildi!");
                }
            }
        }
    }
}