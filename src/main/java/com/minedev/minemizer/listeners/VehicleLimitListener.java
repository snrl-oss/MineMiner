package com.minedev.minemizer.listeners;

import com.minedev.minemizer.MineMizer;
import org.bukkit.Chunk;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;

public class VehicleLimitListener implements Listener {

    private final MineMizer plugin;

    public VehicleLimitListener(MineMizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        Vehicle vehicle = event.getVehicle();
        Chunk chunk = vehicle.getChunk();
        int maxVehicles = plugin.getConfig().getInt("limits.vehicles.max-per-chunk", 5);

        long count = 0;
        for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
            if (entity instanceof Vehicle) count++;
        }

        if (count >= maxVehicles) {
            vehicle.remove();
        }
    }
}