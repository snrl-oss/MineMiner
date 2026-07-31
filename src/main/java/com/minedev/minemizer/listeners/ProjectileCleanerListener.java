package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileCleanerListener implements Listener {

    private final MineMizer plugin;

    public ProjectileCleanerListener(MineMizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (plugin.getConfig().getBoolean("clean-projectiles.enabled", true)) {
            if (event.getEntity() instanceof Arrow || event.getEntity() instanceof SpectralArrow) {
                // Saplandığı an sil
                event.getEntity().remove();
            }
        }
    }
}