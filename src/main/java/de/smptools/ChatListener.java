package de.smptools;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public class ChatListener implements Listener {

    private final SMPTools plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ChatListener(SMPTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String statusId = player.getPersistentDataContainer().get(plugin.getStatusKey(), PersistentDataType.STRING);
        String groupId = player.getPersistentDataContainer().get(plugin.getGroupKey(), PersistentDataType.STRING);

        Component finalPrefix = Component.empty();
        boolean hasPrefix = false;

        // 1. Append Status
        if (statusId != null && !statusId.isEmpty()) {
            String displayRaw = plugin.getConfig().getString("status.options." + statusId + ".display");
            if (displayRaw != null) {
                // Use status-chat-format
                String formatStr = plugin.getConfig().getString("chat.status-format", "<status> ");
                Component statusComp = miniMessage.deserialize(displayRaw);
                finalPrefix = finalPrefix.append(miniMessage.deserialize(formatStr, Placeholder.component("status", statusComp)));
                hasPrefix = true;
            }
        }

        // 2. Append Group
        if (groupId != null && !groupId.isEmpty() && plugin.getGroupManager().exists(groupId)) {
            String displayRaw = plugin.getGroupManager().getGroupDisplay(groupId);
            String formatStr = plugin.getConfig().getString("chat.group-format", "<group> ");
            Component groupComp = miniMessage.deserialize(displayRaw);
            finalPrefix = finalPrefix.append(miniMessage.deserialize(formatStr, Placeholder.component("group", groupComp)));
            hasPrefix = true;
        }

        if (!hasPrefix) {
            return;
        }

        Component prefixToUse = finalPrefix;
        ChatRenderer oldRenderer = event.renderer();

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            Component rendered = oldRenderer.render(source, sourceDisplayName, message, viewer);
            return prefixToUse.append(rendered);
        });
    }
}
