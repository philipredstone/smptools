package de.smptools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class GroupManager {

    private final SMPTools plugin;
    private File file;
    private FileConfiguration config;

    public GroupManager(SMPTools plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "groups.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create groups.yml!");
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save groups.yml!");
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean createGroup(String name, String display) {
        if (config.contains("groups." + name.toLowerCase())) {
            return false;
        }
        config.set("groups." + name.toLowerCase() + ".display", display);
        save();
        return true;
    }
    
    public boolean deleteGroup(String name) {
        if (!config.contains("groups." + name.toLowerCase())) {
            return false;
        }
        config.set("groups." + name.toLowerCase(), null);
        save();
        return true;
    }

    public boolean exists(String name) {
        return config.contains("groups." + name.toLowerCase());
    }

    public String getGroupDisplay(String name) {
        return config.getString("groups." + name.toLowerCase() + ".display", name);
    }

    public Set<String> getGroups() {
        if (config.getConfigurationSection("groups") == null) {
            return Collections.emptySet();
        }
        return config.getConfigurationSection("groups").getKeys(false);
    }
}
