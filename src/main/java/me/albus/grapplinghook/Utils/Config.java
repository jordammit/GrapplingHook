package me.albus.grapplinghook.Utils;

import me.albus.grapplinghook.GrapplingHook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    private YamlConfiguration config;

    private final File file;

    public Config() {
        this.file = new File(GrapplingHook.getInstance().getDataFolder(), "config.yml");
    }

    public YamlConfiguration get() {
        return this.config;
    }

    public void setup() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch(IOException e) {
            Bukkit.getServer().getLogger().info("Couldn't save config.yml");
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

}
