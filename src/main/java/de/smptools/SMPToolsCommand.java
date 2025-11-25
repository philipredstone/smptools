package de.smptools;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SMPToolsCommand implements CommandExecutor, TabCompleter {

    private final SMPTools plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public SMPToolsCommand(SMPTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("smptools.admin")) {
            sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("messages.no-permission", "<red>No permission.")));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getGroupManager().reload();
            // Update all players to apply new formats immediately
            plugin.updateAllPlayers();
            
            sender.sendMessage(miniMessage.deserialize("<green>SMPTools configuration reloaded!"));
            return true;
        }

        sender.sendMessage(miniMessage.deserialize("<gray>SMPTools v" + plugin.getPluginMeta().getVersion() + " by DeFyuse"));
        sender.sendMessage(miniMessage.deserialize("<gray>Usage: <yellow>/smptools reload"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("smptools.admin")) {
            if ("reload".startsWith(args[0].toLowerCase())) {
                return List.of("reload");
            }
        }
        return List.of();
    }
}

