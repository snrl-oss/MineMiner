package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionLimiterListener implements Listener {

    private final MineMizer plugin;

    public ExplosionLimiterListener(MineMizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (!plugin.getConfig().getBoolean("explosion-limiter.disable-block-damage-on-lag", false)) return;

        // Sunucu TPS'i 16'nın altına düşerse patlamaların blok kırmasını engelle (Sadece hasar versin)
        if (plugin.getServer().getTPS()[0] < 16.0) {
            event.blockList().clear();
        }
    }
}