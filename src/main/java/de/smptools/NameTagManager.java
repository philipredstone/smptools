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

        // Determine sort key for the group to ensure players are grouped together
        String sortKey = "zzzz"; // Default for no group (puts them at the bottom)
        if (groupId != null && !groupId.isEmpty() && plugin.getGroupManager().exists(groupId)) {
            sortKey = groupId.toLowerCase();
        }

        // Status priority: '0' for active status (Live/Rec), '1' for none.
        // This puts players with status above everyone else.
        String statusPriority = "1";
        if (statusId != null && !statusId.isEmpty()) {
            statusPriority = "0";
        }

        // Use a unique team name per player that includes the group for sorting.
        // 1.21 supports long team names.
        // Format: st_<statusPriority>_<groupSortKey>_<playerName>
        String teamName = "st_" + statusPriority + "_" + sortKey + "_" + player.getName();

        // Check if player is already in a team that is NOT the new one (e.g. group changed)
        Team currentTeam = scoreboard.getEntryTeam(player.getName());
        if (currentTeam != null && !currentTeam.getName().equals(teamName)) {
            // If it's one of our teams, remove/unregister it
            if (currentTeam.getName().startsWith("st_")) {
                currentTeam.unregister();
            } else {
                // If it's a vanilla team or other plugin team, just remove the player from it
                currentTeam.removeEntry(player.getName());
            }
        }

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
        // Look up the team the player is currently in
        Team team = scoreboard.getEntryTeam(player.getName());
        if (team != null && team.getName().startsWith("st_")) {
            team.unregister();
        }
    }
}
