package de.smptools;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class GroupChatCommand implements CommandExecutor {

    private final SMPTools plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public GroupChatCommand(SMPTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(miniMessage.deserialize("<red>Usage: /g <message>"));
            return true;
        }

        String groupId = player.getPersistentDataContainer().get(plugin.getGroupKey(), PersistentDataType.STRING);

        if (groupId == null || groupId.isEmpty() || !plugin.getGroupManager().exists(groupId)) {
            String msg = plugin.getConfig().getString("messages.not-in-group", "<red>You are not in a group.");
            player.sendMessage(miniMessage.deserialize(msg));
            return true;
        }

        String message = String.join(" ", args);
        String groupDisplayRaw = plugin.getGroupManager().getGroupDisplay(groupId);
        String format = plugin.getConfig().getString("chat.group-msg-format", "<dark_gray>[<group>] <gray><player>: <white><message>");

        // Pre-parse the format with placeholders
        var component = miniMessage.deserialize(format,
                Placeholder.component("group", miniMessage.deserialize(groupDisplayRaw)),
                Placeholder.parsed("player", player.getName()),
                Placeholder.parsed("message", message)
        );

        // Send to all players in same group
        for (Player target : Bukkit.getOnlinePlayers()) {
            String targetGroupId = target.getPersistentDataContainer().get(plugin.getGroupKey(), PersistentDataType.STRING);
            if (groupId.equals(targetGroupId)) {
                target.sendMessage(component);
            }
        }

        return true;
    }
}

