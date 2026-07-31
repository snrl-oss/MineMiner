package com.minedev.minemizer.tasks;

import com.minedev.minemizer.MineMizer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearLagTask extends BukkitRunnable {

    private final MineMizer plugin;

    public ClearLagTask(MineMizer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        executeClear(plugin);
    }

    public static int executeClear(MineMizer plugin) {
        int clearedCount = 0;

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item item) {
                    // İsteğe Bağlı Koruma: Özel ismi olan veya silinmez eşyaları atla
                    if (item.getItemStack().hasItemMeta() && item.getItemStack().getItemMeta().hasDisplayName()) {
                        continue;
                    }

                    entity.remove();
                    clearedCount++;
                }
            }
        }

        // Renk kodlarını Adventure API standartlarına uygun dönüştürme
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&bMineMizer&8] ");
        String message = plugin.getConfig().getString("messages.clearlag-cleared", "&eYerdeki &c%count% &eadet esya temizlendi!")
                .replace("%count%", String.valueOf(clearedCount));

        // LegacyComponentSerializer kullanarak '§' veya '&' renk karmaşasını önlüyoruz
        var formattedMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + message);
        Bukkit.broadcast(formattedMessage);

        return clearedCount;
    }
}