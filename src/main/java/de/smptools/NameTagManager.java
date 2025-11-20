package de.smptools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NameTagManager {

    private final SMPTools plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Scoreboard scoreboard;

    public NameTagManager(SMPTools plugin) {
        this.plugin = plugin;
        // Use the main scoreboard to be compatible with vanilla teams if possible.
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public void updateNameTag(Player player) {
        String statusId = player.getPersistentDataContainer().get(plugin.getStatusKey(), PersistentDataType.STRING);
        String groupId = player.getPersistentDataContainer().get(plugin.getGroupKey(), PersistentDataType.STRING);

        // Use a unique team name per player. 
        // 1.21 supports long team names, so we don't need to truncate to 16 chars anymore.
        // We use a prefix "st_" to avoid collisions with other plugins.
        String teamName = "st_" + player.getName();

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        
        // Add player to their team
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }

        Component finalPrefix = Component.empty();

        // 1. Append Status
        if (statusId != null && !statusId.isEmpty()) {
            String displayRaw = plugin.getConfig().getString("status.options." + statusId + ".display");
            if (displayRaw != null) {
                String formatStr = plugin.getConfig().getString("tablist.status-format", "<status> "); 
                Component statusComp = miniMessage.deserialize(displayRaw);
                finalPrefix = finalPrefix.append(miniMessage.deserialize(formatStr, Placeholder.component("status", statusComp)));
            }
        }

        // 2. Append Group
        if (groupId != null && !groupId.isEmpty() && plugin.getGroupManager().exists(groupId)) {
            String displayRaw = plugin.getGroupManager().getGroupDisplay(groupId);
            String formatStr = plugin.getConfig().getString("tablist.group-format", "<group> ");
            Component groupComp = miniMessage.deserialize(displayRaw);
            finalPrefix = finalPrefix.append(miniMessage.deserialize(formatStr, Placeholder.component("group", groupComp)));
        }

        // Set the prefix
        team.prefix(finalPrefix);
    }

    public void removePlayer(Player player) {
        String teamName = "st_" + player.getName();
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            team.unregister();
        }
    }
}
