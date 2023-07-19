package me.albus.grapplinghook.Listener;

import me.albus.grapplinghook.GrapplingHook;
import me.albus.grapplinghook.Utils.CooldownManager;
import me.albus.grapplinghook.Utils.Notify;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

        GrapplingHook grapplingHook = GrapplingHook.getInstance();

        Notify notify = grapplingHook.getNotify();
        
        config = grapplingHook.config().get();

        CooldownManager cooldownManager = grapplingHook.getCooldownManager();

        if (!player.hasPermission("gh.player") && !player.hasPermission("gh.admin") && !player.isOp()) {
            player.sendMessage(notify.chatMessage("permission"));
            event.setCancelled(true);
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(grapplingHook, "hook");
        String value = container.get(key, PersistentDataType.STRING);

        if(value == null) {
            return;
        }

        if(!value.equalsIgnoreCase("hook")) {
            return;
        }

        if(cooldownManager.hasCooldown(player)) {
            long timeleft = cooldownManager.getRemainingTime(player);
            if(config.getBoolean("Settings.messages.cooldown.enabled")) {
                String title = config.getString("Settings.messages.cooldown.title");
                String subtitle;
                if(timeleft > 0) {
                    subtitle = config.getString("Settings.messages.cooldown.subtitle").replace("%this%", String.valueOf(timeleft));
                } else {
                    subtitle = config.getString("Settings.messages.cooldown.subtitle").replace("%this%", notify.message("cooldown_less"));
                }
                player.sendTitle(notify.color(title), notify.color(subtitle), 15, 50, 15);
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

            if (config.getBoolean("Settings.uses.enabled")) {
                PersistentDataContainer uses = meta.getPersistentDataContainer();
                NamespacedKey useKey = new NamespacedKey(grapplingHook, "uses");

                if (uses.has(useKey, PersistentDataType.INTEGER)) {
                    int used = uses.getOrDefault(useKey, PersistentDataType.INTEGER, 40);

                    if (used > 1) {
                        uses.set(useKey, PersistentDataType.INTEGER, used - 1);
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add(notify.message("stats_uses").replace("%this%", String.valueOf(used - 1)));
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    } else {
                        player.getInventory().removeItem(item);
                        if(config.getBoolean("Settings.uses.sound.enabled")) {
                            player.playSound(player.getLocation(), Sound.valueOf(config.getString("Settings.uses.sound.name")), 1, 1);
                        }
                        if(config.getBoolean("Settings.uses.message.enabled")) {
                            player.sendMessage(notify.chatMessage("break"));
                        }
                    }
                }
            }


            if (config.getBoolean("Settings.sound.enabled")) {
                player.playSound(player.getLocation(), Sound.valueOf(config.getString("Settings.sound.name")), 1, 1);
            }

            if (config.getBoolean("Settings.messages.title.enabled")) {
                player.sendTitle("", notify.color(config.getString("Settings.messages.title.message")), 15, 50, 15);
            }

            if (config.getBoolean("Settings.messages.actionbar.enabled")) {
                String actionbar = config.getString("Settings.messages.actionbar.message");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(notify.color(actionbar)));
            }
        }
    }

}
