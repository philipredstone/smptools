package de.smptools;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SitCommand implements CommandExecutor {

    private final SMPTools plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public SitCommand(SMPTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize("<red>Only players can sit."));
            return true;
        }

        // Toggle sit if already sitting
        if (player.getVehicle() != null) {
            if (player.getVehicle().getType() == EntityType.INTERACTION && 
                player.getVehicle().getPersistentDataContainer().has(plugin.getSitKey(), PersistentDataType.BYTE)) {
                player.getVehicle().removePassenger(player);
                return true;
            }
            
            String msg = plugin.getConfig().getString("sit.messages.already-riding", "<red>Du sitzt bereits woanders.");
            player.sendMessage(miniMessage.deserialize(msg));
            return true;
        }

        if (!player.isOnGround()) {
            String msg = plugin.getConfig().getString("sit.messages.not-on-ground", "<red>Du musst auf dem Boden stehen, um dich zu setzen.");
            player.sendMessage(miniMessage.deserialize(msg));
            return true;
        }

        Location location = player.getLocation().clone();
        location.setY(location.getY() + 0.05);

        World world = location.getWorld();

        // Spawn invisible interaction seat
        Interaction seat = (Interaction) world.spawnEntity(location, EntityType.INTERACTION);

        seat.setInteractionHeight(0.01f);
        seat.setInteractionWidth(0.01f);
        seat.setResponsive(false);
        seat.setSilent(true);
        seat.setInvulnerable(true);

        // Tag seat so listener can find it
        seat.getPersistentDataContainer().set(plugin.getSitKey(), PersistentDataType.BYTE, (byte) 1);

        // Make player sit
        seat.addPassenger(player);

        return true;
    }
}
