package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class HopperLimitListener implements Listener {

    private final MineMizer plugin;

    public HopperLimitListener(MineMizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.HOPPER) {
            Chunk chunk = event.getBlock().getChunk();
            int maxHoppers = plugin.getConfig().getInt("limits.hoppers.max-per-chunk", 10);

            int count = 0;
            for (BlockState tile : chunk.getTileEntities()) {
                if (tile.getType() == Material.HOPPER) count++;
            }

            if (count >= maxHoppers) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§c[MineMizer] Bu chunk'taki Maksimum Huni (Hopper) sınırına ulaşıldı!");
            }
        }
    }
}