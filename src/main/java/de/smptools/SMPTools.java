package de.smptools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class SMPTools extends JavaPlugin {

    private static SMPTools instance;
    private NamespacedKey statusKey;
    private NamespacedKey groupKey;
    private NamespacedKey sitKey;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private GroupManager groupManager;
    private NameTagManager nameTagManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        statusKey = new NamespacedKey(this, "status");
        groupKey = new NamespacedKey(this, "group");
        sitKey = new NamespacedKey(this, "sit");
        
        groupManager = new GroupManager(this);
        nameTagManager = new NameTagManager(this);

        getCommand("status").setExecutor(new StatusCommand(this));
        getCommand("group").setExecutor(new GroupCommand(this));
        getCommand("groupchat").setExecutor(new GroupChatCommand(this));
        getCommand("rules").setExecutor(new RulesCommand(this));
        getCommand("sit").setExecutor(new SitCommand(this));
        getCommand("ping").setExecutor(new PingCommand(this));
        
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new QuitListener(this), this);
        getServer().getPluginManager().registerEvents(new SitListener(this), this);
        
        getLogger().info("SMPTools enabled!");
    }

    @Override
    public void onDisable() {
        // Clean up all teams on disable to avoid clutter if server restarts/reloads
        if (nameTagManager != null) {
            for (Player player : getServer().getOnlinePlayers()) {
                nameTagManager.removePlayer(player);
            }
        }
        getLogger().info("SMPTools disabled!");
    }

    public static SMPTools getInstance() {
        return instance;
    }

    public NamespacedKey getStatusKey() {
        return statusKey;
    }
    
    public NamespacedKey getGroupKey() {
        return groupKey;
    }
    
    public NamespacedKey getSitKey() {
        return sitKey;
    }
    
    public GroupManager getGroupManager() {
        return groupManager;
    }
    
    public NameTagManager getNameTagManager() {
        return nameTagManager;
    }

    public void updateTabList(Player player) {
        String statusId = player.getPersistentDataContainer().get(statusKey, PersistentDataType.STRING);
        String groupId = player.getPersistentDataContainer().get(groupKey, PersistentDataType.STRING);

        Component finalPrefix = Component.empty();

        // 1. Append Status
        if (statusId != null && !statusId.isEmpty()) {
            String displayRaw = getConfig().getString("statuses." + statusId + ".display");
            if (displayRaw != null) {
                String formatStr = getConfig().getString("status-tab-format", getConfig().getString("tab-format", "<status> ")); // fallback for backward compat
                Component statusComp = miniMessage.deserialize(displayRaw);
                finalPrefix = finalPrefix.append(miniMessage.deserialize(formatStr, Placeholder.component("status", statusComp)));
            }
        }

        // 2. Append Group
        if (groupId != null && !groupId.isEmpty() && groupManager.exists(groupId)) {
            String displayRaw = groupManager.getGroupDisplay(groupId);
            String formatStr = getConfig().getString("group-tab-format", "<group> ");
            Component groupComp = miniMessage.deserialize(displayRaw);
            finalPrefix = finalPrefix.append(miniMessage.deserialize(formatStr, Placeholder.component("group", groupComp)));
        }
        
        // Update Player List Name (Tab)
        if (finalPrefix.equals(Component.empty())) {
            player.playerListName(null);
        } else {
            player.playerListName(finalPrefix.append(player.name()));
        }
        
        // Update NameTag (Over Head)
        nameTagManager.updateNameTag(player);
    }

    public void updateAllPlayers() {
        for (Player player : getServer().getOnlinePlayers()) {
            updateTabList(player);
        }
    }
}
