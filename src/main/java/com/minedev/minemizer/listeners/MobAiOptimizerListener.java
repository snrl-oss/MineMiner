package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobAiOptimizerListener implements Listener {

    private final MineMizer plugin;

    public MobAiOptimizerListener(MineMizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (!plugin.getConfig().getBoolean("mob-ai-optimizer.enabled", true)) return;

        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;

        int threshold = plugin.getConfig().getInt("mob-ai-optimizer.disable-ai-threshold", 8);

        int mobCount = 0;
        for (Entity nearby : entity.getChunk().getEntities()) {
            if (nearby instanceof LivingEntity && !(nearby instanceof Player)) {
                mobCount++;
            }
        }

        // Chunk'ta threshold'dan fazla mob varsa yapay zekayı kapat
        if (mobCount >= threshold) {
            entity.setAI(false);
        }
    }
}