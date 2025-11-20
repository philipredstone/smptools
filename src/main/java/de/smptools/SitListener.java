package de.smptools;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.persistence.PersistentDataType;

public class SitListener implements Listener {

    private final SMPTools plugin;

    public SitListener(SMPTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        Entity vehicle = event.getDismounted();
        Entity entity = event.getEntity();

        if (vehicle instanceof Interaction) {
            NamespacedKey key = plugin.getSitKey();
            if (vehicle.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {

                vehicle.remove(); // Remove seat

                // Move player up so they don’t clip into the ground
                Location loc = entity.getLocation();
                loc.setY(loc.getY() + 1.0);
                entity.teleport(loc);
            }
        }
    }
}
