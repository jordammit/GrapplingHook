package me.albus.grapplinghook.Listener;

import me.albus.grapplinghook.GrapplingHook;
import me.albus.grapplinghook.Utils.Notify;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class JoinEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        YamlConfiguration config = GrapplingHook.getInstance().config().get();
        if(!config.getBoolean("Settings.give_on_join")) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.hasPermission("gh.player") && !player.hasPermission("gh.admin") && !player.isOp()) {
            return;
        }

        boolean isInventoryFull = player.getInventory().firstEmpty() == -1;
        if(isInventoryFull) {
            Bukkit.getLogger().info("[GrapplingHook] " + player.getName() + "'s inventory is full. could not give them the grappling hook.");
            return;
        }

        ItemStack item = GrapplingHook.getInstance().getHook();

        if(player.getInventory().contains(item)) {
            return;
        }

        Notify notify = GrapplingHook.getInstance().getNotify();

        player.getInventory().addItem(item);
        player.sendMessage(notify.chatMessage("received").replace("%this%", "Console").replace("%plugin%", notify.message("plugin_prefix")));
    }
}
