package de.smptools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

public class GroupCommand implements CommandExecutor, TabCompleter {

    private final SMPTools plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    public GroupCommand(SMPTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "add":
                handleAdd(sender, args);
                break;
            case "remove":
                handleRemove(sender, args);
                break;
            case "clear":
            case "leave":
                handleLeave(sender);
                break;
            case "join":
                if (args.length < 2) {
                    sender.sendMessage(miniMessage.deserialize("<red>Usage: /group join <name>"));
                    return true;
                }
                handleJoin(sender, args[1]);
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("smptools.admin")) {
            sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("messages.no-permission", "<red>No permission.")));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(miniMessage.deserialize("<red>Usage: /group add <name> <color>"));
            return;
        }

        String name = args[1];
        String colorName = args[2].toLowerCase();
        
        String display = "<" + colorName + ">[" + name + "]</" + colorName + ">";

        // Verify color format by checking if it produces styling
        try {
             Component test = miniMessage.deserialize(display);
             String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(test);
             if (plain.contains("<" + colorName + ">")) {
                 sender.sendMessage(miniMessage.deserialize("<red>Invalid color name: <color>. Try standard colors like 'aqua', 'blue', 'red' or hex codes.", Placeholder.parsed("color", args[2])));
                 return;
             }
        } catch (Exception e) {
             sender.sendMessage(miniMessage.deserialize("<red>Invalid color format."));
             return;
        }

        if (plugin.getGroupManager().createGroup(name, display)) {
            sender.sendMessage(miniMessage.deserialize("<green>Group <name> created with color <color>.", 
                Placeholder.parsed("name", name),
                Placeholder.parsed("color", colorName)
            ));
        } else {
            sender.sendMessage(miniMessage.deserialize("<red>Group <name> already exists.", Placeholder.parsed("name", name)));
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("smptools.admin")) {
            sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("messages.no-permission", "<red>No permission.")));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize("<red>Usage: /group remove <name>"));
            return;
        }

        String name = args[1];
        if (plugin.getGroupManager().deleteGroup(name)) {
             sender.sendMessage(miniMessage.deserialize("<green>Group <name> removed.", Placeholder.parsed("name", name)));
             // Update all players to remove the old group from tablist/nametag immediately
             plugin.updateAllPlayers();
        } else {
             sender.sendMessage(miniMessage.deserialize("<red>Group <name> does not exist.", Placeholder.parsed("name", name)));
        }
    }

    private void handleLeave(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command."));
            return;
        }
        player.getPersistentDataContainer().remove(plugin.getGroupKey());
        plugin.updateTabList(player);
        player.sendMessage(miniMessage.deserialize("<yellow>Group removed."));
    }

    private void handleJoin(CommandSender sender, String groupName) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command."));
            return;
        }

        if (!plugin.getGroupManager().exists(groupName)) {
            player.sendMessage(miniMessage.deserialize("<red>Group not found."));
            return;
        }

        // Set group
        player.getPersistentDataContainer().set(plugin.getGroupKey(), PersistentDataType.STRING, groupName);
        plugin.updateTabList(player);

        String display = plugin.getGroupManager().getGroupDisplay(groupName);
        Component groupComp = miniMessage.deserialize(display);
        
        player.sendMessage(miniMessage.deserialize("<green>You joined group <group>.", Placeholder.component("group", groupComp)));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize("<gray>Usage: /group <join|leave> [name]"));
        if (sender.hasPermission("smptools.admin")) {
            sender.sendMessage(miniMessage.deserialize("<gray>Admin: /group add <name> <color> | /group remove <name>"));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("join");
            options.add("leave");
            options.add("clear");
            
            if (sender.hasPermission("smptools.admin")) {
                options.add("add");
                options.add("remove");
            }
            
            String current = args[0].toLowerCase();
            List<String> result = new ArrayList<>();
            for (String opt : options) {
                if (opt.startsWith(current)) {
                    result.add(opt);
                }
            }
            return result;
        }
        
        // Suggest colors for /group add <name> [color]
        if (args.length == 3 && args[0].equalsIgnoreCase("add") && sender.hasPermission("smptools.admin")) {
            String current = args[2].toLowerCase();
            List<String> colors = new ArrayList<>(List.of(
                "dark_red", "red", "gold", "yellow", "dark_green", "green",
                "aqua", "dark_aqua", "dark_blue", "blue", "light_purple", "dark_purple",
                "white", "gray", "dark_gray", "black"
            ));
            
            List<String> result = new ArrayList<>();
            for (String color : colors) {
                if (color.startsWith(current)) {
                    result.add(color);
                }
            }
            return result;
        }
        
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("join")) {
                 return new ArrayList<>(plugin.getGroupManager().getGroups());
            }
        }
        
        return List.of();
    }
}
