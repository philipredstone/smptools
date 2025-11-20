package de.smptools;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RulesCommand implements CommandExecutor {

    private final SMPTools plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public RulesCommand(SMPTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> rules = plugin.getConfig().getStringList("rules");
        
        if (rules == null || rules.isEmpty()) {
            sender.sendMessage(miniMessage.deserialize("<red>Rules are not configured."));
            return true;
        }

        for (String line : rules) {
            sender.sendMessage(miniMessage.deserialize(line, Placeholder.parsed("player", sender.getName())));
        }

        return true;
    }
}

