package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

public class MobStackerListener implements Listener {

    private final MineMizer plugin;
    private final NamespacedKey stackKey;

    public MobStackerListener(MineMizer plugin) {
        this.plugin = plugin;
        this.stackKey = new NamespacedKey(plugin, "stack_amount");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Player || entity.getType().name().contains("ARMOR_STAND")) return;

        int maxStack = plugin.getConfig().getInt("mob-stacker.max-stack", 50);

        // Bulunduğu Chunk'taki TÜM varlıkları tara (Mesafe sınırı yok!)
        for (Entity nearby : entity.getChunk().getEntities()) {
            if (nearby.getType() == entity.getType() && nearby instanceof LivingEntity target) {
                if (target.isDead() || target.equals(entity)) continue;

                int targetSize = getStackSize(target);
                if (targetSize < maxStack) {
                    event.setCancelled(true);
                    setStackSize(target, targetSize + 1);
                    updateName(target);
                    return;
                }
            }
        }

        setStackSize(entity, 1);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        int currentSize = getStackSize(entity);

        if (currentSize > 1) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                LivingEntity respawned = (LivingEntity) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
                setStackSize(respawned, currentSize - 1);
                updateName(respawned);
            });
        }
    }

    private int getStackSize(LivingEntity entity) {
        return entity.getPersistentDataContainer().getOrDefault(stackKey, PersistentDataType.INTEGER, 1);
    }

    private void setStackSize(LivingEntity entity, int amount) {
        entity.getPersistentDataContainer().set(stackKey, PersistentDataType.INTEGER, amount);
    }

    private void updateName(LivingEntity entity) {
        int count = getStackSize(entity);
        if (count > 1) {
            String rawTypeName = entity.getType().name().replace("_", " ").toLowerCase();
            String cleanName = rawTypeName.substring(0, 1).toUpperCase() + rawTypeName.substring(1);

            entity.customName(Component.text("§e" + cleanName + " §7x" + count));
            entity.setCustomNameVisible(true);
        } else {
            entity.customName(null);
            entity.setCustomNameVisible(false);
        }
    }
}