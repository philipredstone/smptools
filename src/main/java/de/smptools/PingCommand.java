package de.smptools;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements CommandExecutor {

    private final SMPTools plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public PingCommand(SMPTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        int ping = player.getPing();
        String messageFormat = plugin.getConfig().getString("ping.message", "<gray>Dein Ping beträgt: <green><ping>ms");
        
        player.sendMessage(miniMessage.deserialize(messageFormat, Placeholder.parsed("ping", String.valueOf(ping))));

        return true;
    }
}

