package me.albus.grapplinghook.Commands.list;

import me.albus.grapplinghook.Commands.SubCommand;
import me.albus.grapplinghook.GrapplingHook;
import me.albus.grapplinghook.Utils.Notify;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

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
        Notify notify = GrapplingHook.getInstance().getNotify();

        if(args.length != 2) {
            player.sendMessage(notify.chatMessage("syntax").replace("%this%", getSyntax()));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if(target == null || !target.isOnline()) {
            player.sendMessage(notify.chatMessage("missing_player").replace("%this%", args[1]));
            return;
        }

        boolean isInventoryFull = target.getInventory().firstEmpty() == -1;
        if(isInventoryFull) {
            player.sendMessage(notify.chatMessage("full_inventory").replace("%this%", target.getName()));
            return;
        }

        ItemStack item = GrapplingHook.getInstance().getHook();

        target.getInventory().addItem(item);
        target.sendMessage(notify.chatMessage("received").replace("%this%", player.getName()).replace("%plugin%", notify.message("plugin_prefix")));
        player.sendMessage(notify.chatMessage("gave").replace("%this%", target.getName()).replace("%plugin%", notify.message("plugin_prefix")));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
