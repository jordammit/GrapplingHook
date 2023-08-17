package me.albus.grapplinghook.Utils;

import me.albus.grapplinghook.GrapplingHook;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public void startCooldown(Player player) {
        long cooldownDuration = GrapplingHook.getInstance().config().get().getLong("Settings.cooldown");
        long currentTime = System.currentTimeMillis();
        long cooldownExpiration = currentTime + (cooldownDuration * 1000); // Convert seconds to milliseconds
        cooldowns.put(player, cooldownExpiration);
    }

    public boolean hasCooldown(Player player) {
        if (!cooldowns.containsKey(player)) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        long cooldownExpiration = cooldowns.get(player);
        return currentTime < cooldownExpiration;
    }

    public long getRemainingTime(Player player) {
        if (!hasCooldown(player)) {
            return 0;
        }
        long currentTime = System.currentTimeMillis();
        long cooldownExpiration = cooldowns.get(player);
        return (cooldownExpiration - currentTime) / 1000;
    }
}