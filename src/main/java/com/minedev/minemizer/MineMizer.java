package com.minedev.minemizer;

import com.minedev.minemizer.listeners.*;
import com.minedev.minemizer.tasks.ClearLagTask;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class MineMizer extends JavaPlugin implements CommandExecutor, TabCompleter {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Event Listener Registrations
        getServer().getPluginManager().registerEvents(new ChunkLimitListener(this), this);
        getServer().getPluginManager().registerEvents(new RedstoneLimiterListener(this), this);
        getServer().getPluginManager().registerEvents(new MobStackerListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemStackerListener(this), this);
        getServer().getPluginManager().registerEvents(new HopperLimitListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileCleanerListener(this), this);
        getServer().getPluginManager().registerEvents(new PistonLimiterListener(this), this);
        getServer().getPluginManager().registerEvents(new MobAiOptimizerListener(this), this);
        getServer().getPluginManager().registerEvents(new LiquidFlowListener(this), this);
        getServer().getPluginManager().registerEvents(new ExplosionLimiterListener(this), this);
        getServer().getPluginManager().registerEvents(new VehicleLimitListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerLimitListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerStackerListener(this), this);

        // ClearLag Zamanlayıcı
        if (getConfig().getBoolean("clearlag.enabled", true)) {
            long interval = getConfig().getLong("clearlag.interval-seconds", 300) * 20L;
            new ClearLagTask(this).runTaskTimer(this, interval, interval);
        }

        // Komut ve Tab Completer Tanımlama
        if (getCommand("minemizer") != null) {
            getCommand("minemizer").setExecutor(this);
            getCommand("minemizer").setTabCompleter(this);
        }

        getLogger().info("MineMizer v1.0 (Full Optimization Package) Aktif!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = getConfig().getString("messages.prefix", "&8[&bMineMizer&8] ").replace("&", "§");

        if (args.length > 0) {
            // RELOAD KOMUTU
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("minemizer.admin")) {
                    String noPerm = getConfig().getString("messages.no-permission", "&cBu komutu kullanmak icin yetkiniz yok.").replace("&", "§");
                    sender.sendMessage(Component.text(prefix + noPerm));
                    return true;
                }

                reloadConfig();
                String reloadMsg = getConfig().getString("messages.reload-success", "&aKonfigurasyon basariyla yenilendi!").replace("&", "§");
                sender.sendMessage(Component.text(prefix + reloadMsg));
                return true;
            }

            // STATUS KOMUTU
            if (args[0].equalsIgnoreCase("status")) {
                if (!sender.hasPermission("minemizer.admin")) {
                    String noPerm = getConfig().getString("messages.no-permission", "&cBu komutu kullanmak icin yetkiniz yok.").replace("&", "§");
                    sender.sendMessage(Component.text(prefix + noPerm));
                    return true;
                }

                double tps = getServer().getTPS()[0];
                int loadedChunks = 0;
                int totalEntities = 0;

                for (org.bukkit.World world : getServer().getWorlds()) {
                    loadedChunks += world.getLoadedChunks().length;
                    totalEntities += world.getEntities().size();
                }

                sender.sendMessage(Component.text("§8================ §bMineMizer Status §8================"));
                sender.sendMessage(Component.text("§eSunucu Anlık TPS: §a" + String.format("%.2f", tps)));
                sender.sendMessage(Component.text("§eYüklü Chunk Sayısı: §f" + loadedChunks));
                sender.sendMessage(Component.text("§eDünyadaki Toplam Entity: §f" + totalEntities));
                sender.sendMessage(Component.text("§8================================================"));
                return true;
            }

            // MANUEL CLEARLAG KOMUTU
            if (args[0].equalsIgnoreCase("clearlag")) {
                if (!sender.hasPermission("minemizer.admin")) {
                    String noPerm = getConfig().getString("messages.no-permission", "&cBu komutu kullanmak icin yetkiniz yok.").replace("&", "§");
                    sender.sendMessage(Component.text(prefix + noPerm));
                    return true;
                }

                ClearLagTask.executeClear(this);
                return true;
            }
        }

        sender.sendMessage(Component.text(prefix + "§eKullanım: /minemizer [reload | status | clearlag]"));
        return true;
    }

    // TAB TAMAMLAMA MANTIĞI (reload, status, clearlag)
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("minemizer.admin")) {
            String input = args[0].toLowerCase();
            if ("reload".startsWith(input)) completions.add("reload");
            if ("status".startsWith(input)) completions.add("status");
            if ("clearlag".startsWith(input)) completions.add("clearlag");
        }

        return completions;
    }
}