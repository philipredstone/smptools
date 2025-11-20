package de.smptools;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private final SMPTools plugin;

    public QuitListener(SMPTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Clean up the scoreboard team when player leaves
        plugin.getNameTagManager().removePlayer(event.getPlayer());
    }
}
