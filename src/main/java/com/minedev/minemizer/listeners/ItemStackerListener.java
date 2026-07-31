package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class ItemStackerListener implements Listener {

    private final MineMizer plugin;

    public ItemStackerListener(MineMizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item newItem = event.getEntity();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!newItem.isValid() || newItem.isDead()) return;

            ItemStack newItemStack = newItem.getItemStack();

            // Tüm Chunk içindeki eşyaları kontrol et
            for (org.bukkit.entity.Entity nearby : newItem.getChunk().getEntities()) {
                if (nearby instanceof Item existingItem && !existingItem.equals(newItem)) {
                    if (!existingItem.isValid() || existingItem.isDead()) continue;

                    ItemStack existingItemStack = existingItem.getItemStack();

                    if (existingItemStack.isSimilar(newItemStack)) {
                        int maxStackSize = existingItemStack.getMaxStackSize();
                        int totalAmount = existingItemStack.getAmount() + newItemStack.getAmount();

                        if (totalAmount <= maxStackSize) {
                            newItem.remove();
                            existingItemStack.setAmount(totalAmount);
                            existingItem.setItemStack(existingItemStack);
                            return;
                        } else {
                            existingItemStack.setAmount(maxStackSize);
                            newItemStack.setAmount(totalAmount - maxStackSize);
                            existingItem.setItemStack(existingItemStack);
                            newItem.setItemStack(newItemStack);
                        }
                    }
                }
            }
        }, 1L);
    }
}