package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

public class SpawnerStackerListener implements Listener {

    private final MineMizer plugin;
    private final NamespacedKey stackKey;

    public SpawnerStackerListener(MineMizer plugin) {
        this.plugin = plugin;
        this.stackKey = new NamespacedKey(plugin, "spawner_stack_count");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        if (!plugin.getConfig().getBoolean("spawner-stacker.enabled", true)) return;

        Block block = event.getBlockPlaced();
        if (block.getType() != Material.SPAWNER) return;

        if (!(block.getState() instanceof CreatureSpawner newSpawner)) return;

        int radius = plugin.getConfig().getInt("spawner-stacker.radius", 3);
        int maxStack = plugin.getConfig().getInt("spawner-stacker.max-stack", 16);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;

                    Block targetBlock = block.getRelative(x, y, z);
                    if (targetBlock.getType() == Material.SPAWNER && targetBlock.getState() instanceof CreatureSpawner existingSpawner) {

                        if (existingSpawner.getSpawnedType() == newSpawner.getSpawnedType()) {
                            int currentStack = getStackCount(existingSpawner);

                            if (currentStack < maxStack) {
                                setStackCount(existingSpawner, currentStack + 1);

                                block.setType(Material.AIR);
                                var msg = LegacyComponentSerializer.legacyAmpersand().deserialize("&a[MineMizer] Spawner mevcut yığına eklendi! (x" + (currentStack + 1) + ")");
                                event.getPlayer().sendMessage(msg);
                                return;
                            }
                        }
                    }
                }
            }
        }

        setStackCount(newSpawner, 1);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawnerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        if (block.getState() instanceof CreatureSpawner spawner) {
            int currentStack = getStackCount(spawner);

            if (currentStack > 1) {
                setStackCount(spawner, currentStack - 1);
                event.setCancelled(true);
                var msg = LegacyComponentSerializer.legacyAmpersand().deserialize("&e[MineMizer] Spawner yığınından 1 adet kırıldı. (Kalan: x" + (currentStack - 1) + ")");
                event.getPlayer().sendMessage(msg);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        if (!plugin.getConfig().getBoolean("spawner-stacker.multiply-spawns", true)) return;

        CreatureSpawner spawner = event.getSpawner();
        if (spawner != null) {
            int stackCount = getStackCount(spawner);

            if (stackCount > 1) {
                int extraMobs = stackCount - 1;
                EntityType type = spawner.getSpawnedType();
                if (type != null) {
                    for (int i = 0; i < extraMobs; i++) {
                        event.getLocation().getWorld().spawnEntity(event.getLocation(), type);
                    }
                }
            }
        }
    }

    private int getStackCount(CreatureSpawner spawner) {
        return spawner.getPersistentDataContainer().getOrDefault(stackKey, PersistentDataType.INTEGER, 1);
    }

    private void setStackCount(CreatureSpawner spawner, int count) {
        spawner.getPersistentDataContainer().set(stackKey, PersistentDataType.INTEGER, count);
        spawner.update();
    }
}