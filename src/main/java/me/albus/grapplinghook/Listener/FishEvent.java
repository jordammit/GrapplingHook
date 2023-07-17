package me.albus.grapplinghook.Listener;

import me.albus.grapplinghook.GrapplingHook;
import me.albus.grapplinghook.Utils.CooldownManager;
import me.albus.grapplinghook.Utils.Notify;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class FishEvent implements Listener {

    private YamlConfiguration config;

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        Notify notify = GrapplingHook.getInstance().getNotify();
        
        config = GrapplingHook.getInstance().config().get();

        CooldownManager cooldownManager = GrapplingHook.getInstance().getCooldownManager();

        if (!player.hasPermission("gh.player") && !player.hasPermission("gh.admin") && !player.isOp()) {
            player.sendMessage(notify.chatMessage("permission"));
            event.setCancelled(true);
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(GrapplingHook.getInstance(), "hook");
        String value = container.get(key, PersistentDataType.STRING);

        if(value == null) {
            return;
        }

        if(!value.equalsIgnoreCase("hook")) {
            return;
        }

        if(cooldownManager.hasCooldown(player)) {
            long timeleft = cooldownManager.getRemainingTime(player);
            String title = notify.message("cooldown_title");
            if(timeleft > 0) {
                String subtitle = notify.message("cooldown_subtitle").replace("%this%", String.valueOf(timeleft));
                player.sendTitle(title,  subtitle, 15, 50, 15);
            } else {
                String subtitle = notify.message("cooldown_subtitle").replace("%this%", notify.message("cooldown_less"));
                player.sendTitle(title, subtitle, 15, 50, 15);
            }
            event.setCancelled(true);
            return;
        }

        if(event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.IN_GROUND || event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {

            int velocity = config.getInt("Settings.velocity");

            Location updatedLocation = event.getHook().getLocation().subtract(player.getLocation());

            player.setVelocity(updatedLocation.toVector().multiply(velocity));

            if (!player.hasPermission("gh.bypass.cooldown") && !player.hasPermission("gh.admin") && !player.isOp()) {
                cooldownManager.startCooldown(player);
            }

            if (config.getBoolean("Settings.stats.enabled")) {
                NamespacedKey stats_key = new NamespacedKey(GrapplingHook.getInstance(), "hook_stats");
                int uses;

                if (container.has(stats_key, PersistentDataType.INTEGER)) {
                    uses = container.get(stats_key, PersistentDataType.INTEGER);
                    uses = uses + 1;
                } else {
                    uses = 1;
                }

                container.set(stats_key, PersistentDataType.INTEGER, uses);
                ArrayList<String> lore = new ArrayList<>();
                lore.add(notify.message("stats_uses").replace("%this%", String.valueOf(uses)));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            if(config.getBoolean("Settings.sound.enabled")) {
                player.playSound(player.getLocation(), Sound.valueOf(config.getString("Settings.sound.name")), 1, 1);
            }
            player.sendTitle("", notify.message("ab_message"), 15, 50, 15);
        }
    }
}
