package de.smptools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StatusCommand implements CommandExecutor, TabCompleter {

    private final SMPTools plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public StatusCommand(SMPTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command."));
            return true;
        }

        if (args.length == 0) {
            // Show help or list
            sendHelp(player);
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("clear")) {
            player.getPersistentDataContainer().remove(plugin.getStatusKey());
            plugin.updateTabList(player); // Update tab
            
            String msg = plugin.getConfig().getString("status.messages.cleared", "<yellow>Status cleared.");
            player.sendMessage(miniMessage.deserialize(msg));
            return true;
        }

        // Check if status exists in config
        if (plugin.getConfig().getConfigurationSection("status.options") == null || 
            !plugin.getConfig().getConfigurationSection("status.options").contains(action)) {
            
            String msg = plugin.getConfig().getString("status.messages.invalid", "<red>Invalid status.");
            String available = String.join(", ", getAvailableStatuses(player));
            
            player.sendMessage(miniMessage.deserialize(msg.replace("<available>", available)));
            return true;
        }

        // Check permission for this specific status
        String permission = plugin.getConfig().getString("status.options." + action + ".permission");
        if (permission != null && !player.hasPermission(permission)) {
            String msg = plugin.getConfig().getString("messages.no-permission", "<red>No permission.");
            player.sendMessage(miniMessage.deserialize(msg));
            return true;
        }

        // Set status
        player.getPersistentDataContainer().set(plugin.getStatusKey(), PersistentDataType.STRING, action);
        plugin.updateTabList(player); // Update tab

        String displayRaw = plugin.getConfig().getString("status.options." + action + ".display", action);
        String msg = plugin.getConfig().getString("status.messages.set", "<green>Status set.");
        
        // We replace <status> in the message with the formatted status
        Component statusComponent = miniMessage.deserialize(displayRaw);
        
        // Let's use TagResolver for better component insertion
        player.sendMessage(miniMessage.deserialize(msg, net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component("status", statusComponent)));

        return true;
    }

    private void sendHelp(Player player) {
        String available = String.join(", ", getAvailableStatuses(player));
        player.sendMessage(miniMessage.deserialize("<gray>Usage: /status <" + available + "|clear>"));
    }

    private List<String> getAvailableStatuses(Player player) {
        List<String> available = new ArrayList<>();
        if (plugin.getConfig().getConfigurationSection("status.options") != null) {
            Set<String> keys = plugin.getConfig().getConfigurationSection("status.options").getKeys(false);
            for (String key : keys) {
                String perm = plugin.getConfig().getString("status.options." + key + ".permission");
                if (perm == null || player.hasPermission(perm)) {
                    available.add(key);
                }
            }
        }
        return available;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            List<String> options = new ArrayList<>(getAvailableStatuses(player));
            options.add("clear");
            
            String current = args[0].toLowerCase();
            List<String> result = new ArrayList<>();
            for (String opt : options) {
                if (opt.startsWith(current)) {
                    result.add(opt);
                }
            }
            return result;
        }
        return List.of();
    }
}
