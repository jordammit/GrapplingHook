package me.albus.grapplinghook.Commands.list;

import me.albus.grapplinghook.Commands.SubCommand;
import me.albus.grapplinghook.GrapplingHook;
import me.albus.grapplinghook.Utils.Notify;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class give extends SubCommand {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getPermission() {
        return "gh.admin";
    }

    @Override
    public String getSyntax() {
        return "/grapplinghook give <player> <optional uses>";
    }

    @Override
    public void perform(Player player, String[] args) {
        GrapplingHook grapplingHook = GrapplingHook.getInstance();
        Notify notify = grapplingHook.getNotify();
        YamlConfiguration config = grapplingHook.config().get();

        if(args.length < 2 || args.length > 3) {
            player.sendMessage(notify.chatMessage("syntax").replace("%this%", getSyntax()));
            return;
        }

        if(args[1] == null || args[1].isEmpty()) {
            player.sendMessage(notify.chatMessage("syntax").replace("%this%", getSyntax()));
            return;
        }

        String name = args[1];

        Player target = Bukkit.getPlayer(name);

        if(target == null) {
            player.sendMessage(notify.chatMessage("syntax").replace("%this%", getSyntax()));
            return;
        }

        ItemStack item = grapplingHook.getHook();
        ItemMeta meta = item.getItemMeta();
        int uses;


        if(args.length == 2) {
            uses = config.getInt("Settings.uses.amount");
        } else if(args.length == 3) {
            if(grapplingHook.isInteger(args[2])) {
                uses = Integer.valueOf(args[2]);
            } else if(args[2].equalsIgnoreCase("random")) {
                uses = grapplingHook.getRandom();
            } else {
                player.sendMessage(notify.chatMessage("syntax").replace("%this%", getSyntax()));
                return;
            }
        } else {
            player.sendMessage(notify.chatMessage("syntax").replace("%this%", getSyntax()));
            return;
        }

        if(uses > 0 && config.getBoolean("Settings.uses.enabled")) {
            NamespacedKey counter = new NamespacedKey(GrapplingHook.getInstance(), "uses");
            meta.getPersistentDataContainer().set(counter, PersistentDataType.INTEGER, uses);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(notify.message("stats_uses").replace("%this%", String.valueOf(uses)));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        boolean isInventoryFull = target.getInventory().firstEmpty() == -1;
        if(isInventoryFull) {
            player.sendMessage(notify.chatMessage("full_inventory"));
            return;
        }

        target.getInventory().addItem(item);
        target.sendMessage(notify.chatMessage("received").replace("%this%", player.getName()).replace("%plugin%", notify.message("plugin_prefix")));
        player.sendMessage(notify.chatMessage("gave").replace("%this%", target.getName()).replace("%plugin%", notify.message("plugin_prefix")));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
