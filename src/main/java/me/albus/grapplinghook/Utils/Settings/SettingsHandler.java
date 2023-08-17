package me.albus.grapplinghook.Utils.Settings;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SettingsHandler {

    private Map<Player, SettingsData> settingsMap = new HashMap<>();

    public void initialize(Player player) {
        if (!initialized(player)) {
            settingsMap.put(player, new SettingsData());
        }
    }

    public boolean initialized(Player player) {
        return settingsMap.containsKey(player);
    }

    public void updateToggleSound(Player player, boolean value) {
        if (initialized(player)) {
            settingsMap.get(player).setToggleSound(value);
        }
    }

    public void updateToggleBreakSound(Player player, boolean value) {
        if (initialized(player)) {
            settingsMap.get(player).setToggleBreakSound(value);
        }
    }

    public void updateCooldown(Player player, int value) {
        if (initialized(player)) {
            settingsMap.get(player).setCooldown(value);
        }
    }

    public void updateVelocity(Player player, int value) {
        if (initialized(player)) {
            settingsMap.get(player).setVelocity(value);
        }
    }

    // Get methods

    public boolean getToggleSound(Player player) {
        return initialized(player) ? settingsMap.get(player).getToggleSound() : false;
    }

    public boolean getToggleBreakSound(Player player) {
        return initialized(player) ? settingsMap.get(player).getToggleBreakSound() : false;
    }

    public int getCooldown(Player player) {
        return initialized(player) ? settingsMap.get(player).getCooldown() : 0;
    }

    public int getVelocity(Player player) {
        return initialized(player) ? settingsMap.get(player).getVelocity() : 0;
    }

    public void purge(Player player) {
        settingsMap.remove(player);
    }
}
