package de.smptools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class JoinListener implements Listener {

    private final SMPTools plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public JoinListener(SMPTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.updateTabList(player);
        
        // Send MOTD
        List<String> motd = plugin.getConfig().getStringList("motd");
        if (motd != null && !motd.isEmpty()) {
            for (String line : motd) {
                // Deserialize with player name placeholder support
                player.sendMessage(miniMessage.deserialize(line, Placeholder.parsed("player", player.getName())));
            }
        }

        // Send Status Reminder
        String statusId = player.getPersistentDataContainer().get(plugin.getStatusKey(), PersistentDataType.STRING);

        if (statusId != null && !statusId.isEmpty()) {
            // Player has a status
            String displayRaw = plugin.getConfig().getString("status.options." + statusId + ".display");
            if (displayRaw != null) {
                String msg = plugin.getConfig().getString("status.messages.reminder-active");
                if (msg != null && !msg.isEmpty()) {
                    Component statusComponent = miniMessage.deserialize(displayRaw);
                    player.sendMessage(miniMessage.deserialize(msg, Placeholder.component("status", statusComponent)));
                }
            }
        } else {
            // Player has NO status
            String msg = plugin.getConfig().getString("status.messages.reminder-none");
            if (msg != null && !msg.isEmpty()) {
                player.sendMessage(miniMessage.deserialize(msg));
            }
        }
    }
}
