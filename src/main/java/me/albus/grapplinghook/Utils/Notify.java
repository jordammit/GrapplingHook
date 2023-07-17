package me.albus.grapplinghook.Utils;

import me.albus.grapplinghook.GrapplingHook;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Notify {

    private File file;

    private YamlConfiguration config;

    public void setup() {
        this.file = new File(GrapplingHook.getInstance().getDataFolder(), "messages.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("Could not generate 'messages.yml'. - Disabling plugin: " + e.getMessage());
                Bukkit.getServer().getPluginManager().disablePlugin(GrapplingHook.getInstance());
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    private final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";

    public String color(String text) {
        String[] texts = text.split(String.format(WITH_DELIMITER, "&"));

        StringBuilder finalText = new StringBuilder();

        for (int i = 0; i < texts.length; i++) {
            if (texts[i].equalsIgnoreCase("&")) {
                i++;
                if (texts[i].charAt(0) == '#') {
                    finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7)) + texts[i].substring(7));
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            } else {
                finalText.append(texts[i]);
            }
        }

        return finalText.toString();
    }

    public void initialize() {
        get().addDefault("plugin_prefix", "&#EBC7FF[&#BE8CFFGrappling Hook&#EBC7FF]");
        get().addDefault("ab_message", "&#EBC7FFWhoosh!");
        get().addDefault("permission", "&#EBC7FFYou do not have permission.");
        get().addDefault("syntax", "&#EBC7FFWrong syntax: &#BE8CFF%this%");
        get().addDefault("plugin_reloaded", "&#EBC7FFConfig and messages has been reloaded.");
        get().addDefault("set", "&#EBC7FFYou set &#BE8CFF%this%&a to &#EBC7FF%that%&#EBC7FF.");
        get().addDefault("missing_player", "&#EBC7FFCan't find &#BE8CFF%this%&#EBC7FF online.");
        get().addDefault("full_inventory", "&#BE8CFF%this%&#EBC7FF inventory is full");
        get().addDefault("cooldown_title", "&#EBC7FFCan't use this yet!");
        get().addDefault("cooldown_subtitle", "&#EBC7FFCooldown: &#BE8CFF%this%&#EBC7FF seconds.");
        get().addDefault("cooldown_less", "less than 1 second");

        //"&7&lGrappling Hook Used: &e&l" + uses + " times."
        get().addDefault("stats_uses", "&#EBC7FFThe Grappling Hook Has Been Used: &#BE8CFF%this% times.");

        get().addDefault("received", "&#BE8CFF%this%&#EBC7FF gave you a %plugin%");
        get().addDefault("gave", "&#EBC7FFYou gave &#BE8CFF%this%&#EBC7FF a %plugin%");
    }

    public FileConfiguration get() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        }
        catch (IOException e) {
            Bukkit.getServer().getLogger().warning("Failed to save messages.yml. Reason: " + e.getMessage());
        }
    }

    public String chatMessage(final String path) {
        return color(get().getString("plugin_prefix")) + color("&r ") + color(get().getString(path));
    }

    public String message(final String path) {
        return color(get().getString(path));
    }

}